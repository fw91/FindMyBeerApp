package com.msp.findMyBeer.Activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.msp.findMyBeer.Activities.Fragments.SubmitFormFragment1;
import com.msp.findMyBeer.Activities.Fragments.SubmitFormFragment2;
import com.msp.findMyBeer.Activities.Fragments.SubmitFormFragment3;
import com.msp.findMyBeer.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

/**
 * The Submit Activity
 *
 * @author Cenk Canpolat, Florian Fincke
 */
public class SubmitActivity extends FragmentActivity {


    public MyViewPager mViewPager;
    TabsAdapter mTabsAdapter;

    // Preferences of Application
    public static final String APP_PREFERENCES = "AppPreferences";
    SharedPreferences app_preferences;

    // Tutorial Text
    private static final String TUTORIAL_PAGE1 =
            "Welcome to the submit form.\n\n" +
                    "You are able to send us your favorite locations.\n\n" +
                    "All you have to do is fill out the form we provice";

    private static final String TUTORIAL_PAGE2 =
            "On the first page you have to enter basic information about the location\n\n"
                    + "Press the next button at the end of this page if you filled in every field\n\n" +
                    "There are two different ways to send the opening times\n" +
                    "-Enter manually-\n" +
                    "-Upload photo-";

    private static final String TUTORIAL_PAGE3 =
            "-Enter manually-\n\n" +
                    "There are four fields for every day you want to add.\n" +
                    "You can add up to seven days by pressing the add day button\n" +
                    "In case a location is opened after midnight, you have to choose two weekdays for every day you add.\n" +
                    "Example: A bar opened on friday at 18:00 and closes on saturday at 05:00.\n";

    private static final String TUTORIAL_PAGE4 =

            "-Upload photo-\n\n" +
                    "You can easily take a photo of the opening times and upload it.\n" +
                    "If you do that, we fill in the form for you.";

    private static final String HINT_REMINDER =
            "Hints are disabled\n\n" +
                    "(to re-enable, check \"enable hints\" in the Main Menu)";

    String TabOfSubmitFragment2, TabOfSubmitFragment3;

    public void setTabOfSubmitFragment2(String t) {
        TabOfSubmitFragment2 = t;
    }

    public void setTabOfSubmitFragment3(String t) {
        TabOfSubmitFragment3 = t;
    }

    public String getTabOfSubmitFragment2() {
        return TabOfSubmitFragment2;
    }

    public String getTabOfSubmitFragment3() {
        return TabOfSubmitFragment3;
    }

    static final int CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE = 1777;

    public String imageStr, bitmappath;



    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewPager = new MyViewPager(this);
        mViewPager.setId(R.id.pager);
        setContentView(mViewPager);

        createCustomActionBarTitle();

        final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        app_preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        mTabsAdapter = new TabsAdapter(this, mViewPager);
        mTabsAdapter.addTab(bar.newTab().setText("General Information"),
                SubmitFormFragment1.class, null);
        mTabsAdapter.addTab(bar.newTab().setText("Opening Hours"),
                SubmitFormFragment2.class, null);
        mTabsAdapter.addTab(bar.newTab().setText("Review"),
                SubmitFormFragment3.class, null);

        // if hints are enabled -> showTutorial
        if (app_preferences.getBoolean("showHints", true)) {
            showTutorial(TUTORIAL_PAGE1);
        }


        if (savedInstanceState != null) {
            bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }

    //Saves the image and creates a Base64-String from PNG
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Check that request code matches ours:
        if (requestCode == CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE) {
            //Get our saved file into a bitmap object:
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
            Bitmap bitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), 800, 600);
            this.bitmappath = file.getAbsolutePath();
            String TabOfFragment3 = getTabOfSubmitFragment3();
            SubmitFormFragment3 fragment3 = (SubmitFormFragment3) getSupportFragmentManager().findFragmentByTag(TabOfFragment3);

            //Convert bitmap to png, then to base64
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
            byte[] byte_arr = stream.toByteArray();
            this.imageStr = " " + Base64.encodeToString(byte_arr, Base64.DEFAULT);
            fragment3.setBase64String(imageStr);
        }
    }

    //resizes the image
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    //handles Tabs of Activity
    public static class TabsAdapter extends FragmentPagerAdapter
            implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

        private final Context mContext;
        private final ActionBar mActionBar;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        static final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(Class<?> _class, Bundle _args) {
                clss = _class;
                args = _args;
            }
        }

        public TabsAdapter(FragmentActivity activity, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
            mActionBar = activity.getActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(clss, args);
            tab.setTag(info);
            tab.setTabListener(this);
            mTabs.add(info);
            mActionBar.addTab(tab);
            notifyDataSetChanged();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(), info.args);
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
        }

        return true;
    }

    public class MyViewPager extends ViewPager {

        public MyViewPager(Context context) {
            super(context);
        }

        public MyViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent arg0) {
            // Never allow swiping to switch between pages
            return false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // Never allow swiping to switch between pages
            return false;
        }
    }

    private void createCustomActionBarTitle() {
        this.getActionBar().setDisplayShowCustomEnabled(true);
        this.getActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.custom_action_bar, null);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-M.ttf");
        ((TextView) v.findViewById(R.id.titleFragment1)).setTypeface(face);

        ((TextView) v.findViewById(R.id.titleFragment1)).setText("Submit");

        //assign the view to the actionbar
        this.getActionBar().setCustomView(v);
    }

    /*
    *  this is a very simple tutorial for the user, to explore the features of this app
    */
    private void showTutorial(String hint) {
        AlertDialog.Builder tutorial = new AlertDialog.Builder(this);
        TextView title = new TextView(this);
        title.setText(R.string.setRadius_title);
        title.setTextSize(23);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0,25,0,25);
        title.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-L.ttf"));
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        // Inflate the view
        View view = inflater.inflate(R.layout.dialog_hint, null);

        // Initialize Layout
        final TextView hintText = (TextView) view.findViewById(R.id.hintTxt);
        final CheckBox showHints = (CheckBox) view.findViewById(R.id.showHints);

        // enable Checkbox
        showHints.setChecked(app_preferences.getBoolean("showHints", true));

        // set hint Text
        hintText.setText(hint);

        tutorial.setView(view)
                .setTitle("Submit tutorial")
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
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        switch (hintText.getText().toString()) {
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

        tutorial.show();
    }
}
