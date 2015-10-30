package com.mkdutton.androidnavigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

/**
 * Created by Matt on 9/22/14.
 */
public class NetworkConnection {


    public static boolean isConnected(Context context) {

        /* getting the default preferences */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        /* getting the string out for the desired connection type. values are a string but are numbers */
        String selected_type = prefs.getString("CONN_TYPE", "2");

        /* parse that string to an int for better accuracy in the comparison */
        int conn_type = Integer.valueOf(selected_type);

        /*
            0 = wifi only
            1 = none
            2 = any connection
        */

        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (mgr != null) {

            // Getting the active network connection info.
            NetworkInfo info = mgr.getActiveNetworkInfo();

            // Could be null if no network is connected.
            if (info != null) {

                // is theres a conneciton and offline isnt selected then use what ever connection you have
                if (info.isConnected()) {

                    // if the pref conn type is WIFI Only and your on wifi then connection == true
                    if ( conn_type == 0 && info.getType() == ConnectivityManager.TYPE_WIFI) { // wifi only desired

                        return true;

                        // conn type is always off line return offline
                    } else if ( conn_type == 1 ) { // Always offline

                        return false;


                        // if the pref conn type is ANY then connect regardless of what network
                    } else if ( conn_type == 2 ) {// Accept any connection

                        return true;

                    }
                }

            }

        }

        return false;

    }// close isConnected
}
