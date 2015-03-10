package com.msp.findMyBeer.Activities.Fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.msp.findMyBeer.Database.BeerSpot;
import com.msp.findMyBeer.R;

/**
 *  Create and inflate the MapSpotDetailsSmall-Fragment
 *
 *  @author Florian Wirth
 */
public class MapSpotDetailsSmall extends Fragment
{

    String nameText, typeText, statusText, beerText, priceText, distanceText;
    double spotLat, spotLng;
    Location myLocation;

    public static MapSpotDetailsSmall newInstance(BeerSpot spot, Location myLocation)
    {

        MapSpotDetailsSmall f = new MapSpotDetailsSmall();

        String lat = "";
        String lng = "";

        try
        {
            lat = Double.toString(myLocation.getLatitude());
            lng = Double.toString(myLocation.getLongitude());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        /*String lat = Double.toString(myLocation.getLatitude());
        String lng = Double.toString(myLocation.getLongitude());*/
        //String spotJSON = spot.getJSON().toString();

        Bundle args = new Bundle();
        args.putString("name", spot.getName());
        args.putString("type", spot.getType());
        args.putString("beer", spot.getBeerName());
        args.putString("price", spot.getBeerPrice());
        args.putString("myLat", lat);
        args.putString("myLng", lng);
        args.putString("spotLat", Double.toString(spot.getLat()));
        args.putString("spotLng", Double.toString(spot.getLng()));

        // If the bar is open (should be, since only open Spots get loaded onto the map)
        // -> get Remaining Time-String
        if (spot.getDays().isOpen())
        {
            args.putString("status", "Open (" + spot.getDays().getCurrentDay().getTimeRemaining() + ")");
        }
        else
        {
            args.putString("status", "Closed");
        }

        f.setArguments(args);

        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        nameText   = getArguments().getString("name");
        typeText   = getArguments().getString("type");
        statusText = getArguments().getString("status");
        beerText   = getArguments().getString("beer");
        priceText  = getArguments().getString("price");
        distanceText= "";

        try {
            myLocation = new Location(LocationManager.GPS_PROVIDER);
            myLocation.setLatitude(Double.parseDouble(getArguments().getString("myLat")));
            myLocation.setLongitude(Double.parseDouble(getArguments().getString("myLng")));

            spotLat    = Double.parseDouble(getArguments().getString("spotLat"));
            spotLng    = Double.parseDouble(getArguments().getString("spotLng"));

            distanceText = calculateDistance(spotLat, spotLng);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_map_spotdetails_small,container,false);

        View imgView = v.findViewById(R.id.infoSmallLogo);

        View nameView = v.findViewById(R.id.nameVVS);
        View typeView = v.findViewById(R.id.typeVVS);
        View statusView = v.findViewById(R.id.statusVVS);
        View beerView = v.findViewById(R.id.beerVVS);
        View showDistance = v.findViewById(R.id.showDistance);

        ImageView dot = (ImageView) v.findViewById(R.id.dot_small);

        if (statusText.equals("Closed"))
        {
            dot.setImageResource(R.drawable.icon_red_dot);
        }
        else
        {
            dot.setImageResource(R.drawable.icon_green_dot);
        }


        ((ImageView)imgView).setImageBitmap(setIcon());
        ((TextView)nameView).setText(nameText);
        ((TextView)typeView).setText(typeText);
        ((TextView)statusView).setText(statusText);
        ((TextView)beerView).setText(beerText + " (" + priceText + ")");
        ((TextView)showDistance).setText(distanceText);

        Animation fadeAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in);
        v.startAnimation(fadeAnimation);

        return v;
    }


    /*
     *  Set and scale the icon fitting to type
     */
    private Bitmap setIcon()
    {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_bar);

        switch (typeText)
        {
            case "Bar":
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_bar);
                break;
            case "Kiosk":
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_kiosk);
                break;
            case "Shop":
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_shop);
                break;
            case "Gas Station":
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_gas_station);
                break;
            default:
                break;
        }

        return Bitmap.createScaledBitmap(icon, 50, 50, true);
    }

    /*
     *  Calculate Distance between current Location and Spot Location
     */
    private String calculateDistance(double spotLat, double spotLng)
    {
        String dist = "";
        float[] result = new float[1];

        Location.distanceBetween(myLocation.getLatitude(), myLocation.getLongitude(),
                                            spotLat,                  spotLng,
                                                                                 result);

        dist += String.format("%.0f",result[0]) + "m";

        return dist;
    }
}
