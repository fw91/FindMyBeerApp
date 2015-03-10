package com.msp.findMyBeer.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.msp.findMyBeer.MyDDPState;
import com.msp.findMyBeer.R;

/**
 *  The Splash Activity
 *
 *  @author Cenk Canpolat, Florian Wirth, Florian Fincke
 */
public class SplashActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Typeface face  = Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-L.ttf");
        TextView loading = (TextView)findViewById(R.id.loading);
        loading.setTypeface(face);

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    //check if connected!
                    int attempts = 0;
                    while (!MyDDPState.getInstance().isConnected()) {
                        //Wait to connect
                        Thread.sleep(500);
                        attempts ++;
                        Log.i("attempts", "attemps = " + attempts);
                        if (attempts == 10) break;
                    }
                } catch (Exception e) {
                }
                finally {
                    attemptLogin();
                }
            }
        };
        t.start();

    }

    private void attemptLogin() {
        if (MyDDPState.getInstance().getState() == MyDDPState.DDPSTATE.Connected)
        {
            if (MyDDPState.getInstance().getResumeToken() != null)
            {
                MyDDPState.getInstance().login(MyDDPState.getInstance().getResumeToken());

                startActivity(new Intent(getApplicationContext(),MenuActivity.class));
                finish();
            }else {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        }
    }
}
