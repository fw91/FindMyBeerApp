package com.msp.findMyBeer.Database;

/**
 *  This class creates a Comment-Object
 *
 *  @author Florian Wirth
 */
public class Comment {

    // details of location
    private int    _rating;          // rating
    private String _text;            // commentText


    /*
     *  empty Constructor
     */
    public Comment()
    {

    }


    /*
     *  main Constructor
     *
     *  @param rating
     *  @param text
     */
    public Comment(int rating, String text)
    {
        this._rating = rating;
        this._text   = text;
    }


    // --------------------------------- Get-methods -----------------------------------
    // ---------------------------------------------------------------------------------


    public int getRating()
    {
        return this._rating;
    }

    public String getText()
    {
        return this._text;
    }
}
