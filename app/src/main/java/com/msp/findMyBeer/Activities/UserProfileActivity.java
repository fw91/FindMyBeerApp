package com.msp.findMyBeer.Activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.msp.findMyBeer.Activities.Fragments.FBFragment;
import com.msp.findMyBeer.Activities.Fragments.UserProfileFragmentLogin;
import com.msp.findMyBeer.Activities.Fragments.UserProfileFragmentLogout;
import com.msp.findMyBeer.R;

/**
 *  The User-Profile Activity
 *
 *  @author Florian Wirth, Cenk Canpolat
 */
public class UserProfileActivity extends FragmentActivity {
    FBFragment fbFragment;
    UserProfileFragmentLogin fragmentLogin;
    UserProfileFragmentLogout fragmentLogout;

    SharedPreferences user_preferences;

    final static String USER_PREFERENCES = "UserPreferences";
    final static String STATUS_PREFERENCES = "userStatus";
    final static String S_PEN_PREFERENCES = "userSubPending";
    final static String S_ACC_PREFERENCES = "userSubAccepted";

    // Preferences of Application
    public static final String APP_PREFERENCES = "AppPreferences";
    SharedPreferences app_preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        createCustomActionBarTitle();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("newUserData"));

        user_preferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
        app_preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);


        fragmentLogin = new UserProfileFragmentLogin();
        fragmentLogout = new UserProfileFragmentLogout();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("newUserData"));

        if (user_preferences.getBoolean("user_fb_logged_in",false))
        {
            loadLoginFragment();
        }
        else
        {
            loadLogoutFragment();
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int userSubmitsPending = intent.getIntExtra("userSubmitsPending", 0);
            int userSubmitsAccepted = intent.getIntExtra("userSubmitsAccepted", 0);
            int userStatus = intent.getIntExtra("userStatus", 0);
            updateProfile(userSubmitsPending, userSubmitsAccepted, userStatus);
        }
    };

    private void updateProfile(int userSubmitsPending, int userSubmitsAccepted, int userStatus) {
        SharedPreferences.Editor editor = user_preferences.edit();
        editor.putInt(S_PEN_PREFERENCES, userSubmitsPending);
        editor.putInt(S_ACC_PREFERENCES, userSubmitsAccepted);
        editor.putInt(STATUS_PREFERENCES, userStatus);
        editor.apply();
    }


    public void loadLoginFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment newFragment = new UserProfileFragmentLogin();
        ft.replace(R.id.loginFrame, newFragment);
        ft.commit();
    }

    public void loadLogoutFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment newFragment = new UserProfileFragmentLogout();
        ft.replace(R.id.loginFrame, newFragment);
        ft.commit();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
        }

        return true;
    }

    private void createCustomActionBarTitle(){
        this.getActionBar().setDisplayShowCustomEnabled(true);
        this.getActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.custom_action_bar, null);

        Typeface face =Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-M.ttf");
        ((TextView)v.findViewById(R.id.titleFragment1)).setTypeface(face);

        ((TextView)v.findViewById(R.id.titleFragment1)).setText("User Profile");

        //assign the view to the actionbar
        this.getActionBar().setCustomView(v);
    }



}
