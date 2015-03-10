package com.msp.findMyBeer.Activities.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.msp.findMyBeer.R;

/**
 * Create and inflate the UserProfile-Fragment
 *
 *  @author Cenk Canpolat
 */
public class UserProfileFragmentLogout extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_user_profile_logout,container,false);

        return v;
    }
}
