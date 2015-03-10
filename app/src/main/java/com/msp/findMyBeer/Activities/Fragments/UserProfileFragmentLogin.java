package com.msp.findMyBeer.Activities.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.msp.findMyBeer.Database.DatabaseHandler;
import com.msp.findMyBeer.R;

/**
 * Create and inflate the UserProfile-Fragment
 *
 *  @author Cenk Canpolat
 */
public class UserProfileFragmentLogin extends Fragment {

    DatabaseHandler DH;

    SharedPreferences user_preferences;
    final static String USER_PREFERENCES = "UserPreferences";
    final static String NAME_PREFERENCES = "userName";
    final static String FB_ID_PREFERENCES = "userFB_ID";
    final static String STATUS_PREFERENCES = "userStatus";
    final static String S_PEN_PREFERENCES = "userSubPending";
    final static String S_ACC_PREFERENCES = "userSubAccepted";



    TextView nameView, idView, statusView, sAcceptedView, sPendingView;

    @Override
    public void onResume() {
        super.onResume();
        nameView.setText(user_preferences.getString(NAME_PREFERENCES,"empty"));
        idView.setText(user_preferences.getString(FB_ID_PREFERENCES,"empty"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_user_profile_login,container,false);
        nameView = (TextView) v.findViewById(R.id.fb_name);
        idView = (TextView) v.findViewById(R.id.fb_ID);
        statusView = (TextView) v.findViewById(R.id.user_status);
        sAcceptedView = (TextView) v.findViewById(R.id.submits_accepted);
        sPendingView = (TextView) v.findViewById(R.id.submits_pending);
        user_preferences = getActivity().getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        Typeface face =Typeface.createFromAsset(getActivity().getAssets(),"fonts/Ubuntu-M.ttf");
        statusView.setTypeface(face);
        statusView.setTextColor(Color.BLACK);
        statusView.setText(getStatus(user_preferences.getInt(STATUS_PREFERENCES, 0)));
        sAcceptedView.setText(Integer.toString(user_preferences.getInt(S_ACC_PREFERENCES, 0)));
        sPendingView.setText(Integer.toString(user_preferences.getInt(S_PEN_PREFERENCES,0)));

        return v;
    }

    private String getStatus(int i) {
        String status = "Anonymer Alkoholiker";
        switch (i) {
            case 0: status = "Occasional drinker";
                break;
            case 1: status = "Brewer";
                break;
            case 2: status = "The mysteriour beer baron";
                break;
        }
        return status;
    }

}
