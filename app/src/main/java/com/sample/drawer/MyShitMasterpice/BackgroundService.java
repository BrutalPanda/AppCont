package com.sample.drawer.MyShitMasterpice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Slava-laptop on 22.07.2015.
 */
public class BackgroundService extends Service  {
    final String LOG_TAG = "myLogsService";
    final String FILENAME = "file";


    JSONArray json = new JSONArray();
    GPSTracker gps;

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");

        gps = new GPSTracker(super.getBaseContext(),json);

        if(gps.canGetLocation()){
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Log.d("GPS", "Your Location is - \nLat: " + latitude + "\nLong: " + longitude);
        }else{
            gps.showSettingsAlert();
        }


    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        someTask();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        gps.finalizeTracker();
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroyGPS");
        Log.d(LOG_TAG,gps.getTrack().toString());


    }



    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    void someTask() {
    }



}
