package com.mkdutton.crossplatformapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by Matt on 11/25/14.
 */
public class Utils {

    public static final String TAG = "UTILS";

    public static final String PARSE_CLASS_LIST_ITEM = "Shopping_Item";

    public static final String ITEM_OWNER = "Owner";

    public static final String ITEM_NAME = "Item";

    public static final String ITEM_QTY = "Qty";

    public static final String ITEM_ID = "Item_Id";

    public static final String KEY_NAME = "Name";

    public static final String KEY_USER_ID = "User_ID";

    public static final int USER_ID_LENGTH = 16;

    public static final int ITEM_ID_LENGTH = 10;


    public static boolean isConnected(Context context) {

        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (mgr != null) {

            NetworkInfo info = mgr.getActiveNetworkInfo();

            if (info != null) {

                if ( info.isConnectedOrConnecting() ) {

                    return true;
                }

            }

        }

        return false;
    }


    public static String generateRandomIdOfLength(int length) {

        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            // get a random index out of chars
            int randomCharIndex = random.nextInt(chars.length);
            result[i] = chars[randomCharIndex];
        }
        Log.i(TAG, new String(result));

        return new String(result);
    }





}
