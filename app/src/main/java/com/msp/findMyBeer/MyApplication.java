package com.msp.findMyBeer;

import android.app.Application;

/**
 *  Global MyApplication Class, establish server-Connection
 *
 *  @author Florian Wirth
 */
public class MyApplication extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Server-Connection
        MyDDPState.initInstance(getApplicationContext());
        MyDDPState.getInstance().connectIfNeeded();

        // Global Font-Override
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Ubuntu-L.ttf");
    }
}
