package com.msp.findMyBeer.Activities.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.msp.findMyBeer.Database.BeerSpot;
import com.msp.findMyBeer.Database.Comment;
import com.msp.findMyBeer.Database.DatabaseHandler;
import com.msp.findMyBeer.MyDDPState;
import com.msp.findMyBeer.R;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 *  Create and inflate the MapSpotDetails-Dialog, it has the option of adding the selected spot
 *  into the favorite BeerSpot Database
 *
 *  @author Florian Wirth
 */
public class MapSpotDetailsBig extends DialogFragment
{
    String statusText, jsonText;
    BeerSpot currentSpot;
    Typeface face;

    // load spots_disabled prefs, to verify a spot has not already been checked in
    SharedPreferences spots_disabled;

    public MapSpotDetailsBig()
    {
        super();
    }

    public static MapSpotDetailsBig newInstance(BeerSpot spot)
    {

        MapSpotDetailsBig f = new MapSpotDetailsBig();

        Bundle args = new Bundle();

        String spotJSON = spot.getJSON().toString();
        args.putString("json",spotJSON);

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

        spots_disabled  = getActivity().getPreferences(0);

        statusText = getArguments().getString("status");
        jsonText = getArguments().getString("json");

        // get the spot, by using JSON-functionallity
        JSONObject spotJSON = new JSONObject();

        try{
            spotJSON = new JSONObject(jsonText);
        }catch (Exception e){
            e.printStackTrace();
        }
        currentSpot = new BeerSpot(spotJSON);


    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Set custom Layout for the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        face = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Ubuntu-L.ttf");

        View v = inflater.inflate(R.layout.fragment_map_spotdetails_big, null);

        // Inflate the Layout
        View imgView = v.findViewById(R.id.detailsIcon);
        View nameView = v.findViewById(R.id.nameDVVV);
        View typeView = v.findViewById(R.id.typeDVVV);
        View infoView = v.findViewById(R.id.infoDVVV);
        View statusView = v.findViewById(R.id.statusDVVV);
        View cityView = v.findViewById(R.id.cityDVVV);
        View addressView = v.findViewById(R.id.addressDVVV);
        View beerView = v.findViewById(R.id.beerDVVV);
        View priceView = v.findViewById(R.id.priceDVVV);

        // Fill the Layout
        ((ImageView)imgView).setImageBitmap(setIcon());
        ((TextView)nameView).setText(currentSpot.getName());
        ((TextView)typeView).setText(currentSpot.getType());
        ((TextView)infoView).setText(currentSpot.getInfo());
        ((TextView)statusView).setText(statusText);
        ((TextView)cityView).setText(currentSpot.getCity());
        ((TextView)addressView).setText(currentSpot.getAddress());
        ((TextView)beerView).setText(currentSpot.getBeerName());
        ((TextView)priceView).setText(currentSpot.getBeerPrice());

        // Get the buttons
        Button showTimes = (Button) v.findViewById(R.id.show_times);
        Button showComments = (Button) v.findViewById(R.id.show_comments);
        Button addComment = (Button) v.findViewById(R.id.add_comment);

        showTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimes();
            }
        });

        showComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComments();
            }
        });

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });

        // Add the option of adding the selected spot per Button-Click
        builder.setView(v)
                // Add action buttons
                .setPositiveButton("Check In", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Close Dialog
                        if (!spots_disabled.getBoolean("checked" + currentSpot.getId(), false)) {
                            // Check in (send checked to server)
                            MyDDPState.getInstance().wasHere(currentSpot.getId());
                            Toast.makeText(getActivity().getApplicationContext(), "Checked in", Toast.LENGTH_SHORT).show();
                            spots_disabled.edit().putBoolean("checked" + currentSpot.getId(), true).apply();
                        }
                    }
                })
                .setNeutralButton("Add to favorites", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // add spot into DB
                        DatabaseHandler DH = new DatabaseHandler(getActivity().getApplicationContext());
                        DH.insertSpot(currentSpot);
                        Toast.makeText(getActivity().getApplicationContext(), currentSpot.getName() + " added", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        // Close Dialog
                    }
                });

        // add animation
        Animation fadeAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in);
        v.startAnimation(fadeAnimation);

        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);
                Button btnNeutral = alert.getButton(Dialog.BUTTON_NEUTRAL);
                Button btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE);

                // Set font for Buttons
                Typeface face=Typeface.createFromAsset(getActivity().getAssets(),"fonts/Ubuntu-L.ttf");
                btnPositive.setTypeface(face);
                btnNeutral.setTypeface(face);
                btnNegative.setTypeface(face);
            }
        });

        return alert;
    }


    /*
     *  Open a Dialog to show the Timetable
     */
    private void showTimes()
    {
        AlertDialog.Builder times = new AlertDialog.Builder(getActivity());

        TextView title = new TextView(getActivity());
        title.setText(currentSpot.getName() + " - Timetable");
        title.setTextSize(23);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0,25,0,25);
        title.setTypeface(face);

        times.setCustomTitle(title);

        //times.setTitle(currentSpot.getName() + " - Timetable");

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_map_spotdetails_big_times, null);
        View timesView = view.findViewById(R.id.timesT);

        // Set the times
        ((TextView)timesView).setText(currentSpot.daysToString());

        times.setView(view)
             .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int id) {
                     // Close Times View
                 }
             }).create();

        // add animation
        Animation fadeAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in);
        view.startAnimation(fadeAnimation);

        final AlertDialog alert = times.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);

                // Set font for Buttons
                Typeface face=Typeface.createFromAsset(getActivity().getAssets(),"fonts/Ubuntu-L.ttf");
                btnPositive.setTypeface(face);
            }
        });

        alert.show();

        //times.show();
    }


    /*
     *  Open a Dialog to show the Comments
     */
    private void showComments()
    {
        AlertDialog.Builder comments = new AlertDialog.Builder(getActivity());

        TextView title = new TextView(getActivity());
        title.setText(currentSpot.getName() + " - Comments");
        title.setTextSize(23);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0,25,0,25);
        title.setTypeface(face);

        comments.setCustomTitle(title);
        //comments.setTitle(currentSpot.getName() + " - Comments");

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_map_spotdetails_big_comments, null);

        TextView avgRating = (TextView)view.findViewById(R.id.comments_rating);
        ListView commentsList = (ListView) view.findViewById(R.id.comments_list);

        // If there are any Comments to be shown
        if (currentSpot.getComments() != null)
        {
            int ratingSum = 0;
            double average = 0;

            // calculate the average Rating, based on CommentsSize
            for (int i=0;i<currentSpot.getComments().size();i++)
            {
                ratingSum += currentSpot.getComments().get(i).getRating();
            }

            NumberFormat formatter = new DecimalFormat("#0.0");
            average = (double)ratingSum/currentSpot.getComments().size();

            avgRating.setText("Average Rating: " + formatter.format(average) + "/5");


            CommentsAdapter adapter = new CommentsAdapter(this.getActivity().getApplicationContext(),
                    currentSpot.getComments());

            commentsList.setAdapter(adapter);
        }
        else
        {
            avgRating.setText("There are no Comments to be shown yet.\n Be the first one to add a new Comment");
        }

        comments.setView(view)
                .setPositiveButton("Back", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // Close Comments View
                    }
                }).create();

        // add animation
        Animation fadeAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in);
        view.startAnimation(fadeAnimation);

        final AlertDialog alert = comments.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);

                // Set font for Buttons
                Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Ubuntu-L.ttf");
                btnPositive.setTypeface(face);
            }
        });

        alert.show();

        //comments.show();
    }


    /*
     *  Open a Dialog to submit a new Comment
     */
    private void addComment() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        TextView title = new TextView(getActivity());
        title.setText("Write a Comment");
        title.setTextSize(23);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 25, 0, 25);
        title.setTypeface(face);

        builder.setCustomTitle(title);
        //builder.setTitle("Write a Comment");

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_map_spotdetails_big_addcomment, null);

        final Spinner rating = (Spinner)view.findViewById(R.id.rating);
        final EditText commentInput = (EditText) view.findViewById(R.id.commentInput);

        builder.setView(view)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        if (commentInput.getText().toString().trim().equals(""))
                        {
                            Toast.makeText(getActivity().getApplicationContext(),"Comment is Required!",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            JSONObject object = new JSONObject();

                            try {
                                object.put("station_id", currentSpot.getId());
                                object.put("rating", Integer.parseInt(rating.getSelectedItem().toString()));
                                object.put("commentText", commentInput.getText().toString());
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            MyDDPState.getInstance().comment(object);
                            Toast.makeText(getActivity().getApplicationContext(),"Comment added",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Close Comments View
                    }
                }).create();

        //builder.show();

        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);
                Button btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE);

                // Set font for Buttons
                Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Ubuntu-L.ttf");
                btnPositive.setTypeface(face);
                btnNegative.setTypeface(face);
            }
        });

        alert.show();
    }


    /*
     *  Set and scale the icon fitting to type
     */
    private Bitmap setIcon()
    {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_bar);

        switch (currentSpot.getType())
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
     *  private Adapter for filling Comments into ListView
     */
    private class CommentsAdapter extends ArrayAdapter<Comment> {

        public CommentsAdapter(Context context, ArrayList<Comment> comments) {
            super(context, 0, comments);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Comment comment = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_map_spotdetails_big_comment_view, parent, false);
            }

            // Lookup view for data population
            TextView commentText = (TextView) convertView.findViewById(R.id.comment_text);

            // Populate the data into the template view using the data object
            commentText.setText(comment.getText());
            commentText.setTypeface(face);

            // Return the completed view to render on screen
            return convertView;
        }
    }
}