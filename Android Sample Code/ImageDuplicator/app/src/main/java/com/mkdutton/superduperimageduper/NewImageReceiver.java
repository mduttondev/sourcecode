package com.mkdutton.superduperimageduper;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

/**
 * Created by Matt on 10/10/14.
 */
public class NewImageReceiver extends BroadcastReceiver{

    public static final String TAG = "NEW_IMAGE_REC";
    public static final String ACTION_DELETE_NEW = "com.mkdutton.superduperimageduper.ACTION_DELETE_NEW";
    public static final String EXTRA_FILE_NAME = "com.mkdutton.superduperimageduper.EXTRA_FILE_NAME";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Camera.ACTION_NEW_PICTURE)){

            String filePath = getFileUri( context , intent);

            Intent copyImageIntent = new Intent(context, CopyService.class);
            copyImageIntent.putExtra(CopyService.EXTRA_IMAGE_PATH, filePath);
            context.startService( copyImageIntent );

        } else if (intent.getAction().equals(ACTION_DELETE_NEW)){

            String fName = intent.getStringExtra(EXTRA_FILE_NAME);

            if (fName != null){
                File storageLoc = context.getExternalFilesDir(null);
                File jpgFile = new File(storageLoc, fName);

                jpgFile.delete();
                Log.i(TAG, "Deleted: " + fName);
            }

            // calls update grid only for the when the user presses delete on the notification
            // this is just in case the user is looking at the app when an image is deleted: that way
            // its updated in real time for the UX.
            Intent updateIntent = new Intent(MainActivity.ACTION_UPDATE_GRID);
            context.sendBroadcast(updateIntent);

            NotificationManager notifMGR = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            notifMGR.cancel(CopyService.NOTIF_ID);


        }


    }



    public String getFileUri(Context context, Intent capturedIntent) {

        Uri selectedImage = capturedIntent.getData();
        String[] pathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = context.getContentResolver().query(selectedImage, pathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(pathColumn[0]);
        String imagePath = cursor.getString(columnIndex);
        cursor.close();

        return imagePath;
    }
}
