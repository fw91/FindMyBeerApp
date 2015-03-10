package com.msp.findMyBeer.Activities.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.msp.findMyBeer.Activities.Dialogs.RemoveSpotDialog;
import com.msp.findMyBeer.Database.BeerSpot;
import com.msp.findMyBeer.Database.DatabaseHandler;
import com.msp.findMyBeer.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 *  Create and inflate the FavoriteBeerSpot-Fragment to show everything You stored into the DB
 *
 *  @author Florian Wirth
 */
public class FavoriteBSFragment extends Fragment {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, BeerSpot> listDataChild;

    private ArrayList<BeerSpot> myBeerSpots = new ArrayList<>();
    DatabaseHandler DH;

    Typeface headerFace, childFace;

    // Preferences for Map-Features
    public static final String FAV_PREFERENCES = "FavPreferences";
    SharedPreferences fav_preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View myInflatedView = inflater.inflate(R.layout.fragment_favorite_bs_list,container,false);

        // custom Typefaces
        headerFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Ubuntu-M.ttf");
        childFace  = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Ubuntu-L.ttf");

        // get the listview
        expListView = (ExpandableListView) myInflatedView.findViewById(R.id.favList);

        // Load SharedPreferences for Map-Features
        fav_preferences = this.getActivity().getSharedPreferences(FAV_PREFERENCES, 0);

        DH = new DatabaseHandler(myInflatedView.getContext());

        // preparing list data
        getDatabaseSpots();
        prepareListData(myBeerSpots);

        // invisible indicator
        expListView.setGroupIndicator(getResources().getDrawable(R.drawable.invisarrow));

        // get the listAdapter
        listAdapter = new ExpandableListAdapter(getActivity().getApplicationContext(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // move selected group to the top
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
            {

                // necessary, since override happens here
                if (parent.isGroupExpanded(groupPosition))
                {
                    parent.collapseGroup(groupPosition);
                }
                else
                {
                    parent.expandGroup(groupPosition);
                }

                // move to the top
                parent.setSelectionFromTop(groupPosition, 0);

                return true;
            }
        });

        return myInflatedView;
    }

    /*
     *  get the spots from the Database
     */
    private void getDatabaseSpots()
    {
        myBeerSpots = DH.getSpotsFromDB();
    }


    /*
     * Preparing the list data
     */
    private void prepareListData(ArrayList<BeerSpot> dbSpots)
    {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        int currentEntry = 0;

        // load open spots first
        for (BeerSpot spot : dbSpots)
        {
            if (spot.getDays().isOpen())
            {
                // get spot type, and check if it is supposed to be shown
                switch (spot.getType())
                {
                    case "Bar":
                        if (fav_preferences.getBoolean("showBars", true))
                        {
                            listDataHeader.add(spot.getName());
                            listDataChild.put(listDataHeader.get(currentEntry), spot);
                            currentEntry += 1;
                        }
                        break;

                    case "Shop":
                       if (fav_preferences.getBoolean("showShops", true))
                        {
                           listDataHeader.add(spot.getName());
                           listDataChild.put(listDataHeader.get(currentEntry), spot);
                           currentEntry += 1;
                        }
                        break;

                    case "Gas Station":
                        if (fav_preferences.getBoolean("showGasStations", true))
                        {
                            listDataHeader.add(spot.getName());
                            listDataChild.put(listDataHeader.get(currentEntry), spot);
                            currentEntry += 1;
                        }
                        break;

                    case "Kiosk":
                        if (fav_preferences.getBoolean("showKiosks", true))
                        {
                            listDataHeader.add(spot.getName());
                            listDataChild.put(listDataHeader.get(currentEntry), spot);
                            currentEntry += 1;
                        }
                        break;
                }
            }
        }

        // load closed spots after (IF openOnly is false)
        if (!fav_preferences.getBoolean("openOnly",false))
        {
            for (BeerSpot spot : dbSpots)
            {
                if (!spot.getDays().isOpen())
                {
                    switch (spot.getType())
                    {
                        case "Bar":
                            if (fav_preferences.getBoolean("showBars", true))
                            {
                                listDataHeader.add(spot.getName());
                                listDataChild.put(listDataHeader.get(currentEntry), spot);
                                currentEntry += 1;
                            }
                            break;

                        case "Shop":
                            if (fav_preferences.getBoolean("showShops", true))
                            {
                                listDataHeader.add(spot.getName());
                                listDataChild.put(listDataHeader.get(currentEntry), spot);
                                currentEntry += 1;
                            }
                            break;

                        case "Gas Station":
                            if (fav_preferences.getBoolean("showGasStations", true))
                            {
                                listDataHeader.add(spot.getName());
                                listDataChild.put(listDataHeader.get(currentEntry), spot);
                                currentEntry += 1;
                            }
                            break;

                        case "Kiosk":
                            if (fav_preferences.getBoolean("showKiosks", true))
                            {
                                listDataHeader.add(spot.getName());
                                listDataChild.put(listDataHeader.get(currentEntry), spot);
                                currentEntry += 1;
                            }
                            break;
                    }
                }
            }
        }
    }

    // -------------------------------------- List-Adapter --------------------------------------------
    //-------------------------------------------------------------------------------------------------

    private class ExpandableListAdapter extends BaseExpandableListAdapter
    {

        private Context _context;
        private List<String> _listDataHeader;
        private HashMap<String, BeerSpot> _listDataChild;
        private int lastGroupExpanded;

        public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, BeerSpot> listChildData)
        {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPos, int childPos)
        {
            return this._listDataChild.get(this._listDataHeader.get(groupPos));
        }

        @Override
        public long getChildId(int groupPos, int childPos)
        {
            return childPos;
        }

        @Override
        public View getChildView(final int groupPos, final int childPos, boolean isLastChild, View convertView, ViewGroup parent)
        {

            // inflate Child-layout
            final BeerSpot spot = (BeerSpot) getChild(groupPos, childPos);

            if (convertView == null)
            {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.fragment_favorite_bs_itemview, parent, false);
            }

            Button remove = (Button) convertView.findViewById(R.id.remove_btn);
            remove.setTypeface(childFace);

            TextView typeView = (TextView) convertView.findViewById(R.id.typeDVVV);
            typeView.setTypeface(childFace);
            TextView infoView = (TextView) convertView.findViewById(R.id.infoDVVV);
            infoView.setTypeface(childFace);
            TextView statusView = (TextView) convertView.findViewById(R.id.statusDVVV);
            statusView.setTypeface(childFace);
            TextView cityView = (TextView) convertView.findViewById(R.id.cityDVVV);
            cityView.setTypeface(childFace);
            TextView addressView = (TextView) convertView.findViewById(R.id.addressDVVV);
            addressView.setTypeface(childFace);
            TextView beerView = (TextView) convertView.findViewById(R.id.beerDVVV);
            beerView.setTypeface(childFace);
            TextView priceView = (TextView) convertView.findViewById(R.id.priceDVVV);
            priceView.setTypeface(childFace);
            TextView timesView = (TextView) convertView.findViewById(R.id.timesDVVV);
            timesView.setTypeface(childFace);

            typeView.setText(spot.getType());
            infoView.setText(spot.getInfo());
            if (spot.getDays().isOpen())
            {
                statusView.setText("Open (" + spot.getDays().getCurrentDay().getTimeRemaining() + ")");
            }
            else
            {
                statusView.setText("Closed");
            }
            cityView.setText(spot.getCity());
            addressView.setText(spot.getAddress());
            beerView.setText(spot.getBeerName());
            priceView.setText(spot.getBeerPrice());
            timesView.setText(spot.daysToString());

            // add animation
            Animation fadeAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
            convertView.setAnimation(fadeAnimation);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // custom collapse command, onclick view
                    expListView.collapseGroup(groupPos);
                }
            });

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // handle button-click
                    RemoveSpotDialog dialog = RemoveSpotDialog.newInstance(spot);
                    dialog.show(getFragmentManager(), "RemoveSpotDialog");
                }
            });

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition)
        {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition)
        {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount()
        {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition)
        {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
        {
            String headerTitle = (String) getGroup(groupPosition);
            final BeerSpot spot = (BeerSpot) getChild(groupPosition, 0);

            if (convertView == null)
            {
                // inflate Header-Layout
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.fragment_favorite_bs_item_head, parent, false);
            }

            ImageView dot = (ImageView) convertView.findViewById(R.id.open_dot);

            if (spot.getDays().isOpen())
            {
                dot.setImageResource(R.drawable.icon_green_dot);
            }
            else
            {
                dot.setImageResource(R.drawable.icon_red_dot);
            }

            //Typeface face=Typeface.createFromAsset(getActivity().getAssets(),"fonts/Ubuntu-M.ttf");

            TextView nameView = (TextView) convertView.findViewById(R.id.nameVVD);
            nameView.setTypeface(childFace);
            nameView.setText(headerTitle);

            return convertView;
        }

        @Override
        public void onGroupExpanded(int groupPosition)
        {
            if(groupPosition != lastGroupExpanded)
            {
                // only expand 1 group at a time
                expListView.collapseGroup(lastGroupExpanded);
            }

            super.onGroupExpanded(groupPosition);
            lastGroupExpanded = groupPosition;
        }

        @Override
        public boolean hasStableIds()
        {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition)
        {
            return true;
        }
    }
}