package com.msp.findMyBeer.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.msp.findMyBeer.MyDDPState;
import com.msp.findMyBeer.R;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 * @author Cenk Canpolat, Florian Wirth, Florian Fincke
 */
public class LoginActivity extends Activity {


    SharedPreferences user_preferences;
    SharedPreferences.Editor editor;

    private String loginToken;
    /**
     * broadcast receiver for DDP events
     */
    private BroadcastReceiver mReceiver;

    /**
     * bundle key for default email to populate the email key with
     */
    public static final String EXTRA_EMAIL = "login.extra.EMAIL";


    /** value for Username at the time of the register attempt */
    // changed -> username=email
    // private String mUsername;
    /**
     * value for email at the time of the login attempt
     */
    private String mEmail;
    /**
     * value for password at the time of the login attempt
     */
    private String mPassword;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    final static String USER_PREFERENCES = "UserPreferences";
    final static String TOKEN_PREFERENCES = "resumeToken";
    final static String EMAIL_PREFERENCES = "userEmail";


    /**
     * Handles creation of this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createCutomActionBarTitle();
        setContentView(R.layout.activity_login);


        user_preferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);

        // Set up the login form.
        mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
        mEmailView = (EditText) findViewById(R.id.email);
        mEmailView.setText(mEmail);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin(false);
                    return true;
                }
                return false;
            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(true);
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(false);
            }
        });

    }

    /**
     * Creation action menu items
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_forgot_password:
                View focusView = validateFields(false);
                if (focusView == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.dlg_password_sent)
                            .setTitle(R.string.action_forgot_password);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MyDDPState.getInstance().forgotPassword(mEmail);
                            finish();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called on resume of activity and initial startup
     */
    protected void onResume() {
        super.onResume();
        //initSingletons();
        // get ready to handle DDP events
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // display errors to the user
                Bundle bundle = intent.getExtras();
                showProgress(false);
                if (intent.getAction().equals(MyDDPState.MESSAGE_ERROR)) {
                    String message = bundle.getString(MyDDPState.MESSAGE_EXTRA_MSG);
                    showError("Login Error", message);
                } else if (intent.getAction().equals(MyDDPState.MESSAGE_CONNECTION)) {
                    int state = bundle.getInt(MyDDPState.MESSAGE_EXTRA_STATE);

                    if (state == MyDDPState.DDPSTATE.LoggedIn.ordinal()) {
                        // login complete, so we can close this login activity and go back
                        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                        finish();
                    }
                }
            }

        };
        // we want error messages
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(MyDDPState.MESSAGE_ERROR));
        // we want connection state change messages so we know we're logged in
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(MyDDPState.MESSAGE_CONNECTION));

        // show connection error if it happened
        if (MyDDPState.getInstance().getState() == MyDDPState.DDPSTATE.Closed) {
            showError("Connection Issue", "Error connecting to server...please try again");
            MyDDPState.getInstance().connectIfNeeded();    // try reconnecting
        }

        // Add a tiny delay, to make sure the connection is established (required for Token-Login)
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (MyDDPState.getInstance().getState() == MyDDPState.DDPSTATE.Connected)
        {
            if (MyDDPState.getInstance().getResumeToken() != null)
            {
                MyDDPState.getInstance().login(MyDDPState.getInstance().getResumeToken());
            }
        }
    }

    /**
     * Used to display error dialogs
     *
     * @param title title of dialog
     * @param msg   details of error
     */
    private void showError(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Called when pausing or rotating app
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(boolean registerUser) {
        View focusView = validateFields(registerUser);
        if (focusView == null) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);

            // since we're using websockets which are non-blocking,
            // we can do network stuff here instead of a task
            if (registerUser) {
                MyDDPState.getInstance().registerUser(mEmail, mEmail, mPassword);
                loginToken = MyDDPState.getInstance().getResumeToken();
                SharedPreferences.Editor editor = user_preferences.edit();
                editor.putString(TOKEN_PREFERENCES, MyDDPState.getInstance().getResumeToken());
                editor.putString(EMAIL_PREFERENCES, mEmail);
                editor.commit();
            } else {
                MyDDPState.getInstance().login(mEmail, mPassword);
                loginToken = MyDDPState.getInstance().getResumeToken();
                SharedPreferences.Editor editor = user_preferences.edit();
                editor.putString(TOKEN_PREFERENCES, MyDDPState.getInstance().getResumeToken());
                editor.putString(EMAIL_PREFERENCES, mEmail);
                editor.commit();
            }
        }
    }

    public View validateFields(boolean registerUser) {
        // Reset errors.
        //mUsernameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);


        // Store values at the time of the login attempt.
        //mUsername = mUsernameView.getText().toString();
        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();
        View focusView = null;


      /*  // Check for a valid username.
        if (registerUser){
            if (TextUtils.isEmpty(mUsername)) {
                mUsernameView.setError(getString(R.string.error_field_required));
                focusView = mUsernameView;
            }
        }*/

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
        } else if (!mEmail.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
        }
        if (focusView != null) {
            // There was an error; focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        return focusView;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void initSingletons() {
        MyDDPState.initInstance(getApplicationContext());
    }

    private void createCutomActionBarTitle(){
        this.getActionBar().setDisplayShowCustomEnabled(true);
        this.getActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.custom_action_bar, null);

        Typeface face =Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-M.ttf");
        ((TextView)v.findViewById(R.id.titleFragment1)).setTypeface(face);

        ((TextView)v.findViewById(R.id.titleFragment1)).setText("FindMyBeer - Login");

        //assign the view to the actionbar
        this.getActionBar().setCustomView(v);
    }
}