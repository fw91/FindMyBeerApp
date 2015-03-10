package com.msp.findMyBeer.Activities.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.msp.findMyBeer.Activities.UserProfileActivity;
import com.msp.findMyBeer.MyDDPState;
import com.msp.findMyBeer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


/**
 *  This Fragment is used to login with facebook
 *
 *  @author Florian Wirth, Florian Fincke, Cenk Canpolat
 */
public class FBFragment extends Fragment {

    private static final String TAG = "FBFragment";

    SharedPreferences fb_preferences;
    SharedPreferences user_preferences;
    final static String FB_PREFERENCES = "FBPreferences";
    final static String USER_PREFERENCES = "UserPreferences";
    final static String NAME_PREFERENCES = "userName";
    final static String FB_ID_PREFERENCES = "userFB_ID";
    final static String FB_ACCESS_TOKEN_PREFERENCES = "FBAccessToken";
    final static String FB_ACCESS_TOKEN_EXPIRES_PREFERENCES = "FBAccessTokenExpires";


    private String user_id, user_name;


    private UiLifecycleHelper uiHelper;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    public static FBFragment newInstance(String param1, String param2) {
        FBFragment fragment = new FBFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    public FBFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fb, container, false);
        user_preferences = getActivity().getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        fb_preferences = getActivity().getSharedPreferences(FB_PREFERENCES, Context.MODE_PRIVATE);


        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList(""));

        return view;
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            setUserToDB(session.getAccessToken());
            Log.i(TAG, "Logged in with token: " + session.getAccessToken());
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
            ((UserProfileActivity) getActivity()).loadLogoutFragment();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    /*
     *  This method automatically updates the Users details inside the SharedPreferences
     *  after completing the login
     *
     *  @param accessToken This is the unique accessToken for each user,
     *                     it gets stored in the SharedPreferences
     */
    private void setUserToDB(String accessToken) {
        final Session session = Session.getActiveSession();
        final String accessToken1 = accessToken;
        if (session != null && session.isOpened()) {
            // If the session is open, make an API call to get user data
            // and define a new callback to handle the response
            Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    // If the response is successful
                    if (session == Session.getActiveSession()) {
                        if (user != null) {

                            JSONObject json = new JSONObject();
                            try {

                                json.put("accessToken", accessToken1);
                                json.put("expiresAt", Session.getActiveSession().getExpirationDate());
                                json.put("id", user.getId().toString());
                                json.put("name", user.getName());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JSONObject update = new JSONObject();
                            SharedPreferences.Editor editFB = fb_preferences.edit();
                            if (!fb_preferences.contains(user.getId())) {
                                MyDDPState.getInstance().addFacebookToUser(json);
                                editFB.putBoolean(user.getId() + Session.getActiveSession().getExpirationDate().toString()
                                        , false);
                                editFB.putString(user.getId(), user.getId());
                                editFB.commit();
                            } else {
                                try {
                                    update.put("accessToken", accessToken1);
                                    update.put("expiresAt", Session.getActiveSession().getExpirationDate());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                MyDDPState.getInstance().updateFacebookToken(update);
                            }
                            SharedPreferences.Editor editor = user_preferences.edit();

                            editor.putBoolean("user_fb_logged_in", true);
                            editor.putString(NAME_PREFERENCES, user.getName());
                            editor.putString(FB_ID_PREFERENCES, user.getId());
                            editor.putString(FB_ACCESS_TOKEN_PREFERENCES, Session.getActiveSession().getAccessToken());
                            editor.putString(FB_ACCESS_TOKEN_EXPIRES_PREFERENCES, Session.getActiveSession().getExpirationDate().toString());
                            editor.commit();
                            try {
                                ((UserProfileActivity) getActivity()).loadLoginFragment();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            });
            Request.executeBatchAsync(request);
        }
    }
}
