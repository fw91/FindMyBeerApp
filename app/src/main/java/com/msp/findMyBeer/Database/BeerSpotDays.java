package com.msp.findMyBeer.Database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *  This class creates a BeerSpotDays-Object to store
 *  the BeerSpot open-periods in an Array
 *
 *  @author Florian Wirth
 */
public class BeerSpotDays {

    final private ArrayList<Day> _days;


    /*
     *  main Constructor
     */
    public BeerSpotDays() {
        this._days = new ArrayList<Day>();
    }


    /*
     *  add a new Day to the Array
     *
     *  @param openDay   the day the spot will open (1=monday,2=tuesday,...)
     *  @param openTime  the time the spot will open (1230 = 12.30h)
     *  @param closeDay  the day the spot will close (1=monday,2=tuesday,...)
     *  @param closeTime the time the spot will close (1230 = 12.30h)
     */
    public void addDay(int openDay, int openTime, int closeDay, int closeTime) {
        Day newDay = (new Day(openDay, openTime, closeDay, closeTime));

        _days.add(newDay);
    }


    /*
     *  get the Day-Array
     */
    public ArrayList<Day> getDaysArray() {
        return this._days;
    }


    /*
     *  used to see if the BeerSpot is currently open
     */
    public boolean isOpen() {
        Calendar cal = GregorianCalendar.getInstance();
        int currentDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int currentTime = cal.get(Calendar.HOUR_OF_DAY) * 100 + cal.get(Calendar.MINUTE);

        for (int i = 0; i < this._days.size(); i++) {

            Day entry = this._days.get(i);
            int openDay = entry.getOpenDay();
            int closeDay = entry.getCloseDay();
            int openTime = entry.getOpenTime();
            int closeTime = entry.getCloseTime();

            if (openDay == currentDay && closeDay == currentDay) {
                if (currentTime >= openTime && currentTime <= closeTime) {
                    return true;
                }
            } else if (openDay == currentDay && closeDay == (currentDay + 1) % 7) {
                if (currentTime >= openTime) {
                    return true;
                }
            } else if ((openDay == currentDay - 1 && closeDay == currentDay) || (openDay == 6 && closeDay == 0)) {
                if (currentTime <= closeTime) {
                    return true;
                }
            }
        }
        return false;
    }


    /*
     *  get the CurrentDay to extract information about how long it will be open
     *
     *  @param day  currentDay
     *  @param time currentTime
     */
    public Day getCurrentDay() {
        Calendar cal = GregorianCalendar.getInstance();
        int currentDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int currentTime = cal.get(Calendar.HOUR_OF_DAY) * 100 + cal.get(Calendar.MINUTE);


        for (int i = 0; i < this._days.size(); i++) {
            Day entry = this._days.get(i);
            int openDay = entry.getOpenDay();
            int closeDay = entry.getCloseDay();
            int openTime = entry.getOpenTime();
            int closeTime = entry.getCloseTime();

            // open and close today
            if (openDay == currentDay && closeDay == currentDay) {
                if (currentTime >= openTime && currentTime <= closeTime) {
                    return entry;
                }
            // open today and close tomorrow
            } else if (openDay == currentDay && closeDay == (currentDay + 1) % 7) {
                if (currentTime >= openTime) {
                    return entry;
                }
            // opened yesterday and close today
            } else if ((openDay == currentDay - 1 && closeDay == currentDay) || (openDay == 6 && closeDay == 0)) {
                if (currentTime <= closeTime) {
                    return entry;
                }
            }
        }
        return null;
    }

}
