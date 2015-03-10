package com.msp.findMyBeer.Activities.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.msp.findMyBeer.Activities.Fragments.FavoriteBSFragment;
import com.msp.findMyBeer.Database.BeerSpot;
import com.msp.findMyBeer.Database.DatabaseHandler;
import com.msp.findMyBeer.R;

import org.json.JSONObject;

/**
 *  Create and inflate the RemoveSpot-Dialog, used to remove the selected Spot from DB
 *
 *  @author Florian Wirth
 */
public class RemoveSpotDialog extends DialogFragment
{

    DatabaseHandler DH;
    String jsontxt;
    BeerSpot spot;

    public static RemoveSpotDialog newInstance(BeerSpot spot)
    {

        RemoveSpotDialog f = new RemoveSpotDialog();

        String spotJSON = spot.getJSON().toString();
        Bundle args = new Bundle();
        args.putString("json",spotJSON);
        f.setArguments(args);

        return f;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the spot, by using JSON-functionallity
        jsontxt = getArguments().getString("json");
        JSONObject test = new JSONObject();

        try{
            test = new JSONObject(jsontxt);
        }catch (Exception e){
            e.printStackTrace();
        }
        spot = new BeerSpot(test);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        DH = new DatabaseHandler(getActivity().getApplicationContext());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_remove, null);

        View removeView = v.findViewById(R.id.removeTxt);

        // reminder
        ((TextView)removeView).setText("Are You sure You want to delete "+spot.getName()+"?\n(This can not be undone)\n");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v)
                .setPositiveButton(R.string.cdbYes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Delete the Spot
                        DH.deleteSpot(spot);
                        Toast.makeText(getActivity().getApplicationContext(), spot.getName() + " deleted", Toast.LENGTH_SHORT).show();
                        reloadFavFragment();
                    }
                })
                .setNegativeButton(R.string.cdbNo, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);
                Button btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE);

                // Set Button Font
                Typeface face=Typeface.createFromAsset(getActivity().getAssets(),"fonts/Ubuntu-L.ttf");

                btnPositive.setTypeface(face);
                btnNegative.setTypeface(face);
            }
        });

        return alert;
    }


    /*
     *  Load the DB-fragment at FavBsActivity
     */
    public void reloadFavFragment()
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment newFragment = new FavoriteBSFragment();
        ft.replace(R.id.favFragment, newFragment);
        ft.commit();
    }
}
