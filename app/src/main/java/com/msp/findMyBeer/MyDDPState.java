package com.msp.findMyBeer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.msp.findMyBeer.Database.BeerSpot;
import com.msp.findMyBeer.android_ddp_client.DDPStateSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements specific DDP state/commands for this application
 *
 * @author Florian Fincke
 */
public class MyDDPState extends DDPStateSingleton{


    private Map<String, BeerSpot> mySpots;
    private Context myContext;

    /**
     * Constructor for this singleton (private because it's a singleton)
     *
     * @param context Android application context
     */
    protected MyDDPState(Context context) {
        super(context);
        myContext = context;
        mySpots = new ConcurrentHashMap<>();
    }

    /**
     * Used by Activity singleton to initialize this singleton
     *
     * @param context Android application context
     */
    public static void initInstance(Context context) {
        // only called by MyApplication
        if (mInstance == null) {
            // Create the instance
            mInstance = new MyDDPState(context);
        }
    }

    /**
     * Gets only instance of this singleton
     *
     * @return this singleton object
     */
    public static MyDDPState getInstance() {
        // Return the instance
        return (MyDDPState) mInstance;
    }

    public Map<String, BeerSpot> getSpots() {
        return mySpots;
    }


    /**
     * Lets us lightly wrapper default implementation's objects
     * instead of using our own DB if we had to override
     * the add/remove/updateDoc methods
     */
    @Override
    public void broadcastSubscriptionChanged(String collectionName,
                                             String changetype, String docId) {
        if (collectionName.equals("users")) {
            Object obj = getCollection(collectionName).get(docId);
            Gson g = new Gson();

            try {
                JSONObject profile = new JSONObject(g.toJson(obj)).getJSONObject("profile");
                int userSubmitsPending = profile.getInt("userSubmitsPending");
                int userSubmitsAccepted = profile.getInt("userSubmitsAccepted");
                int userStatus = profile.getInt("userStatus");
                JSONArray markers = profile.getJSONArray("currentMarkers");
                mySpots.clear();
                for (int i = 0; i < markers.length(); i++) {
                    mySpots.put(markers.getJSONObject(i).getString("_id"), new BeerSpot(markers.getJSONObject(i)));
                }
                Intent markerIntent = new Intent("newMarkers");
                LocalBroadcastManager.getInstance(myContext).sendBroadcast(markerIntent);
                Intent userIntent = new Intent("newUserData");
                userIntent.putExtra("userSubmitsPending", userSubmitsPending);
                userIntent.putExtra("userSubmitsAccepted", userSubmitsAccepted);
                userIntent.putExtra("userStatus", userStatus);
                LocalBroadcastManager.getInstance(myContext).sendBroadcast(userIntent);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // do the broadcast after we've taken care of our parties wrapper
        super.broadcastSubscriptionChanged(collectionName, changetype, docId);

    }


    /**
     * Gets email address for user
     *
     * @param userId user ID
     * @return email address for that user (lookup via users collection)
     */
    @Override
    public String getUserEmail(String userId) {

        return "blu";
    }

}