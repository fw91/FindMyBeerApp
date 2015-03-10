package com.msp.findMyBeer.Activities.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.TileOverlay;
import com.msp.findMyBeer.Activities.SubmitActivity;
import com.msp.findMyBeer.R;

import java.io.File;

/**
 *  Submit Page 1
 *
 *  @author Cenk Canpolat
 */
public class SubmitFormFragment1
        extends Fragment {

    EditText name_input, address_input, city_input, info_input;
    Spinner locType_input, beerType_input;
    NumberPicker eu_input, ct1_input, ct2_input;
    Button nextBtn;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_submit_p1, container, false);

        name_input = (EditText) myFragmentView.findViewById(R.id.name_input);
        locType_input = (Spinner) myFragmentView.findViewById(R.id.locType_input);
        address_input = (EditText) myFragmentView.findViewById(R.id.address_input);
        city_input = (EditText) myFragmentView.findViewById(R.id.city_input);
        beerType_input = (Spinner) myFragmentView.findViewById(R.id.beerType_input);
        info_input = (EditText) myFragmentView.findViewById(R.id.info_input);
        eu_input = (NumberPicker) myFragmentView.findViewById(R.id.eu_input);
        ct1_input = (NumberPicker) myFragmentView.findViewById(R.id.ct1_input);
        ct2_input = (NumberPicker) myFragmentView.findViewById(R.id.ct2_input);

        locType_input.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                locType_input.clearFocus();
                address_input.requestFocus();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        beerType_input.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                beerType_input.clearFocus();
                info_input.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        eu_input.setMinValue(0);
        eu_input.setMaxValue(9);
        ct1_input.setMinValue(0);
        ct1_input.setMaxValue(9);
        ct2_input.setMinValue(0);
        ct2_input.setMaxValue(9);

        nextBtn = (Button) myFragmentView.findViewById(R.id.page1Next_btn);
        nextBtn.setOnClickListener(A_enterOnClickListener);

        return myFragmentView;
    }

    // Check if no view has focus:
    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    OnClickListener A_enterOnClickListener
            = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            //check if user wrote something in Edittext fields
            hideKeyboard();
            if (name_input.getText().toString().isEmpty()) {
                name_input.setError("Location name is required!");
                name_input.setHint("Please enter location name");
                Toast.makeText(getActivity(), "please enter location name", Toast.LENGTH_SHORT).show();
            } else if
                    (address_input.getText().toString().trim().equals("")) {
                address_input.setError("Address is required!");
                address_input.setHint("Please enter address");
                Toast.makeText(getActivity(), "Please enter address", Toast.LENGTH_SHORT).show();
            } else if
                    (city_input.getText().toString().trim().equals("")) {
                city_input.setError("city is required!");
                city_input.setHint("Please enter city");
                Toast.makeText(getActivity(), "Please enter city", Toast.LENGTH_SHORT).show();
            } else if (locType_input.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(), "Please select location type", Toast.LENGTH_SHORT).show();
                return;
            } else if (beerType_input.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(), "Please select beer", Toast.LENGTH_SHORT).show();
                return;
            } else if (info_input.getText().toString().isEmpty()) {
                info_input.setError("Info is required!");
                info_input.setHint("Please enter additional information");
                Toast.makeText(getActivity(), "Please enter additional information", Toast.LENGTH_SHORT).show();
            } else {
                String name = name_input.getText().toString();
                String address = address_input.getText().toString();
                String city = city_input.getText().toString();
                String info = info_input.getText().toString();
                String locType = locType_input.getSelectedItem().toString();
                String beerType = beerType_input.getSelectedItem().toString();
                String beerPrice = eu_input.getValue() + "," + ct1_input.getValue() + "" + ct2_input.getValue() + "€";


                String TabOfFormFragment2 = ((SubmitActivity) getActivity()).getTabOfSubmitFragment2();
                SubmitFormFragment2 formFragment2 = (SubmitFormFragment2) getActivity().getSupportFragmentManager().findFragmentByTag(TabOfFormFragment2);

                formFragment2.getInput(name, address, city, locType, beerType, beerPrice, info);

                SubmitTimeDialog newFragment = new SubmitTimeDialog();
                newFragment.show(getActivity().getFragmentManager(), "Submit");
            }
        }
    };


    //Dialog to choose between manual input and image capture to submit openingtimes
    public static class SubmitTimeDialog extends DialogFragment {

        static final int CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE = 1777;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("How would you like to submit opening times?").setItems(R.array.submitTimes_type, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    switch (which) {
                        case 0: //Manually enter opening times
                            ViewPager aViewPager = ((SubmitActivity) getActivity()).mViewPager;
                            aViewPager.setCurrentItem(1, true);

                            break;

                        case 1: //Open Camera

                            String TabOfFormFragment2 = ((SubmitActivity) getActivity()).getTabOfSubmitFragment2();
                            SubmitFormFragment2 fragment2 = (SubmitFormFragment2) ((SubmitActivity) getActivity()).getSupportFragmentManager().findFragmentByTag(TabOfFormFragment2);
                            ViewPager aaViewPager = ((SubmitActivity) getActivity()).mViewPager;
                            aaViewPager.setCurrentItem(1, true);
                            fragment2.nextPageIfCameraUsed();

                            dispatchTakePictureIntent();
                            break;
                        default:
                            break;
                    }
                }
            });
            return builder.create();
        }

        private void dispatchTakePictureIntent() {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            getActivity().startActivityForResult(intent, CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE);
        }


    }
}
