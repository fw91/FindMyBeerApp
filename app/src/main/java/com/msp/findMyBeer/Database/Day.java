package com.msp.findMyBeer.Database;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *  This class creates a Day-Object to store with the parameters of a Day
 *
 *  @author Florian Wirth
 */
public class Day
{
    private int _openTime, _closeTime, _openDay, _closeDay;

    /*
     *  main Constructor
     *
     *  @param openDay
     *  @param openTime
     *  @param closeDay
     *  @param closeTime
     */
    public Day(int openDay, int openTime, int closeDay, int closeTime)
    {
        this._openDay   = openDay;
        this._openTime  = openTime;
        this._closeDay  = closeDay;
        this._closeTime = closeTime;
    }


    // -------------------------------- Get-methods ------------------------------------
    // ---------------------------------------------------------------------------------

    public int getOpenDay()
    {
        return this._openDay;
    }

    public int getCloseDay()
    {
        return this._closeDay;
    }

    public int getOpenTime()
    {
        return this._openTime;
    }

    public int getCloseTime()
    {
        return this._closeTime;
    }


    // ------------------------------ Time-Calculation ---------------------------------
    // ---------------------------------------------------------------------------------

    /*
     *  Get the remaining open-Time of a beerSpot at that day
     *
     *  @param currentTime
     */
    public String getTimeRemaining()
    {
        String timeRemaining;

        Calendar cal = GregorianCalendar.getInstance();
        int currentTimeHours = cal.get(Calendar.HOUR_OF_DAY);
        int currentTimeMinutes = cal.get(Calendar.MINUTE);

        int closeTimeMinutes = getMinutes(this._closeTime);
        int closeTimeHours = getHours(this._closeTime);

        int hoursRemaining, minutesRemaining;


        if (currentTimeHours > closeTimeHours)
        {
            closeTimeHours += 24;
        }

        if (currentTimeMinutes > closeTimeMinutes)
        {
            closeTimeMinutes += 60;
            closeTimeHours -= 1;
        }

        hoursRemaining = (closeTimeHours-currentTimeHours);
        minutesRemaining = (closeTimeMinutes-currentTimeMinutes);

        if (hoursRemaining == 0)
        {
            timeRemaining = ""+String.format("%02d",minutesRemaining)+"min left";
        }
        else
        {
            timeRemaining = ""+hoursRemaining+"."+String.format("%02d",minutesRemaining)+"h left";
        }

        return timeRemaining;
    }

    /*
     *  extract the minutes out of an integer with format "hhmm" (e.g. 1835 -> 35)
     *  needed to get the correct timeRemaining value above
     *
     *  @param input The "time"-IntegerValue
     */
    private int getMinutes(int input)
    {
                                                        // Example Calculation for Value 1835
        double tempValue = (double)input;               // 1835   -> 1835.0
        tempValue = tempValue/100;                      // 1835.0 -> 18.35
        tempValue = tempValue - Math.floor(tempValue);  // 18.35  -> 0.35
        tempValue = tempValue*100;                      // 0.35   -> 35

        return (int)tempValue;
    }

    /*
     *  extract the hours out of an integer with format "hhmm" (e.g. 1835 -> 18)
     *  needed to get the correct timeRemaining value above
     *
     *  @param input The "time"-IntegerValue
     */
    private int getHours(int input)
    {
                                                        // Example Calculation for Value 1835
        double tempValue = (double)input;               // 1835   -> 1835.0
        tempValue = tempValue/100;                      // 1835.0 -> 18.35
        tempValue = Math.floor(tempValue);              // 18.35  -> 18

        return (int)tempValue;
    }
}
