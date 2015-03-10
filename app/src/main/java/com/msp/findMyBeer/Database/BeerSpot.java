package com.msp.findMyBeer.Database;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *  This class creates a BeerSpot-Object
 *
 *  @author Florian Wirth
 */
public class BeerSpot
{
    // spot-specific id
    private String _id;                 // ID

    // details of location
    private String _name;            // Name
    private String _type;            // Bar, Tankstelle, Klohaus w/e
    private String _city;            // Stadt
    private String _address;         // Adresse
    private BeerSpotDays _days;      // Öffnungszeiten Mo/Di/...
                                     // (BeerSpotDays enthält eine Arraylist mit "open-days")
    private String _info;            // Kurze Beschreibung

    // geo-coordinates for map
    private double _geoLat;          // latitude
    private double _geoLng;          // longitude

    // beer details
    private String _beerName;        // Die Biersorte die angeboten wird
    private String _beerPrice;       // Der Bierpreis

    // information about submitter
    private String _submitterId;        // Von wem wurde der Spot in die DB geladen

    private String _createdAt;

    private int _visitCounter;

    private ArrayList<Comment> _comments;

    // base64 String
    private String _base64;          // ImageString

    // JSON-File of this BeerSpot
    JSONObject _jsonSpot;            // Der Spot als JSON-Object für Austausch mit der DB


    /*
     *  empty Constructor
     */
    public BeerSpot()
    {

    }


    /*
     *  main Constructor
     *
     *  @param id
     *  @param name
     *  @param type
     *  @param info
     *  @param city
     *  @param address
     *  @param days
     *  @param beerName
     *  @param beerPrice
     *  @param submitterId
     *  @param lat
     *  @param lng
     */
    public BeerSpot(String id,
                    String name, String type, String info, String city, String address,
                    BeerSpotDays days,
                    String beerName, String beerPrice,
                    String submitterId,
                    double lat, double lng,
                    String createdAt,
                    int visitCounter,
                    ArrayList<Comment> comments)
    {
        this._id           = id;
        this._name         = name;
        this._type         = type;
        this._info         = info;
        this._city         = city;
        this._address      = address;
        this._days         = days;
        this._beerName     = beerName;
        this._beerPrice    = beerPrice;
        this._submitterId  = submitterId;
        this._geoLat       = lat;
        this._geoLng       = lng;
        this._createdAt    = createdAt;
        this._visitCounter = visitCounter;

        if (comments.isEmpty())
        {
            this._comments = new ArrayList<>();
        }
        else
        {
            this._comments = comments;
        }

        // after everything is initialized, create the JSONObject for this spot
        this._jsonSpot = createJSON();
    }


    /*
     *  Constructor to get BeerSpot from a JSON-File
     *
     *  @param object
     */
    public BeerSpot(JSONObject object)
    {
        extractJSON(object);
        this._jsonSpot = object;
    }



    public BeerSpot(String name, String type, String info, String city, String address,
                    BeerSpotDays days,
                    String beerName, String beerPrice)
    {
        this._name      = name;
        this._type      = type;
        this._info      = info;
        this._city      = city;
        this._address   = address;
        this._days      = days;
        this._beerName  = beerName;
        this._beerPrice = beerPrice;
    }


    //used for submit with image
    public BeerSpot(String name, String type, String info, String city, String address,
                    String beerName, String beerPrice, String base64)
    {
        this._name      = name;
        this._type      = type;
        this._info      = info;
        this._city      = city;
        this._address   = address;
        this._beerName  = beerName;
        this._beerPrice = beerPrice;
        this._base64    = base64;
    }


    // --------------------------------- Get-methods -----------------------------------
    // ---------------------------------------------------------------------------------

    public String getId()
    {
        return this._id;
    }

    public String getName()
    {
        return this._name;
    }

    public String getType()
    {
        return this._type;
    }

    public String getInfo()
    {
        return this._info;
    }

    public String getCity()
    {
        return this._city;
    }

    public String getAddress()
    {
        return this._address;
    }

    public double getLat()
    {
        return this._geoLat;
    }

    public double getLng()
    {
        return this._geoLng;
    }

    public LatLng getLocation()
    {
        return new LatLng(this._geoLat,this._geoLng);
    }

    public BeerSpotDays getDays()
    {
        return this._days;
    }

    public String getBeerName()
    {
        return this._beerName;
    }

    public String getBeerPrice()
    {
        return this._beerPrice;
    }

    public String getSubmitterId()
    {
        return this._submitterId;
    }

    public String getCreatedAt()
    {
        return this._createdAt;
    }

    public int getVisitCounter()
    {
        return this._visitCounter;
    }

    public ArrayList<Comment> getComments()
    {
        return this._comments;
    }

    public JSONObject getJSON()
    {
        return this._jsonSpot;
    }


    // --------------------------------- JSON-methods ----------------------------------
    // ---------------------------------------------------------------------------------

    /*
     *  create a JSONObject for BeerSpot by its data
     */
    public JSONObject createJSON() {
        JSONObject object = new JSONObject();

        try {
            object.put("_id", this._id);
            object.put("name", this._name);
            object.put("type", this._type);
            object.put("info", this._info);
            object.put("city", this._city);
            object.put("address", this._address);
            object.put("openingTimes", DaysToJSON(this._days));
            object.put("beerName", this._beerName);
            object.put("beerPrice", this._beerPrice);
            object.put("submitter_id", this._submitterId);
            object.put("lat", Double.toString(this._geoLat));
            object.put("lng", Double.toString(this._geoLng));
            object.put("createdAt", this._createdAt);
            object.put("visitors", this._visitCounter);
            object.put("comments", CommentsToJSON(this._comments));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }


    /*
     *  used to get a BeerSpot Object from an incoming JSON-File
     */
    private void extractJSON(JSONObject jsonObject)
    {
        try {
            this._id           = jsonObject.getString("_id");
            this._name         = jsonObject.getString("name");
            this._type         = jsonObject.getString("type");
            this._info         = jsonObject.getString("info");
            this._city         = jsonObject.getString("city");
            this._address      = jsonObject.getString("address");
            this._days         = JSONtoDays(jsonObject.getJSONArray("openingTimes").toString());
            this._beerName     = jsonObject.getString("beerName");
            this._beerPrice    = jsonObject.getString("beerPrice");
            this._submitterId  = jsonObject.getString("submitter_id");
            this._geoLat       = Double.parseDouble(jsonObject.getString("lat"));
            this._geoLng       = Double.parseDouble(jsonObject.getString("lng"));
            this._createdAt    = jsonObject.getString("createdAt");
            this._visitCounter = jsonObject.getInt("visitors");

            if (jsonObject.has("comments"))
            {
                this._comments = JSONToComments(jsonObject.getJSONArray("comments").toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
     *  convert BeerSpotHours into a JSON-Array
     *
     *  @param days
     */
    public JSONArray DaysToJSON(BeerSpotDays days){

        JSONArray daysArray = new JSONArray();
        JSONObject open_data, close_data, day_data;

        for (int i=0; i<days.getDaysArray().size();i++)
        {
            try {
                open_data = new JSONObject();
                close_data = new JSONObject();
                day_data = new JSONObject();

                open_data.put("time",days.getDaysArray().get(i).getOpenTime());
                open_data.put("day",days.getDaysArray().get(i).getOpenDay());

                close_data.put("time",days.getDaysArray().get(i).getCloseTime());
                close_data.put("day",days.getDaysArray().get(i).getCloseDay());

                day_data.put("open",open_data);
                day_data.put("close",close_data);

                daysArray.put(day_data);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return daysArray;
    }


    /*
     *  extract the days object from a JSON-File
     *
     *  @param jsonString
     */
    public BeerSpotDays JSONtoDays(String jsonString)
    {
        BeerSpotDays days = new BeerSpotDays();
        JSONArray daysArray;
        JSONObject day_data;

        try {
            daysArray = new JSONArray(jsonString);

            for (int i=0;i<daysArray.length();i++)
            {
                day_data = daysArray.getJSONObject(i);

                days.addDay(day_data.getJSONObject("open").getInt("day"),
                            day_data.getJSONObject("open").getInt("time"),
                            day_data.getJSONObject("close").getInt("day"),
                            day_data.getJSONObject("close").getInt("time"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return days;
    }


    /*
     *  convert the Comments-Array into a JSON-Array
     *
     *  @param days
     */
    public JSONArray CommentsToJSON (ArrayList<Comment> spotComments)
    {
        JSONArray commentsArray = new JSONArray();
        JSONObject comment_data;

        for (int i=0; i<spotComments.size();i++)
        {
            try {
                comment_data = new JSONObject();

                /*
                comment_data.put("timestamp", "");
                comment_data.put("user_id", "");
                */
                comment_data.put("rating",      spotComments.get(i).getRating());
                comment_data.put("commentText", spotComments.get(i).getText());

                commentsArray.put(comment_data);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return commentsArray;
    }


    /*
     *  extract the comments object from a JSON-File
     *
     *  @param jsonString
     */
    public ArrayList<Comment> JSONToComments(String jsonString)
    {
        ArrayList<Comment> spotComments = new ArrayList<>();
        JSONArray commentsArray;
        JSONObject comment_data;

        try {
            commentsArray = new JSONArray(jsonString);

            for (int i=0;i<commentsArray.length();i++) {
                comment_data = commentsArray.getJSONObject(i);

                /*
                comment_data.getString("timestamp");
                comment_data.getString("user_id");
                */

                Comment comment = new Comment(comment_data.getInt("rating"),
                                              comment_data.getString("commentText"));
                spotComments.add(comment);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return spotComments;
    }



    public JSONObject createSubmitTimeJSON()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("name",         this._name);
            object.put("type",         this._type);
            object.put("city",         this._city);
            object.put("address",      this._address);
            object.put("openingTimes", DaysToJSON(this._days));
            object.put("beerName",     this._beerName);
            object.put("beerPrice",    this._beerPrice);
            object.put("info",         this._info);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    public JSONObject createSubmitImageJSON()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("name",         this._name);
            object.put("type",         this._type);
            object.put("city",         this._city);
            object.put("address",      this._address);
            object.put("beerName",     this._beerName);
            object.put("beerPrice",    this._beerPrice);
            object.put("info",         this._info);
            object.put("imageString",  this._base64);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }


    /*
     *  this method turns days-ArrayList into readable information
     */
    public String daysToString()
    {
        String returnString = "";
        String day1 ="";
        String day2 ="";

        for (int i=0;i<this._days.getDaysArray().size();i++)
        {
            switch (this._days.getDaysArray().get(i).getOpenDay())
            {
                case 1: day1 = "Mon"; break;
                case 2: day1 = "Tue"; break;
                case 3: day1 = "Wed"; break;
                case 4: day1 = "Thu"; break;
                case 5: day1 = "Fri"; break;
                case 6: day1 = "Sat"; break;
                case 7: day1 = "Sun"; break;
                default: break;
            }

            switch (this._days.getDaysArray().get(i).getCloseDay())
            {
                case 1: day2 = "Mon"; break;
                case 2: day2 = "Tue"; break;
                case 3: day2 = "Wed"; break;
                case 4: day2 = "Thu"; break;
                case 5: day2 = "Fri"; break;
                case 6: day2 = "Sat"; break;
                case 7: day2 = "Sun"; break;
                default: break;
            }

            if (this._days.getDaysArray().get(i).getOpenDay() == this._days.getDaysArray().get(i).getCloseDay())
            {
                returnString += day1 + ".: "  + String.format("%.2f",((double)this._days.getDaysArray().get(i).getOpenTime()/100))
                                              + " - "
                                              + String.format("%.2f",((double)this._days.getDaysArray().get(i).getCloseTime()/100))
                                              + "h";
                if ((i+1) == this._days.getDaysArray().size())
                {
                    returnString += "";
                }
                else
                {
                    returnString += "\n";
                }
            }
            else
            {
                returnString += day1 + ".-"
                              + day2 + ".: "  + String.format("%.2f",((double)this._days.getDaysArray().get(i).getOpenTime()/100))
                                              + " - "
                                              + String.format("%.2f",((double)this._days.getDaysArray().get(i).getCloseTime()/100))
                                              + "h";

                if ((i+1) == this._days.getDaysArray().size())
                {
                    returnString += "";
                }
                else
                {
                    returnString += "\n";
                }
            }
        }

        return returnString;
    }
}