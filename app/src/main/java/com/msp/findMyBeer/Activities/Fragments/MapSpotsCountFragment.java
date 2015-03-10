package com.msp.findMyBeer.Activities.Fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.msp.findMyBeer.R;

/**
 *  Create and inflate the MapSpotsCount-Fragment
 *
 *  @author Florian Wirth
 */
public class MapSpotsCountFragment extends Fragment
{

    int spotsCount, favCount;
    public static final String MAP_PREFERENCES = "MapPreferences";
    SharedPreferences map_preferences;

    public static MapSpotsCountFragment newInstance(int spotsCount, int favCount)
    {

        MapSpotsCountFragment f = new MapSpotsCountFragment();

        Bundle args = new Bundle();
        args.putInt("count", spotsCount);
        args.putInt("favCount", favCount);

        f.setArguments(args);

        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        map_preferences = getActivity().getSharedPreferences(MAP_PREFERENCES,0);

        spotsCount = getArguments().getInt("count");
        favCount = getArguments().getInt("favCount");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_map_spotscount,container,false);

        View overView = v.findViewById(R.id.overviewSpots);

        //int spotsLoaded = spotsCount - favCount;
        String displayText = "";
        String km ="";
        switch (map_preferences.getInt("zoom",2))
        {

            case 0: // zoomLevel = 16 / 500m view
                km = "250 meters"; // meters
                break;
            case 1: // zoomLevel = 15 / 1km view
                km = "500 meters"; // meters
                break;
            case 2: // zoomLevel = 14 / 2km view
                km = "1 km"; // meters
                break;
            case 3: // zoomLevel = 13 / 4km view
                km = "2 km"; // meters
                 break;
            case 4: // zoomLevel = 12 / 8km view
                km = "4 km";
                break;
            default:
                break;
        }

        if (spotsCount == 1)
        {
            displayText += "" + spotsCount + " Spot within " + km + "\n";
        }
        else if (spotsCount == 0)
        {
            displayText += "No Spots found within " + km + "\n";
        }
        else
        {
            displayText += "" + spotsCount + " Spots within " + km + "\n";
        }


        if (map_preferences.getBoolean("show_favorites",false))
        {
            displayText += "(Favorites are displayed)";
        }
        else
        {
            displayText += "(Favorites are hidden)";
        }

        Typeface face = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Ubuntu-L.ttf");
        ((TextView)overView).setTypeface(face);

        ((TextView)overView).setText(displayText);

        Animation fadeAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in);
        v.startAnimation(fadeAnimation);

        return v;
    }
}
