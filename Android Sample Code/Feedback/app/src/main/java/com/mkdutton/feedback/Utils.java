package com.mkdutton.feedback;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by Matt on 10/31/14.
 */
public class Utils {

    public static final String TAG = "UTILS";

    public static final int RANDOM_USER_ID = 15;
    public static final int RANDOM_CAMPAIGN_ID = 20;
    public static final int RANDOM_FEEDBACK_ID = 25;

    public static final String PARSE_CLASS_CAMPAIGN = "Campaign";
    public static final String PARSE_CLASS_FEEDBACK = "Feedback";

    public static final String LOGIN_KEY_USER_ID = "USER_ID";
    public static final String LOGIN_KEY_FIRST_NAME = "FIRST_NAME";
    public static final String LOGIN_KEY_LAST_NAME = "LAST_NAME";

    public static final String CAMPAIGN_PRETTYNAME = "prettyName";
    public static final String CAMPAIGN_ID = "campaignID";
    public static final String CAMPAIGN_USER = "user";
    public static final String CREATION_TIME = "creationTime";

    public static final String FEEDBACK_FOR_CAMPAIGN = "forCampaign";
    public static final String FEEDBACK_TEXT = "feedbackText";
    public static final String FEEDBACK_ID = "feedbackID";
    public static final String FEEDBACK_CONTACT_NAME = "contactName";
    public static final String FEEDBACK_CONTACT_INFO = "contactInfo";

    public static final int LOCATION_GALLERY = 0x02030203;
    public static final int LOCATION_PROTECTED_EXTERNAL = 0x07080708;


    public static boolean isConnected(Context context) {

        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (mgr != null) {

            NetworkInfo info = mgr.getActiveNetworkInfo();

            if (info != null) {

                if (info.isConnected()) {

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
        Log.i( TAG, new String(result));

        return new String(result);
    }

    public static Bitmap generateQRCode(String encodeID){

        QRCodeWriter qrCode = new QRCodeWriter();

        Bitmap bmp = null;

        try {
            bmp = toBitmap(qrCode.encode(encodeID, BarcodeFormat.QR_CODE, 250, 250));
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bmp;

    }

    private static Bitmap toBitmap( BitMatrix matrix ){

        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int[] pixels = new int[width * height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[(y * width) + x] = matrix.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);

        return bmp;


    }

    public static void saveBitmapToStorage(Context context, int location, Bitmap bmp, String filename){

        File storageLoc;

        if (location == LOCATION_GALLERY){
            storageLoc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        } else {
            storageLoc = context.getExternalFilesDir(null);
        }

        File file = new File(storageLoc, filename + ".jpg");

        try{
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            scanFile(context, Uri.fromFile(file));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void scanFile(Context context, Uri imageUri){
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        context.sendBroadcast(scanIntent);

    }

    public static void shareQRCode(Context context, Bitmap bmp){


        File storage = context.getExternalCacheDir();
        File file = new File(storage, "tempQR.jpg" );

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            scanFile(context, Uri.fromFile(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        context.startActivity(shareIntent);


    }


    public static void deleteImagefromStorage(Context context, int location, String filename) {

        File storageLoc;

        if (location == LOCATION_GALLERY){
            storageLoc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        } else {
            storageLoc = context.getExternalFilesDir(null);
        }

        File file = new File(storageLoc, filename + ".jpg");

        if (file.exists()){
            file.delete();

        }

    }

    public static Bitmap readImageFromStorage(Context context, int location, String filename){

        File storageLoc;

        if (location == LOCATION_GALLERY){
            storageLoc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        } else {
            storageLoc = context.getExternalFilesDir(null);
        }

        File file = new File(storageLoc, filename + ".jpg");

        if (file.exists()){
            return BitmapFactory.decodeFile(Uri.fromFile(file).getPath());
        } else {
            return null;
        }

    }


}



























