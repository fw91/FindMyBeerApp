package com.msp.findMyBeer.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/*
 *  This class handles the SQLite-Database on the device it creates the tables and handles
 *  inputs/reads/etc.
 *
 *  @author Florian Wirth
 */
public class DatabaseHandler extends SQLiteOpenHelper
{
    // set database name and version
    private static final String DATABASE_NAME = "appDatabase";
    private static final int DATABASE_VERSION = 1;

    // table name for BeerSpots
    private static final String SPOT_TABLE   = "beer_spots";
    // column names for beer_spots
    private static final String SPOT_ID        = "_id";
    private static final String SPOT_NAME      = "name";
    private static final String SPOT_TYPE      = "tag";
    private static final String SPOT_INFO      = "info";
    private static final String SPOT_CITY      = "city";
    private static final String SPOT_ADDRESS   = "address";
    private static final String SPOT_DAYS      = "openingTimes";
    private static final String SPOT_B_NAME    = "beer_name";
    private static final String SPOT_B_PRICE   = "beer_price";
    private static final String SPOT_SUB_ID    = "submitter_id";
    private static final String SPOT_LAT       = "latitude";
    private static final String SPOT_LNG       = "longitude";
    private static final String SPOT_CREATED   = "created_at";
    private static final String SPOT_VISITED   = "visited_counter";
    private static final String SPOT_COMMIS    = "comments";

    // table name for UserAccount
    private static final String USER_TABLE     = "user_account";

    // table create statements
    private static final String createBeerDB =

            "CREATE TABLE " + SPOT_TABLE +

                       " (" + SPOT_ID        + " TEXT PRIMARY KEY, " +
                              SPOT_NAME      + " TEXT, "             +
                              SPOT_TYPE      + " TEXT, "             +
                              SPOT_INFO      + " TEXT, "             +
                              SPOT_CITY      + " TEXT, "             +
                              SPOT_ADDRESS   + " TEXT, "             +
                              SPOT_DAYS      + " TEXT, "             +
                              SPOT_B_NAME    + " TEXT, "             +
                              SPOT_B_PRICE   + " TEXT, "             +
                              SPOT_SUB_ID    + " TEXT, "             +
                              SPOT_LAT       + " TEXT, "             +
                              SPOT_LNG       + " TEXT, "             +
                              SPOT_CREATED   + " TEXT, "             +
                              SPOT_VISITED   + " TEXT, "             +
                              SPOT_COMMIS    + " TEXT"               +
                                                                  ")";

    /*
    private static final String createUserDB =

            "CREATE TABLE " + USER_TABLE +

                       " (" + USER_ID        + " TEXT PRIMARY KEY, " +
                              USER_NAME      + " TEXT, "             +
                              USER_FROM      + " TEXT, "             +
                              USER_EMAIL     + " TEXT, "             +
                              USER_STATUS    + " TEXT, "             +
                              USER_CREATED   + " TEXT, "             +
                              USER_SUB_A     + " INTEGER, "          +
                              USER_SUB_P     + " INTEGER, "          +
                              USER_TOKEN     + " TEXT"               +
                                                                  ")";
    */


    /*
     *  main constructor
     *
     *  @param ctx application context
     */
    public DatabaseHandler(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate (SQLiteDatabase db)
    {
        // creating required tables
        db.execSQL(createBeerDB);
        //db.execSQL(createUserDB);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + SPOT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);

        // create new tables
        onCreate(db);
    }


    // ----------------------------- SPOT TABLE FUNCTIONS ------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // Methods:
    //
    // checkIfSpotTableIsEmpty()
    // getSpotCursor()
    // getSpotsFromDB()
    // clearSpotDB()
    // refreshSpotDB()
    // insertSpot()
    // deleteSpot()
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    /*
     *  this method is used to see if there is any data inside the beer_spots-Table
     *  if the Cursor "can move to first row", there is data inside
     */
    private boolean checkIfSpotTableIsEmpty() {

        SQLiteDatabase db = getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + SPOT_TABLE, null);

        // if Cursor has first Element (true)
        //              -> spot Table has data in it and return false (not empty)
        // if Cursor has no first Element (false)
        //              -> spot Table has no data in it and return true (empty)

        return !mCursor.moveToFirst();
    }

    /*
     *  retrieve a Cursor to work with the SQLite-Database for BeerSpots
     */
    private Cursor getSpotCursor()
    {
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {SPOT_ID,SPOT_NAME,SPOT_TYPE,SPOT_INFO,SPOT_CITY,SPOT_ADDRESS,SPOT_DAYS,
                            SPOT_B_NAME,SPOT_B_PRICE,SPOT_SUB_ID,SPOT_LAT,SPOT_LNG,SPOT_CREATED,
                            SPOT_VISITED, SPOT_COMMIS};

        return db.query(SPOT_TABLE,columns,null,null,null,null,null);
    }


    /*
     *  method to retrieve an ArrayList of BeerSpots of current DB
     */
    public ArrayList<BeerSpot> getSpotsFromDB()
    {
        ArrayList<BeerSpot> spotList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor CR = getSpotCursor();

        // initialize everything required for reading out spot data
        BeerSpot spot = new BeerSpot();
        int visitedCtr;
        String spotID, spotSubID;
        double spotLat, spotLng;
        String spotName, spotType, spotInfo, spotCity, spotAddress, spotBName, spotBPrice, createdAt;
        BeerSpotDays spotDays;
        ArrayList<Comment> comments = new ArrayList<>();

        if (!checkIfSpotTableIsEmpty()) {
            if (CR != null)
                CR.moveToFirst();

            // read out every entry, and add to ArrayList spotList
            do {
                spotID      = CR.getString(0);
                spotName    = CR.getString(1);
                spotType    = CR.getString(2);
                spotInfo    = CR.getString(3);
                spotCity    = CR.getString(4);
                spotAddress = CR.getString(5);
                spotDays    = spot.JSONtoDays(CR.getString(6));
                spotBName   = CR.getString(7);
                spotBPrice  = CR.getString(8);
                spotSubID   = CR.getString(9);
                spotLat     = Double.parseDouble(CR.getString(10));
                spotLng     = Double.parseDouble(CR.getString(11));
                createdAt   = CR.getString(12);
                visitedCtr  = Integer.parseInt(CR.getString(13));

                if (CR.getString(14) != null)
                {
                   comments = spot.JSONToComments(CR.getString(14));
                }

                spot = new BeerSpot(spotID, spotName, spotType, spotInfo, spotCity, spotAddress,
                                    spotDays, spotBName, spotBPrice, spotSubID, spotLat, spotLng,
                                    createdAt, visitedCtr, comments);

                spotList.add(spot);

            } while (CR.moveToNext());
        }

        db.close();

        return spotList;
    }

    /*
     *  clear the Spot-Table and re-create an empty one
     */
    public void clearSpotDB()
    {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + SPOT_TABLE);
        db.execSQL(createBeerDB);
        db.close();
    }


    /*
     *  this is used to completely clear, and refill the spot-table after retrieving data from the
     *  server
     *
     *  @param newSpotsArray the new Array of BeerSpots
     */
    public void refreshSpotDB(ArrayList<BeerSpot> newSpotsArray)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(SPOT_TABLE,null,null);

        for (BeerSpot spot : newSpotsArray)
        {
            insertSpot(spot);
        }
        db.close();
    }


    /*
     *  insert a spot into SQLite-Database for BeerSpots
     *
     *  @param spot the BeerSpot to insert
     */
    public void insertSpot(BeerSpot spot)
    {
        SQLiteDatabase db = getWritableDatabase();

        Cursor CR = db.rawQuery("SELECT " + SPOT_NAME + " FROM " + SPOT_TABLE +
                                " WHERE " + SPOT_ID + "='" + spot.getId() + "'",null);

        if (!CR.moveToFirst())
        {
            ContentValues spotValues = new ContentValues();

            spotValues.put(SPOT_ID,      spot.getId());
            spotValues.put(SPOT_NAME,    spot.getName());
            spotValues.put(SPOT_TYPE,    spot.getType());
            spotValues.put(SPOT_INFO,    spot.getInfo());
            spotValues.put(SPOT_CITY,    spot.getCity());
            spotValues.put(SPOT_ADDRESS, spot.getAddress());
            spotValues.put(SPOT_DAYS,    spot.DaysToJSON(spot.getDays()).toString());
            spotValues.put(SPOT_B_NAME,  spot.getBeerName());
            spotValues.put(SPOT_B_PRICE, spot.getBeerPrice());
            spotValues.put(SPOT_SUB_ID,  spot.getSubmitterId());
            spotValues.put(SPOT_LAT,     Double.toString(spot.getLat()));
            spotValues.put(SPOT_LNG,     Double.toString(spot.getLng()));
            spotValues.put(SPOT_CREATED, spot.getCreatedAt());
            spotValues.put(SPOT_VISITED, spot.getVisitCounter());

            if (spot.getComments() != null)
            {
                spotValues.put(SPOT_COMMIS,  spot.CommentsToJSON(spot.getComments()).toString());
            }

            //spotValues.put(SPOT_COMMIS,  spot.CommentsToJSON(spot.getComments()).toString());

            db.insert(SPOT_TABLE, null, spotValues);
        }
        db.close();
    }


    /*
     *  this deletes a selected Spot using its id
     *
     *  @param spot BeerSpot to delete
     */
    public void deleteSpot(BeerSpot spot)
    {
        SQLiteDatabase db = getReadableDatabase();
        try
        {
            db.delete(SPOT_TABLE, SPOT_ID + "='" + spot.getId()+"'", null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        db.close();
    }

}