package com.msp.findMyBeer.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.msp.findMyBeer.Activities.Fragments.FavoriteBSFragment;
import com.msp.findMyBeer.Database.BeerSpot;
import com.msp.findMyBeer.Database.BeerSpotDays;
import com.msp.findMyBeer.Database.Comment;
import com.msp.findMyBeer.Database.DatabaseHandler;
import com.msp.findMyBeer.R;

import java.util.ArrayList;

/**
 *  Activity to show current SQLite-Database BeerSpot-entries
 *
 *  @author Florian Wirth
 */
public class FavoriteBSActivity extends Activity {

    DatabaseHandler DH;
    ArrayList<BeerSpot> spotlist1;

    // Preferences for Favorites
    public static final String FAV_PREFERENCES = "FavPreferences";
    SharedPreferences fav_preferences;

    // Preferences of Application
    public static final String APP_PREFERENCES = "AppPreferences";
    SharedPreferences app_preferences;

    private boolean [] filter_selections;

    // Tutorial Text
    private static final String TUTORIAL_PAGE1 =
                    "This are Your own Favorites.\n\n" +
                    "Everything here is stored on Your device, and You can access it on Your Map.\n";

    private static final String TUTORIAL_PAGE2 =
                    "It supports the following features:\n\n" +
                    "- Apply Filters - \nChoose Filters for Your List\n\n" +
                    "- Only Open Spots - \nOnly display Spots, which are open right now\n\n" +
                    "- Remove - \nRemove the selected Spot\n\n" +
                    "- Details - \nYou can review the details of a Spot\n\n" +
                    "- Clear Favorites - \nDelete everything\n" +
                    "(this will permanently remove Your Favorites)\n";

    private static final String HINT_REMINDER =
                    "Hints are disabled\n\n" +
                    "(to re-enable, check \"enable hints\" in the Main Menu)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_bs);

        // Add own ActionBar Style
        createCustomActionBarTitle();

        // Initialize a test-List to see if functionallity is granted
        setUpTestList();

        // Load SharedPreferences for Fav-Features
        fav_preferences = getSharedPreferences(FAV_PREFERENCES, MODE_PRIVATE);
        app_preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        // if hints are enabled -> showTutorial
        if (app_preferences.getBoolean("showHints",true))
        {
            showTutorial(TUTORIAL_PAGE1);
        }

        DH = new DatabaseHandler(getApplicationContext());

        // Load the fragment
        loadFragment();
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        // new animation
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    /*
     *  Load the DB-fragment
     */
    public void loadFragment()
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment newFragment = new FavoriteBSFragment();
        ft.replace(R.id.favFragment, newFragment);
        ft.commit();
    }


    /*
     *  Dialog for clearing the DB
     */
    private void clearDatabaseDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_remove, null);

        View removeView = v.findViewById(R.id.removeTxt);

        ((TextView)removeView).setText("Are You sure?\n(This can not be undone)\n");

        builder.setView(v);
        builder.setPositiveButton(R.string.cdbYes, new Dialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                DH.clearSpotDB();
                dialog.cancel();
                loadFragment();
                Toast.makeText(getApplicationContext(),"Database cleared",Toast.LENGTH_SHORT).show();
            }

        });

        builder.setNegativeButton(R.string.cdbNo, new Dialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);
                Button btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE);

                // Set font for Buttons
                Typeface face=Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-L.ttf");
                btnPositive.setTypeface(face);
                btnNegative.setTypeface(face);
            }
        });

        alert.show();

        //builder.show();
    }

    // -------------------------------------- Options Menu -----------------------------------------
    // ---------------------------------------------------------------------------------------------
    // Methods:
    //
    // onCreateOptionsMenu()
    // onOptionsItemSelected()
    // setRadiusDialog()
    //
    // Features:
    //
    //   Refresh Spots
    //   Set Radius
    //   Show Favorites
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fav_settings, menu);

        // if Feature is enabled in the Prefs -> check item
        // else don't
        menu.getItem(2).setChecked(fav_preferences.getBoolean("openOnly", false));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.addTestList:
                // Set Test list
                // delete DB and fill with test-spotlist
                DH.refreshSpotDB(spotlist1);
                // reload the fragment
                loadFragment();
                break;

            case R.id.applyFilters:
                applyFiltersDialog();
                break;

            case R.id.openOnly:
                if (item.isChecked())
                {
                    // un-check item -> change prefs to false -> reprint markers
                    item.setChecked(false);
                    fav_preferences.edit().putBoolean("openOnly", false).apply();
                    loadFragment();
                }
                else
                {
                    // check item -> change prefs to true -> reprint markers
                    item.setChecked(true);
                    fav_preferences.edit().putBoolean("openOnly", true).apply();
                    loadFragment();
                }
                break;

            case R.id.clearDB:
                clearDatabaseDialog();
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


    /*
     *  Create the dialog window for applying filters
     */
    private void applyFiltersDialog()
    {
        filter_selections = new boolean[]{
                fav_preferences.getBoolean("showBars", true),
                fav_preferences.getBoolean("showShops", true),
                fav_preferences.getBoolean("showGasStations", true),
                fav_preferences.getBoolean("showKiosks", true),
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        TextView title = new TextView(this);
        title.setText("Apply Filters");
        title.setTextSize(23);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0,25,0,25);
        title.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-L.ttf"));

        builder.setCustomTitle(title);

        builder.setMultiChoiceItems(R.array.applyFilters_options, filter_selections,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            switch (which) {
                                case 0:
                                    fav_preferences.edit().putBoolean("showBars", true).apply();
                                    break;
                                case 1:
                                    fav_preferences.edit().putBoolean("showShops", true).apply();
                                    break;
                                case 2:
                                    fav_preferences.edit().putBoolean("showGasStations", true).apply();
                                    break;
                                case 3:
                                    fav_preferences.edit().putBoolean("showKiosks", true).apply();
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            switch (which) {
                                case 0:
                                    fav_preferences.edit().putBoolean("showBars", false).apply();
                                    break;
                                case 1:
                                    fav_preferences.edit().putBoolean("showShops", false).apply();
                                    break;
                                case 2:
                                    fav_preferences.edit().putBoolean("showGasStations", false).apply();
                                    break;
                                case 3:
                                    fav_preferences.edit().putBoolean("showKiosks", false).apply();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                })

                .setPositiveButton(R.string.applyFilters_apply, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        loadFragment();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);

                // Set font for Buttons
                Typeface face=Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-L.ttf");
                btnPositive.setTypeface(face);
            }
        });

        alert.show();

        //builder.show();
    }


    // -------------------------------------- Options Menu -----------------------------------------
    // ---------------------------------------------------------------------------------------------
    // Methods:
    //
    // showTutorial()
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------


    /*
     *  this is a very simple tutorial for the user, to explore the features of this app
     */
    private void showTutorial(String hint)
    {
        AlertDialog.Builder tutorial = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        // Inflate the view
        View view = inflater.inflate(R.layout.dialog_hint, null);

        // Initialize Layout
        final TextView hintText = (TextView) view.findViewById(R.id.hintTxt);
        final CheckBox showHints = (CheckBox) view.findViewById(R.id.showHints);

        // enable Checkbox
        showHints.setChecked(app_preferences.getBoolean("showHints",true));

        TextView title = new TextView(this);
        title.setText("Favorites Features");
        title.setTextSize(23);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0,25,0,25);
        title.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-L.ttf"));

        tutorial.setCustomTitle(title);

        // set hint Text
        hintText.setText(hint);

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
                                    if (!app_preferences.getBoolean("showHints", true))
                                    {
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

                // Set font for Buttons
                Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-L.ttf");
                btnPositive.setTypeface(face);
                btnNegative.setTypeface(face);
            }
        });

        alert.show();

        //tutorial.show();
    }


    /*
     *  Test Environment to see if functionality is granted
     */
    private void setUpTestList()
    {
        BeerSpotDays days = new BeerSpotDays();
        days.addDay(1,100,1,2300);
        days.addDay(2,1,2,2300);
        days.addDay(3,100,3,2300);
        days.addDay(4,100,4,2300);
        days.addDay(5,100,5,2400);
        days.addDay(6,100,6,2300);
        days.addDay(7,100,7,2300);

        BeerSpotDays days2 = new BeerSpotDays();
        days2.addDay(5,2100,6,400);
        days2.addDay(6,2100,7,400);

        ArrayList<Comment> test = new ArrayList<>();
        test.add(new Comment(5,"DB TEST COMMENT"));

        spotlist1 = new ArrayList<>();
        spotlist1.add(new BeerSpot("sd1","DB Spot 1","Bar","Info", "München","Sesamstr",days,"Gustl","3Euro","12",48.15072666,11.59566164,"now",1, test));
        spotlist1.add(new BeerSpot("sdasd2","DB Spot 2","Gas Station","Info", "München","Sesamstr",days2,"Gustl","3Euro","12",48.15056919,11.59634829,"now",2, test));
        spotlist1.add(new BeerSpot("3asdasd","DB Spot 3","Bar","Info", "München","Sesamstr",days,"Gustl","3Euro","12",48.15035445,11.59546852,"now",3, test));
        spotlist1.add(new BeerSpot("asdasd4","DB Spot 4","Kiosk","Info", "München","Sesamstr",days2,"Gustl","3Euro","12",48.15104162,11.59448147,"now",1, test));
        spotlist1.add(new BeerSpot("asdasd5","DB Spot 5","Kiosk","Info", "München","Sesamstr",days,"Gustl","3Euro","12",48.15187193,11.5952754,"now",2, test));
        spotlist1.add(new BeerSpot("asdasd6","DB Spot 6","Shop","Info", "München","Sesamstr",days,"Gustl","3Euro","12",48.15188625,11.59581184,"now",3, test));
        spotlist1.add(new BeerSpot("asdasd7","DB Spot 7","Bar","Info", "München","Sesamstr",days2,"Gustl","3Euro","12",48.15192919,11.59645557,"now",1, test));
        spotlist1.add(new BeerSpot("8sdsds","DB Spot 8","Shop","Info", "München","Sesamstr",days2,"Gustl","3Euro","12",48.15178604,11.5969491,"now",2, test));
        spotlist1.add(new BeerSpot("assd9","DB Spot 9","Gas Station","Info", "München","Sesamstr",days,"Gustl","3Euro","12",48.15162856,11.59752846,"now",3, test));
        spotlist1.add(new BeerSpot("1sdsa0","DB Spot 10","Kiosk","Info", "München","Sesamstr",days,"Gustl","3Euro","12",48.15118477,11.5979147,"now",1, test));
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

        ((TextView)v.findViewById(R.id.titleFragment1)).setText("Favorites");

        //assign the view to the actionbar
        this.getActionBar().setCustomView(v);
    }
}

