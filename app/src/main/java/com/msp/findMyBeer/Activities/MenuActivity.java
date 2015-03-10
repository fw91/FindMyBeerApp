package com.msp.findMyBeer.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.Session;
import com.msp.findMyBeer.MyDDPState;
import com.msp.findMyBeer.R;

/**
 *  The Menu Activity
 *
 *  @author Florian Wirth, Cenk Canpolat
 */
public class MenuActivity extends FragmentActivity {

    // Preferences of Application
    public static final String APP_PREFERENCES = "AppPreferences";
    SharedPreferences app_preferences;
    // Preferences of User
    public static final String USER_PREFERENCES = "UserPreferences";
    SharedPreferences user_preferences;
    // Preferences of User
    public static final String MAP_PREFERENCES = "MapPreferences";
    SharedPreferences map_preferences;

    final static String NAME_PREFERENCES = "userName";
    final static String FB_ID_PREFERENCES = "userFB_ID";


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        createCustomActionBarTitle();

        app_preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        user_preferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
        map_preferences = getSharedPreferences(MAP_PREFERENCES, MODE_PRIVATE);

        TextView userStatus  = (TextView)findViewById(R.id.menuStatus);

        ImageButton beerMe   = (ImageButton)findViewById(R.id.beerMe_btn);
        ImageButton submit   = (ImageButton)findViewById(R.id.submit_btn);
        ImageButton favorites = (ImageButton)findViewById(R.id.favorites_btn);
        ImageButton profile  = (ImageButton)findViewById(R.id.profile_btn);

        fillBanner();

        beerMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SubmitActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),FavoriteBSActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),UserProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_settings, menu);

        // if Feature is enabled in the Prefs -> check item
        // else don't
        menu.getItem(0).setChecked(app_preferences.getBoolean("showHints", true));

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.enable_hints:
                if (item.isChecked()) {
                    // un-check item -> change prefs to false -> reprint markers
                    item.setChecked(false);
                    app_preferences.edit().putBoolean("showHints", false).apply();
                } else {
                    // check item -> change prefs to true -> reprint markers
                    item.setChecked(true);
                    app_preferences.edit().putBoolean("showHints", true).apply();
                }
                break;
            case R.id.logout:
                MyDDPState.getInstance().logout();
                callFacebookLogout(getApplicationContext());
                app_preferences.edit().clear().apply();
                user_preferences.edit().clear().apply();
                map_preferences.edit().clear().apply();

                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                break;
            default:
                break;
        }
        return true;
    }


    /*
     *  LogOut from Facebook
     */
    public static void callFacebookLogout(Context context) {
        Session session = Session.getActiveSession();
        if (session != null) {

            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
                //clear your preferences if saved
            }
        } else {

            session = new Session(context);
            Session.setActiveSession(session);

            session.closeAndClearTokenInformation();
            //clear your preferences if saved

        }
    }


    /*
     *  Fill the Banner with user details
     */
    private void fillBanner()
    {
        Typeface face =Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-M.ttf");
        TextView userStatus  = (TextView)findViewById(R.id.menuStatus);

        userStatus.setTypeface(face);
        userStatus.setTextColor(Color.BLACK);

        userStatus.setText(getStatus(user_preferences.getInt("userStatus",0)));
    }

    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private String getStatus(int i) {
        String status = "Anonymer Alkoholiker";
        switch (i) {
            case 0: status = "Occasional drinker";
                break;
            case 1: status = "Brewer";
                break;
            case 2: status = "The mysterious beer baron";
                break;
        }
        return status;
    }


    /*
     *  Create own Action Bar Style
     */
    private void createCustomActionBarTitle(){
        this.getActionBar().setDisplayShowCustomEnabled(true);
        this.getActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.custom_action_bar, null);

        Typeface face =Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-M.ttf");
        ((TextView)v.findViewById(R.id.titleFragment1)).setTypeface(face);

        ((TextView)v.findViewById(R.id.titleFragment1)).setText("FindMyBeer");

        //assign the view to the actionbar
        this.getActionBar().setCustomView(v);
    }
}
