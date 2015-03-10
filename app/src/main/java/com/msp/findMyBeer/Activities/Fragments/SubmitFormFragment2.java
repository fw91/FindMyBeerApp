package com.msp.findMyBeer.Activities.Fragments;


import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.msp.findMyBeer.Activities.SubmitActivity;
import com.msp.findMyBeer.R;

import java.util.ArrayList;

/**
 *  Submit Page 2
 *
 *  @author Cenk Canpolat
 */
public class SubmitFormFragment2 extends Fragment {
    Button page2next, page2previous, addDay, removeDay;
    String name, address, city, locType, beerType, beerPrice, info;
    Spinner day1f, day1t, day2f, day2t, day3f, day3t, day4f, day4t, day5f, day5t, day6f, day6t, day7f, day7t;
    EditText time1f, time1t, time2f, time2t, time3f, time3t, time4f, time4t, time5f, time5t, time6f, time6t, time7f, time7t;
    LinearLayout row2, row3, row4, row5, row6, row7;

    int mHour, mMin;

    int daysAdded;
    ArrayList<Integer> openDays = new ArrayList<Integer>();
    ArrayList<Integer> closedDays = new ArrayList<Integer>();
    ArrayList<Integer> openTimes = new ArrayList<Integer>();
    ArrayList<Integer> closedTimes = new ArrayList<Integer>();


    Integer[] items = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_submit_p2, container, false);

        row2 = (LinearLayout) myFragmentView.findViewById(R.id.row2);
        row3 = (LinearLayout) myFragmentView.findViewById(R.id.row3);
        row4 = (LinearLayout) myFragmentView.findViewById(R.id.row4);
        row5 = (LinearLayout) myFragmentView.findViewById(R.id.row5);
        row6 = (LinearLayout) myFragmentView.findViewById(R.id.row6);
        row7 = (LinearLayout) myFragmentView.findViewById(R.id.row7);

        day1f = (Spinner) myFragmentView.findViewById(R.id.day1f);
        time1f = (EditText) myFragmentView.findViewById(R.id.time1f);
        day1t = (Spinner) myFragmentView.findViewById(R.id.day1t);
        time1t = (EditText) myFragmentView.findViewById(R.id.time1t);

        day2f = (Spinner) myFragmentView.findViewById(R.id.day2f);
        time2f = (EditText) myFragmentView.findViewById(R.id.time2f);
        day2t = (Spinner) myFragmentView.findViewById(R.id.day2t);
        time2t = (EditText) myFragmentView.findViewById(R.id.time2t);

        day3f = (Spinner) myFragmentView.findViewById(R.id.day3f);
        time3f = (EditText) myFragmentView.findViewById(R.id.time3f);
        day3t = (Spinner) myFragmentView.findViewById(R.id.day3t);
        time3t = (EditText) myFragmentView.findViewById(R.id.time3t);

        day4f = (Spinner) myFragmentView.findViewById(R.id.day4f);
        time4f = (EditText) myFragmentView.findViewById(R.id.time4f);
        day4t = (Spinner) myFragmentView.findViewById(R.id.day4t);
        time4t = (EditText) myFragmentView.findViewById(R.id.time4t);

        day5f = (Spinner) myFragmentView.findViewById(R.id.day5f);
        time5f = (EditText) myFragmentView.findViewById(R.id.time5f);
        day5t = (Spinner) myFragmentView.findViewById(R.id.day5t);
        time5t = (EditText) myFragmentView.findViewById(R.id.time5t);

        day6f = (Spinner) myFragmentView.findViewById(R.id.day6f);
        time6f = (EditText) myFragmentView.findViewById(R.id.time6f);
        day6t = (Spinner) myFragmentView.findViewById(R.id.day6t);
        time6t = (EditText) myFragmentView.findViewById(R.id.time6t);

        day7f = (Spinner) myFragmentView.findViewById(R.id.day7f);
        time7f = (EditText) myFragmentView.findViewById(R.id.time7f);
        day7t = (Spinner) myFragmentView.findViewById(R.id.day7t);
        time7t = (EditText) myFragmentView.findViewById(R.id.time7t);


        time1f.setOnTouchListener(onTouchListener);
        time1t.setOnTouchListener(onTouchListener);
        time2f.setOnTouchListener(onTouchListener);
        time2t.setOnTouchListener(onTouchListener);
        time3f.setOnTouchListener(onTouchListener);
        time3t.setOnTouchListener(onTouchListener);
        time4f.setOnTouchListener(onTouchListener);
        time4t.setOnTouchListener(onTouchListener);
        time5f.setOnTouchListener(onTouchListener);
        time5t.setOnTouchListener(onTouchListener);
        time6f.setOnTouchListener(onTouchListener);
        time6t.setOnTouchListener(onTouchListener);
        time7f.setOnTouchListener(onTouchListener);
        time7t.setOnTouchListener(onTouchListener);


        addDay = (Button) myFragmentView.findViewById(R.id.addDay_btn);
        removeDay = (Button) myFragmentView.findViewById(R.id.removeDay_btn);

        daysAdded = 0;
        addDay.setOnClickListener(addDayOnClickListener);
        removeDay.setOnClickListener(removeDayOnClickListener);

        page2next = (Button) myFragmentView.findViewById(R.id.page2Next_btn);
        page2next.setOnClickListener(page2NextButtonOnClickListener);

        page2previous = (Button) myFragmentView.findViewById(R.id.page2Back_btn);
        page2previous.setOnClickListener(Page2PreviousButtonOnClickListener);


        String myTag = getTag();

        ((SubmitActivity) getActivity()).setTabOfSubmitFragment2(myTag);
        return myFragmentView;
    }

    View.OnClickListener addDayOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (daysAdded) {
                case 0:
                    row2.setVisibility(View.VISIBLE);
                    removeDay.setVisibility(View.VISIBLE);
                    daysAdded++;
                    break;
                case 1:
                    row3.setVisibility(View.VISIBLE);
                    daysAdded++;
                    break;
                case 2:
                    row4.setVisibility(View.VISIBLE);
                    daysAdded++;
                    break;
                case 3:
                    row5.setVisibility(View.VISIBLE);
                    daysAdded++;
                    break;
                case 4:
                    row6.setVisibility(View.VISIBLE);
                    daysAdded++;
                    break;
                case 5:
                    row7.setVisibility(View.VISIBLE);
                    addDay.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };

    View.OnClickListener removeDayOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (daysAdded) {
                case 6:
                    row7.setVisibility(View.INVISIBLE);
                    addDay.setVisibility(View.VISIBLE);
                    daysAdded--;
                    break;
                case 5:
                    row6.setVisibility(View.INVISIBLE);
                    daysAdded--;
                    break;
                case 4:
                    row5.setVisibility(View.INVISIBLE);
                    daysAdded--;
                    break;
                case 3:
                    row4.setVisibility(View.INVISIBLE);
                    daysAdded--;
                    break;
                case 2:
                    row3.setVisibility(View.INVISIBLE);
                    daysAdded--;
                    break;
                case 1:
                    row2.setVisibility(View.INVISIBLE);
                    daysAdded--;
                    break;
            }
        }
    };
    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                // Launch Time Picker Dialog
                TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // Display Selected time in textbox

                                mHour = hourOfDay;
                                mMin = minute;
                                String curTime = String.format("%02d%02d", hourOfDay, minute);
                                switch (v.getId()) {
                                    case R.id.time1f:
                                        time1f.setText(curTime);
                                        break;
                                    case R.id.time1t:
                                        time1t.setText(curTime);
                                        break;
                                    case R.id.time2f:
                                        time2f.setText(curTime);
                                        break;
                                    case R.id.time2t:
                                        time2t.setText(curTime);
                                        break;
                                    case R.id.time3f:
                                        time3f.setText(curTime);
                                        break;
                                    case R.id.time3t:
                                        time3t.setText(curTime);
                                        break;
                                    case R.id.time4f:
                                        time4f.setText(curTime);
                                        break;
                                    case R.id.time4t:
                                        time4t.setText(curTime);
                                        break;
                                    case R.id.time5f:
                                        time5f.setText(curTime);
                                        break;
                                    case R.id.time5t:
                                        time5t.setText(curTime);
                                        break;
                                    case R.id.time6f:
                                        time6f.setText(curTime);
                                        break;
                                    case R.id.time6t:
                                        time6t.setText(curTime);
                                        break;
                                    case R.id.time7f:
                                        time7f.setText(curTime);
                                        break;
                                    case R.id.time7t:
                                        time7t.setText(curTime);
                                        break;
                                }
                            }
                        }, mHour, mMin, true);


                tpd.show();
            }
            return true;
        }
    };

    public void nextPageIfCameraUsed() {
        String TabOfFragment3 = ((SubmitActivity) getActivity()).getTabOfSubmitFragment3();
        SubmitFormFragment3 fragment3 = (SubmitFormFragment3) getActivity().getSupportFragmentManager().findFragmentByTag(TabOfFragment3);
        fragment3.setInput(name, address, city, locType, beerType, beerPrice, info);
        fragment3.cameraWasUsed();
        ViewPager aViewPager = ((SubmitActivity) getActivity()).mViewPager;
        aViewPager.setCurrentItem(2, true);
    }

    View.OnClickListener page2NextButtonOnClickListener
            = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {


            String TabOfFragment3 = ((SubmitActivity) getActivity()).getTabOfSubmitFragment3();

            SubmitFormFragment3 fragment3 = (SubmitFormFragment3) getActivity().getSupportFragmentManager().findFragmentByTag(TabOfFragment3);


            fragment3.setInput(name, address, city, locType, beerType, beerPrice, info);
            fragment3.cameraNotUsed();


            // putting
            switch (daysAdded) {
                case 0:
                    try {
                        openDays.add(dayStringToInt(day1f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day1t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time1f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time1t.getText().toString()));
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(),
                                "Please enter time",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
                case 1:
                    try {
                        openDays.add(dayStringToInt(day1f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day1t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time1f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time1t.getText().toString()));
                        openDays.add(dayStringToInt(day2f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day2t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time2f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time2t.getText().toString()));
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(),
                                "Please enter time",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
                case 2:
                    try {
                        openDays.add(dayStringToInt(day1f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day1t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time1f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time1t.getText().toString()));
                        openDays.add(dayStringToInt(day2f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day2t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time2f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time2t.getText().toString()));
                        openDays.add(dayStringToInt(day3f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day3t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time3f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time3t.getText().toString()));
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(),
                                "Please enter time",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
                case 3:
                    try {
                        openDays.add(dayStringToInt(day1f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day1t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time1f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time1t.getText().toString()));
                        openDays.add(dayStringToInt(day2f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day2t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time2f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time2t.getText().toString()));
                        openDays.add(dayStringToInt(day3f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day3t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time3f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time3t.getText().toString()));
                        openDays.add(dayStringToInt(day4f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day4t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time4f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time4t.getText().toString()));
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(),
                                "Please enter time",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
                case 4:
                    try {
                        openDays.add(dayStringToInt(day1f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day1t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time1f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time1t.getText().toString()));
                        openDays.add(dayStringToInt(day2f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day2t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time2f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time2t.getText().toString()));
                        openDays.add(dayStringToInt(day3f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day3t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time3f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time3t.getText().toString()));
                        openDays.add(dayStringToInt(day4f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day4t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time4f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time4t.getText().toString()));
                        openDays.add(dayStringToInt(day5f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day5t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time5f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time5t.getText().toString()));
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(),
                                "Please enter time",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
                case 5:
                    try {
                        openDays.add(dayStringToInt(day1f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day1t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time1f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time1t.getText().toString()));
                        openDays.add(dayStringToInt(day2f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day2t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time2f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time2t.getText().toString()));
                        openDays.add(dayStringToInt(day3f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day3t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time3f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time3t.getText().toString()));
                        openDays.add(dayStringToInt(day4f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day4t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time4f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time4t.getText().toString()));
                        openDays.add(dayStringToInt(day5f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day5t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time5f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time5t.getText().toString()));
                        openDays.add(dayStringToInt(day6f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day6t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time6f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time6t.getText().toString()));
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(),
                                "Please enter time",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
                case 6:
                    try {
                        openDays.add(dayStringToInt(day1f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day1t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time1f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time1t.getText().toString()));
                        openDays.add(dayStringToInt(day2f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day2t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time2f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time2t.getText().toString()));
                        openDays.add(dayStringToInt(day3f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day3t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time3f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time3t.getText().toString()));
                        openDays.add(dayStringToInt(day4f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day4t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time4f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time4t.getText().toString()));
                        openDays.add(dayStringToInt(day5f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day5t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time5f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time5t.getText().toString()));
                        openDays.add(dayStringToInt(day6f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day6t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time6f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time6t.getText().toString()));
                        openDays.add(dayStringToInt(day7f.getSelectedItem().toString()));
                        closedDays.add(dayStringToInt(day7t.getSelectedItem().toString()));
                        openTimes.add(Integer.parseInt(time7f.getText().toString()));
                        closedTimes.add(Integer.parseInt(time7t.getText().toString()));
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(),
                                "Please enter time",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
            }
            ;

            fragment3.setBeerSpotDays(openDays, openTimes, closedDays, closedTimes);


            ViewPager aViewPager = ((SubmitActivity) getActivity()).mViewPager;
            aViewPager.setCurrentItem(2, true);
        }
    };

    View.OnClickListener Page2PreviousButtonOnClickListener
            = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {

            ViewPager aViewPager = ((SubmitActivity) getActivity()).mViewPager;
            aViewPager.setCurrentItem(0, true);
        }
    };

    public void getInput(String nameInput, String addressInput, String cityInput, String locTypeInput, String beerTypeInput, String beerPriceInput, String infoInput) {
        name = nameInput;
        address = addressInput;
        city = cityInput;
        info = infoInput;
        locType = locTypeInput;
        beerType = beerTypeInput;
        beerPrice = beerPriceInput;
    }

    public int dayStringToInt(String day) {
        if (day.equals("Mon")) {
            return 1;
        } else if (day.equals("Tue")) {
            return 2;
        } else if (day.equals("Wed")) {
            return 3;
        } else if (day.equals("Thu")) {
            return 4;
        } else if (day.equals("Fri")) {
            return 5;
        } else if (day.equals("Sat")) {
            return 6;
        } else if (day.equals("Sun")) {
            return 7;
        } else return 0;
    }

    public void clearTimes() {
        openDays.clear();
        closedDays.clear();
        openTimes.clear();
        closedTimes.clear();
    }
}