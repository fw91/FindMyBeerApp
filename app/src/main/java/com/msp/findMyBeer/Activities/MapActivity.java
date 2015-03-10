package com.msp.findMyBeer.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.msp.findMyBeer.Activities.Dialogs.MapSpotDetailsBig;
import com.msp.findMyBeer.Activities.Fragments.MapSpotDetailsSmall;
import com.msp.findMyBeer.Activities.Fragments.MapSpotsCountFragment;
import com.msp.findMyBeer.Database.BeerSpot;
import com.msp.findMyBeer.Database.DatabaseHandler;
import com.msp.findMyBeer.MyDDPState;
import com.msp.findMyBeer.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  The Map Activity
 *
 *  @author Florian Wirth
 */
public class MapActivity extends Activity implements LocationListener, GoogleMap.OnMarkerClickListener
{
    // Map Stuff
    final int MIN_ZOOM = 12;
    final int MAX_ZOOM = 16;

    private GoogleMap googleMap;
    private LocationManager locationManager;

    private Marker myMarker;
    private Map<String, BeerSpot> currentMarkers;
    private BeerSpot currentSpot;
    private LatLngBounds currentSpotsArea;
    private int favCount;

    // Preferences of Application
    public static final String APP_PREFERENCES = "AppPreferences";
    SharedPreferences app_preferences;

    // Preferences for Map-Features
    public static final String MAP_PREFERENCES = "MapPreferences";
    SharedPreferences map_preferences;

    SharedPreferences spots_disabled;

    // Layout
    private FrameLayout infoBoxSmall_frame;
    private EditText search_field;
    private FrameLayout mapContainer;

    InputMethodManager inputManager;

    // Tutorial Text
    private static final String TUTORIAL_PAGE1 =
                    "Welcome to Find My Beer.\n\n" +
                    "This is Your personal Map.";

    private static final String TUTORIAL_PAGE2 =
                    "It supports the following features:\n\n" +
                    "- Auto-Update - \nAutomatically get the Spots of Your current View\n\n" +
                    "- Details - \nYou can review details for each Spot\n\n" +
                    "- Search - \nJust type in Your desired destination\n\n" +
                    "- Set View Range - \nChoose Your desired View Range\n\n" +
                    "- Show Favorites - \nShow all the Spots from Your Favorites on the Map\n\n" +
                    "- Only Open - \nOnly display open Spots\n";

    private static final String TUTORIAL_PAGE3 =
                    "There are 4 different types of Markers:\n\n" +
                    "- Blue - \nOpen Spots from Your Favorites \n\n" +
                    "- Green - \nMost visited Spots\n\n" +
                    "- Yellow - \nAverage visited Spots\n\n" +
                    "- Red - \nLeast visited Spots\n";

    private static final String TUTORIAL_PAGE4 =
                    "You can click a Marker to get its details.\n\n" +
                    "You can add every Spot into Your Favorites.\n\n" +
                    "You have the possibility of \"checking in\", this will improve the " +
                    "status of that Spot, and will show to other Users\n\n" +
                    "You can read and add Comments for each spot\n\n" +
                    "Don't drink and drive. Cheers.\n";

    private static final String HINT_REMINDER =
                    "Hints are disabled\n\n" +
                    "(to re-enable, check \"enable hints\" in the Main Menu)\n";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Apply custom ActionBar style
        createCustomActionBarTitle();

        // connect to Server
        MyDDPState.getInstance().connectIfNeeded();

        currentMarkers = new HashMap<>();

        // Load SharedPreferences
        map_preferences = getSharedPreferences(MAP_PREFERENCES, MODE_PRIVATE);
        app_preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        // Temporary Preferences, to store which Spot has been checked in, which hasn't
        spots_disabled  = getPreferences(MODE_PRIVATE);
        // On ActivityStart -> clear
        spots_disabled.edit().clear().apply();
        // call inside MapSpotDetailsBig


        // Initialize Layout
        inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        ImageButton search_btn = (ImageButton)findViewById(R.id.search_btn);
        search_field = (EditText)findViewById(R.id.search_field);
        infoBoxSmall_frame = (FrameLayout) findViewById(R.id.infoContainer);

        // set onClickListener for GeoCoder
        search_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    geoLocate(search_field.getText().toString());
                    search_field.setText("");
                    // hide keyboard after search
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        // Set OnClickListener for Details
        infoBoxSmall_frame.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (infoBoxSmall_frame.getTag().toString().equals("showDetailSmall"))
                {
                    showDetails(currentSpot);
                }

            }
        });

        // if hints are enabled -> showTutorial
        if (app_preferences.getBoolean("showHints",true))
        {
            showTutorial(TUTORIAL_PAGE1);
        }

    }


    @Override
    public void onResume()
    {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                                                mMessageReceiver, new IntentFilter("newMarkers"));

        // Initialize Map
        try
        {
            initializeMap();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        // Prints Markers on the Map
        updateMarkers(MyDDPState.getInstance().getSpots());
        showSpotsCountSmall(getInboundMarkers(),favCount);

        // start Location Updates
        try
        {
            // Resume Location Updates, after re-entering the Activity
            // requestUpdate, every 20m moved
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 20, this);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    @Override
    protected void onPause() {
        super.onPause();

        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        // stop Location Updates
        try
        {
            // Stop Location Updates, when leaving the Activity
            locationManager.removeUpdates(this);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // -------------------------------------- Map-Methods ------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // Methods:
    //
    // initializeMap()
    // onMarkerClick()
    // cameraMovement()
    // updateSpots()
    // geoLocate()
    // getZoomLevel()
    // updateMarkers()
    // addFavoriteSpots()
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------


    /**
     *  Map setup
     */
    private void initializeMap()
    {

        if (googleMap == null)
        {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();


            // check if map is created successfully or not
            if (googleMap == null)
            {
                Toast.makeText(getApplicationContext(), "Sorry! unable to create maps",
                        Toast.LENGTH_SHORT).show();
            }

            if (googleMap != null)
            {
                // enable location of device
                googleMap.setMyLocationEnabled(true);

                // choose map-type
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                // initialize Location Manager
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                // disable Zoom buttons
                googleMap.getUiSettings().setZoomControlsEnabled(false);

                // a Location to Start from
                double startLat = Double.parseDouble(map_preferences.getString("lastKnownLat", "48.15073382"));
                double startLng = Double.parseDouble(map_preferences.getString("lastKnownLng", "11.58058763"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(startLat,startLng),
                                                                                    getZoomLevel()));

                // get bounds of CameraView for first updateRequest
                LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;

                // first init of currentSpotsArea
                if (currentSpotsArea == null)
                {
                    currentSpotsArea = bounds;
                }

                // first init of SpotList
                updateSpots(currentSpotsArea);


                // set new-defined listener
                googleMap.setOnMarkerClickListener(this);


                /*
                 *  Used to reset the infoBox below the map
                 */
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
                {
                    @Override
                    public void onMapClick(LatLng latLng)
                    {
                        showSpotsCountSmall(getInboundMarkers(),favCount);
                    }
                });


                /*
                 *  Used to refresh Spots based on current View, force zoom
                 */
                googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener()
                {
                    @Override
                    public void onCameraChange(CameraPosition position)
                    {
                        // get current Camera
                        CameraPosition pos = googleMap.getCameraPosition();

                        // force min/max_zoom
                        if (pos.zoom < MIN_ZOOM)
                        {
                            googleMap.moveCamera(CameraUpdateFactory.zoomTo(MIN_ZOOM));
                        }
                        else if (pos.zoom > MAX_ZOOM)
                        {
                            googleMap.moveCamera(CameraUpdateFactory.zoomTo(MAX_ZOOM));
                        }

                        // save zoomLevel
                        switch (Math.round(pos.zoom))
                        {
                            case 16:
                                map_preferences.edit().putInt("zoom", 0).apply(); break;
                            case 15:
                                map_preferences.edit().putInt("zoom", 1).apply(); break;
                            case 14:
                                map_preferences.edit().putInt("zoom", 2).apply(); break;
                            case 13:
                                map_preferences.edit().putInt("zoom", 3).apply(); break;
                            case 12:
                                map_preferences.edit().putInt("zoom", 4).apply(); break;
                        }

                        // get bounds of CameraView
                        LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;

                        // init of currentSpotsArea
                        if (currentSpotsArea == null)
                        {
                            currentSpotsArea = bounds;
                        }

                        // handle camera movement
                        cameraMovement(bounds);
                    }
                });
            }
        }
    }


    /**
     *  whenever a marker is clicked, check which one and execute details-stuff
     */
    @Override
    public boolean onMarkerClick(Marker marker)
    {
        if (currentMarkers.get(marker.getId()) != null)
        {
            showDetailsSmall(currentMarkers.get(marker.getId()));
            this.currentSpot = currentMarkers.get(marker.getId());
        }

        return true;
    }


    /**
     *  Track the cameraMovement to update the Spots whenever the user changes the view
     *
     *  @param bounds
     */
    private void cameraMovement(LatLngBounds bounds)
    {
        double offset = 0;

        // Set offset in meters based on which zoomLevel we are in
        switch (map_preferences.getInt("zoom", 2))
        {
            case 0: // zoomLevel = 16 / 500m view
                offset = 300; // meters
                break;
            case 1: // zoomLevel = 15 / 1km view
                offset = 600; // meters
                break;
            case 2: // zoomLevel = 14 / 2km view
                offset = 1100; // meters
                break;
            case 3: // zoomLevel = 13 / 4km view
                offset = 2200; // meters
                break;
            case 4: // zoomLevel = 12 / 8km view
                offset = 4400; // meters
                break;
            default:
                break;
        }

        // get top-right coordinates of mapFragment (where we last loaded mapSpots)
        double curLatNorth = currentSpotsArea.northeast.latitude;
        double curLngEast = currentSpotsArea.northeast.longitude;

        // get top-right coordinates of mapFragment (where we are now)
        double newLatNorth = bounds.northeast.latitude;
        double newLngEast = bounds.northeast.longitude;

        float[] distance = new float[1];

        Location.distanceBetween(curLatNorth, curLngEast,
                                 newLatNorth, newLngEast,
                                                         distance);

        // if the distance is greater than the set-offset -> update spots
        if (distance[0] > offset)
        {
            //Toast.makeText(this, "update Spots", Toast.LENGTH_SHORT).show();
            currentSpotsArea = bounds;
            updateSpots(currentSpotsArea);
        }
    }


    /**
     *  Update Markers on Camera Movement
     */
    private void updateSpots(LatLngBounds bounds)
    {
        double lngWest, latSouth, lngEast, latNorth;

        lngWest = bounds.southwest.longitude;
        latNorth = bounds.northeast.latitude;
        lngEast = bounds.northeast.longitude;
        latSouth = bounds.southwest.latitude;

        // add a little offset value, so we send less requests
        double horizontalOffset = Math.abs(lngEast - lngWest) * 0.7;
        double verticalOffset   = Math.abs(latNorth - latSouth) * 0.7;

        /*
        Log.i("updateSpots","lngWest = " + lngWest);
        Log.i("updateSpots","lngEast = " + lngEast);
        Log.i("updateSpots","horizontalOffset = " + horizontalOffset);
        Log.i("updateSpots","latNorth = " + latNorth);
        Log.i("updateSpots","latSouth = " + latSouth);
        Log.i("updateSpots","verticalOffset = " + verticalOffset);
        */

        MyDDPState.getInstance().findMyBeer(Double.toString(lngWest-horizontalOffset),
                                            Double.toString(latNorth+verticalOffset),
                                            Double.toString(lngEast+horizontalOffset),
                                            Double.toString(latSouth-verticalOffset));

        currentSpotsArea = bounds;
    }


    /**
     *  this is the Google-Geocoder, it can be used to type in a desired destination
     *  and show it on the map
     */
    private void geoLocate(String input) throws IOException
    {
        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(input, 1);
        Address add = list.get(0);

        double lat = add.getLatitude();
        double lng = add.getLongitude();

        LatLng destination = new LatLng(lat, lng);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, getZoomLevel()));
    }


    /**
     *  Get the Zoom Level
     */
    private int getZoomLevel()
    {
        int zoomLevel = 14; // default Value

        switch (map_preferences.getInt("zoom", 2))
        {
            case 0:
                zoomLevel = 16; // 250m radius
                break;
            case 1:
                zoomLevel = 15; // 500m radius
                break;
            case 2:
                zoomLevel = 14; // 1km  radius
                break;
            case 3:
                zoomLevel = 13; // 2km  radius
                break;
            case 4:
                zoomLevel = 12; // 4km  radius
                break;
            default:
                break;
        }

        return zoomLevel;
    }


    /**
     *  this method updates the markers inside the map, it clears existing markers, it adds new
     *  markers and it stores the markers combined with their corresponding BeerSpots
     *
     *  @param markerSpots ArrayList of new BeerSpots to display on map
     */
    public void updateMarkers(Map<String, BeerSpot> markerSpots)
    {
        // clear map
        googleMap.clear();

        // (re)load HashMap
        currentMarkers.clear();

        // for each spot from HashMap -> Set Marker -> add it into HashMap with their keys to make
        //                                               it accessible for detailed views

        for (Map.Entry<String, BeerSpot> spot : markerSpots.entrySet())
        {
            // only show open spots
            if (map_preferences.getBoolean("open_only",true))
            {
                if (spot.getValue().getDays().isOpen())
                {
                    LatLng spotPos = spot.getValue().getLocation();

                    // set color - based on visits
                    if (spot.getValue().getVisitCounter() < 5)
                    {
                        myMarker = googleMap.addMarker(new MarkerOptions()
                                .position(spotPos)
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    }
                    else if (spot.getValue().getVisitCounter() > 5 && spot.getValue().getVisitCounter() < 10)
                    {
                        myMarker = googleMap.addMarker(new MarkerOptions()
                                .position(spotPos)
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    }
                    else
                    {
                        myMarker = googleMap.addMarker(new MarkerOptions()
                                .position(spotPos)
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    }

                    // myMarker.getId() is used in a HashMap as Key to access the corresponding BeerSpot
                    // when clicked
                    currentMarkers.put(myMarker.getId(),spot.getValue());
                }
            }
            else
            {
                LatLng spotPos = spot.getValue().getLocation();

                // set color - based on visits
                if (spot.getValue().getVisitCounter() < 5)
                {
                    myMarker = googleMap.addMarker(new MarkerOptions()
                            .position(spotPos)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }
                else if (spot.getValue().getVisitCounter() > 5 && spot.getValue().getVisitCounter() < 10)
                {
                    myMarker = googleMap.addMarker(new MarkerOptions()
                            .position(spotPos)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                }
                else
                {
                    myMarker = googleMap.addMarker(new MarkerOptions()
                            .position(spotPos)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }

                // myMarker.getId() is used in a HashMap as Key to access the corresponding BeerSpot
                // when clicked
                currentMarkers.put(myMarker.getId(),spot.getValue());
            }
        }


        // If Feature is activated, add Favorite-Spots on the Map (false is default value here)
        if (map_preferences.getBoolean("show_favorites", false))
        {
            addFavoriteSpots();
        }

        showSpotsCountSmall(getInboundMarkers(),favCount);
    }


    /**
     *  This method adds all the favorite-BeerSpots from the personal SQLite-Database onto the map
     *  use a different color, than for normal Spots (only adds Spots that are Open right now)
     */
    private void addFavoriteSpots()
    {
        DatabaseHandler DH = new DatabaseHandler(getApplicationContext());

        ArrayList<BeerSpot> favoriteSpots = DH.getSpotsFromDB();
        favCount = 0;

        // for each spot from ArrayList -> Set Marker -> add it into HashMap with their keys to make
        //                                               it accessible for detailed views
        for (BeerSpot spot : favoriteSpots)
        {
            // only add open Spots onto map
            if (map_preferences.getBoolean("open_only",true))
            {
                if (spot.getDays().isOpen())
                {
                    LatLng spotPos = spot.getLocation();

                    myMarker = googleMap.addMarker(new MarkerOptions()
                            .position(spotPos)
                            .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                    // myMarker.getId() is used in a HashMap as Key to access the corresponding BeerSpot
                    // when clicked
                    currentMarkers.put(myMarker.getId(), spot);
                    favCount += 1;
                }
            }
            else
            {
                LatLng spotPos = spot.getLocation();

                myMarker = googleMap.addMarker(new MarkerOptions()
                        .position(spotPos)
                        .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                // myMarker.getId() is used in a HashMap as Key to access the corresponding BeerSpot
                // when clicked
                currentMarkers.put(myMarker.getId(), spot);
                favCount += 1;
            }
        }
    }


    /**
     *  register the Receiver so we can get updates from the Server
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateMarkers(MyDDPState.getInstance().getSpots());
        }
    };


    /* ------------------------------------ Location-Listener ------------------------------------
     * -------------------------------------------------------------------------------------------
     * Methods:
     *
     * onLocationChanged()
     * -------------------------------------------------------------------------------------------
     * -------------------------------------------------------------------------------------------*/


    /**
     *  LocationListener.onLocationChanged (whenever we receive a new Location, this gets executed)
     */
    @Override
    public void onLocationChanged(Location location)
    {
        String lastKnownLat = Double.toString(location.getLatitude());
        String lastKnownLng = Double.toString(location.getLongitude());

        // Store Location for next App-start
        map_preferences.edit().putString("lastKnownLat",lastKnownLat).apply();
        map_preferences.edit().putString("lastKnownLng",lastKnownLng).apply();

        LatLng currentPos = new LatLng(location.getLatitude(),location.getLongitude());

        // move the camera to our position (with animation)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPos, getZoomLevel()));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle)
    {

    }

    @Override
    public void onProviderEnabled(String s)
    {

    }

    @Override
    public void onProviderDisabled(String s)
    {

    }


    /**
     *  this method opens the big details_dialog on screen
     *
     *  @param spot The BeerSpot of which the information is, to be shown
     */
    public void showDetails(BeerSpot spot)
    {
        MapSpotDetailsBig dialog = MapSpotDetailsBig
                .newInstance(spot);
        dialog.show(getFragmentManager(), "dialog");
    }


    /**
     *  this method shows the small-InfoBox with some details about the Spot
     *
     *  @param spot The BeerSpot of which the information is, to be shown
     */
    public void showDetailsSmall(BeerSpot spot)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment newFragment = MapSpotDetailsSmall.newInstance(spot, googleMap.getMyLocation());
        ft.replace(R.id.infoContainer, newFragment);
        infoBoxSmall_frame.setTag("showDetailSmall");
        ft.commit();
    }


    /**
     *  this method shows the small-InfoBox with the number of BeerSpots found nearby
     *
     *  @param spotsCount The size() of the beerSpots-Array
     */
    public void showSpotsCountSmall(int spotsCount, int favCount)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment newFragment = MapSpotsCountFragment.newInstance(spotsCount,favCount);
        ft.replace(R.id.infoContainer, newFragment);
        infoBoxSmall_frame.setTag("spotsCount");
        ft.commit();
    }


    /**
     *  Calculate size of current markers displayed inside the MapView
     */
    public int getInboundMarkers(){
        int listSize = 0;
        LatLngBounds bound = googleMap.getProjection().getVisibleRegion().latLngBounds;
        for(Map.Entry<String, BeerSpot> entry : currentMarkers.entrySet()) {
            if(bound.contains(entry.getValue().getLocation())){
                listSize++;
            }
        }
        return listSize;
    }


    /**
     * Options Menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.map_settings, menu);

        // if Feature is enabled in the Prefs -> check item
        // else don't
        menu.getItem(2).setChecked(map_preferences.getBoolean("show_favorites", false));
        menu.getItem(3).setChecked(map_preferences.getBoolean("open_only", true));

        return true;
    }

    /**
     * OptionsMenu Items
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case R.id.refresh_spots:
                // manually update Markers
                updateMarkers(MyDDPState.getInstance().getSpots());
                break;
            case R.id.set_radius:
                // set the radius -> change prefs
                setViewRangeDialog();
                break;
            case R.id.show_favorites:
                if (item.isChecked()) {
                    // un-check item -> change prefs to false -> reprint markers
                    item.setChecked(false);
                    map_preferences.edit().putBoolean("show_favorites", false).apply();
                    favCount = 0;
                    updateMarkers(MyDDPState.getInstance().getSpots());
                } else {
                    // check item -> change prefs to true -> reprint markers
                    item.setChecked(true);
                    map_preferences.edit().putBoolean("show_favorites", true).apply();
                    updateMarkers(MyDDPState.getInstance().getSpots());
                }
                break;
            case R.id.openOnlyMap:
                if (item.isChecked()) {
                    // un-check item -> change prefs to false -> reprint markers
                    item.setChecked(false);
                    map_preferences.edit().putBoolean("open_only", false).apply();
                    updateMarkers(MyDDPState.getInstance().getSpots());
                } else {
                    // check item -> change prefs to true -> reprint markers
                    item.setChecked(true);
                    map_preferences.edit().putBoolean("open_only", true).apply();
                    updateMarkers(MyDDPState.getInstance().getSpots());
                }
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            default:
                break;
        }
        return true;
    }


    /**
     *  Show a Dialog for setting the Radius, Custom Style
     */
    private void setViewRangeDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        TextView title = new TextView(this);
        title.setText(R.string.setRadius_title);
        title.setTextSize(23);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0,25,0,25);
        title.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-L.ttf"));

        builder.setCustomTitle(title);

        builder.setSingleChoiceItems(R.array.setRadius_distance, map_preferences.getInt("zoom", 2), null)
               .setPositiveButton(R.string.setRadius_apply, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                       dialog.dismiss();
                       int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                       map_preferences.edit().putInt("zoom", selectedPosition).apply();
                       googleMap.animateCamera(CameraUpdateFactory.zoomTo(getZoomLevel()));
                   }
                })
               .setNegativeButton(R.string.setRadius_cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                       dialog.dismiss();
                   }
                });

        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);
                Button btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE);

                Typeface face=Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-L.ttf");
                btnPositive.setTypeface(face);
                btnNegative.setTypeface(face);
            }
        });

        alert.show();
    }


    /**
     *  Create own Action Bar Style
     */
    private void createCustomActionBarTitle(){
        this.getActionBar().setDisplayShowCustomEnabled(true);
        this.getActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.custom_action_bar, null);

        Typeface face =Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-M.ttf");
        ((TextView)v.findViewById(R.id.titleFragment1)).setTypeface(face);

        ((TextView)v.findViewById(R.id.titleFragment1)).setText("Beer Me");

        //assign the view to the actionbar
        this.getActionBar().setCustomView(v);
    }


    /**
     *  this is a very simple tutorial for the user, to explore the features of this app
     *
     *  @param hint
     */
    private void showTutorial(String hint)
    {
        AlertDialog.Builder tutorial = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_hint, null);

        final TextView hintText = (TextView) view.findViewById(R.id.hintTxt);
        final CheckBox showHints = (CheckBox) view.findViewById(R.id.showHints);

        showHints.setChecked(app_preferences.getBoolean("showHints",true));

        hintText.setText(hint);

        TextView title = new TextView(this);
        title.setText("Map Features");
        title.setTextSize(23);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0,25,0,25);
        title.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-L.ttf"));

        tutorial.setCustomTitle(title);

        tutorial.setView(view)
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (showHints.isChecked()) {
                            switch (hintText.getText().toString()) {
                                case TUTORIAL_PAGE1:
                                    app_preferences.edit().putBoolean("showHints", true).apply();
                                    showTutorial(TUTORIAL_PAGE2);
                                    break;
                                case TUTORIAL_PAGE2:
                                    app_preferences.edit().putBoolean("showHints", true).apply();
                                    showTutorial(TUTORIAL_PAGE3);
                                    break;
                                case TUTORIAL_PAGE3:
                                    app_preferences.edit().putBoolean("showHints", true).apply();
                                    showTutorial(TUTORIAL_PAGE4);
                                    break;
                                case TUTORIAL_PAGE4:
                                    app_preferences.edit().putBoolean("showHints", true).apply();
                                    if (!app_preferences.getBoolean("showHints", true)) {
                                        showTutorial(HINT_REMINDER);
                                    }
                                    break;
                                case HINT_REMINDER:
                                    app_preferences.edit().putBoolean("showHints", true).apply();
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            switch (hintText.getText().toString()) {
                                case TUTORIAL_PAGE1:
                                    app_preferences.edit().putBoolean("showHints", false).apply();
                                    showTutorial(TUTORIAL_PAGE2);
                                    break;
                                case TUTORIAL_PAGE2:
                                    app_preferences.edit().putBoolean("showHints", false).apply();
                                    showTutorial(TUTORIAL_PAGE3);
                                    break;
                                case TUTORIAL_PAGE3:
                                    app_preferences.edit().putBoolean("showHints", false).apply();
                                    showTutorial(TUTORIAL_PAGE4);
                                    break;
                                case TUTORIAL_PAGE4:
                                    app_preferences.edit().putBoolean("showHints", false).apply();
                                    if (!app_preferences.getBoolean("showHints", true)) {
                                        showTutorial(HINT_REMINDER);
                                    }
                                    break;
                                case HINT_REMINDER:
                                    app_preferences.edit().putBoolean("showHints", false).apply();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        switch (hintText.getText().toString())
                        {
                            case HINT_REMINDER:
                                app_preferences.edit().putBoolean("showHints", false).apply();
                                break;
                            default:
                                app_preferences.edit().putBoolean("showHints", false).apply();
                                showTutorial(HINT_REMINDER);
                                break;
                        }
                    }
                });

        final AlertDialog alert = tutorial.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);
                Button btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE);

                Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-L.ttf");
                btnPositive.setTypeface(face);
                btnNegative.setTypeface(face);
            }
        });

        alert.show();
    }
}
