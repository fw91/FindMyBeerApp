package com.msp.findMyBeer.Activities.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.msp.findMyBeer.Activities.MenuActivity;
import com.msp.findMyBeer.Activities.SubmitActivity;
import com.msp.findMyBeer.Database.BeerSpot;
import com.msp.findMyBeer.Database.BeerSpotDays;
import com.msp.findMyBeer.MyDDPState;
import com.msp.findMyBeer.R;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 *  Submit Review
 *
 *  @author Cenk Canpolat und Florian Fincke
 */
public class SubmitFormFragment3 extends Fragment {
    TextView nameFinal, addressFinal, cityFinal, locTypeFinal, beerTypeFinal, beerPriceFinal, infoFinal;
    Button page3previous, submitButton;
    boolean cameraused;

    BeerSpot submitBeerSpotImage, submitBeerSpotTimes;
    BeerSpotDays finalDays;
    String finalName, finalAddress, finalCity, finalLocType, finalBeerType, finalBeerPrice, finalInfo;
    String base64String;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_submit_p3, container, false);

        nameFinal = (TextView) myFragmentView.findViewById(R.id.submitRevName);
        addressFinal = (TextView) myFragmentView.findViewById(R.id.submitRevAddress);
        cityFinal = (TextView) myFragmentView.findViewById(R.id.submitRevCity);
        locTypeFinal = (TextView) myFragmentView.findViewById(R.id.submitRevType);
        beerTypeFinal = (TextView) myFragmentView.findViewById(R.id.submitRevBeer);
        beerPriceFinal = (TextView) myFragmentView.findViewById(R.id.submitRevPrice);
        infoFinal = (TextView) myFragmentView.findViewById(R.id.submitRevInfo);

        page3previous = (Button) myFragmentView.findViewById(R.id.page3Back_btn);
        submitButton = (Button) myFragmentView.findViewById(R.id.submit_btn);

        page3previous.setOnClickListener(Page3PreviousButtonOnClickListener);
        submitButton.setOnClickListener(SubmitOnclickListener);


        String myTag = getTag();

        ((SubmitActivity) getActivity()).setTabOfSubmitFragment3(myTag);

        return myFragmentView;
    }

    public void setInput(String name, String address, String city, String locType, String beerType, String beerPrice, String info) {
        nameFinal.setText(name);
        addressFinal.setText(address);
        cityFinal.setText(city);
        locTypeFinal.setText(locType);
        beerTypeFinal.setText(beerType);
        beerPriceFinal.setText(beerPrice);
        infoFinal.setText(info);

        finalName = name;
        finalAddress = address;
        finalCity = city;
        finalLocType = locType;
        finalBeerType = beerType;
        finalBeerPrice = beerPrice;
        finalInfo = info;
    }

    public void setBase64String(String imageString) {
        base64String = imageString;
    }

    public void setBeerSpotDays(ArrayList<Integer> openDay, ArrayList<Integer> openTime, ArrayList<Integer> closeDay, ArrayList<Integer> closeTime) {
        finalDays = new BeerSpotDays();
        for (int i = 0; i < openDay.size(); i++) {
            finalDays.addDay(openDay.get(i), openTime.get(i), closeDay.get(i), closeTime.get(i));
        }
    }

    View.OnClickListener Page3PreviousButtonOnClickListener
            = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            if (cameraused) {
                String TabOfFragment3 = ((SubmitActivity) getActivity()).getTabOfSubmitFragment2();
                SubmitFormFragment2 fragment2 = (SubmitFormFragment2) getActivity().getSupportFragmentManager().findFragmentByTag(TabOfFragment3);
                fragment2.clearTimes();
                ViewPager aViewPager = ((SubmitActivity) getActivity()).mViewPager;
                aViewPager.setCurrentItem(0, true);
            } else {
                String TabOfFragment2 = ((SubmitActivity) getActivity()).getTabOfSubmitFragment2();
                SubmitFormFragment2 fragment2 = (SubmitFormFragment2) getActivity().getSupportFragmentManager().findFragmentByTag(TabOfFragment2);
                fragment2.clearTimes();
                ViewPager aViewPager = ((SubmitActivity) getActivity()).mViewPager;
                aViewPager.setCurrentItem(1, true);
            }
        }
    };


    View.OnClickListener SubmitOnclickListener
            = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            Toast.makeText(getActivity(),
                    "BEERSPOT IS BEING UPLOADED",
                    Toast.LENGTH_LONG).show();
            if (cameraused) {
                submitBeerSpotImage = new BeerSpot(finalName, finalLocType, finalInfo, finalCity, finalAddress,
                        finalBeerType, finalBeerPrice, base64String);
                JSONObject jsonImage = submitBeerSpotImage.createSubmitImageJSON();
                MyDDPState.getInstance().submitStationWithImage(jsonImage);
            } else {
                submitBeerSpotTimes = new BeerSpot(finalName, finalLocType, finalInfo, finalCity, finalAddress,
                        finalDays,
                        finalBeerType, finalBeerPrice);
                JSONObject jsonTime = submitBeerSpotTimes.createSubmitTimeJSON();
                MyDDPState.getInstance().submitStationWithTimes(jsonTime);
            }
            startActivity(new Intent(getActivity().getApplicationContext(), MenuActivity.class));
        }
    };

    public void cameraWasUsed() {
        cameraused = true;
    }

    public void cameraNotUsed() {
        cameraused = false;
    }
}
