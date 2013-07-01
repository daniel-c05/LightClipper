package com.thoughts.apps.clipper;

import android.util.Log;

/**
 * Created by user on 06-30-13.
 */
public class Constants {

    public static final String TAG = "LightClipper";

    public static void logMessage (String message) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, message);
        }
    }

}
