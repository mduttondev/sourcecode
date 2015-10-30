package com.mkdutton.superduperimageduper;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Matt on 10/10/14.
 */
public class CopyService extends IntentService {

    public static final String EXTRA_IMAGE_PATH = "com.mkdutton.superduperimageduper.EXTRA_IMAGE_PATH";
    public static final int NOTIF_ID = 0x10256;

    NotificationManager mNotifMGR;

    public  CopyService(){
        super ("CopyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        mNotifMGR = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);

        String imagePath = null;

        if (intent.hasExtra(EXTRA_IMAGE_PATH)){
            imagePath = intent.getStringExtra(EXTRA_IMAGE_PATH);
        }


        if (imagePath != null && !imagePath.isEmpty() ) {

            // gets the name of the file that Android gave it for re-use in the FOS
            String fName = imagePath.substring(  (imagePath.lastIndexOf("/") + 1)  , imagePath.length());

            // get the image from the OS folder and reduce to 1/4 original size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;

            Bitmap image = BitmapFactory.decodeFile(imagePath, options);

            // Where i want to save it
            File storageLoc = this.getExternalFilesDir(null);

            File writeFile = new File(storageLoc, fName);

            try {

                FileOutputStream fos = new FileOutputStream(writeFile);

                // format to save, quality, where to save it.
                image.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                fos.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //scan the file so the Filesystem knows its there.
            scanFile(Uri.fromFile(writeFile));

            // show notification with the image.
            showNotification( writeFile, fName );

            // broadcast to update the grid, if the receiver is registered it will update for the user
            Intent updateIntent = new Intent(MainActivity.ACTION_UPDATE_GRID);
            this.sendBroadcast(updateIntent);


        }

    }

    private void scanFile(Uri imageUri){
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        sendBroadcast(scanIntent);

    }

    private void showNotification(File _image, String _fName) {


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_action_content_picture);
        builder.setContentTitle("New Image Captured");
        builder.setContentText("Click to view image");

        builder.setLargeIcon(BitmapFactory.decodeFile(Uri.fromFile(_image).getPath()));
        builder.setAutoCancel(true);


        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.setBigContentTitle("New Image Captured");
        style.setSummaryText("Click to view image");
        style.bigLargeIcon( BitmapFactory.decodeResource(getResources(),R.drawable.ic_action_content_picture) );

        style.bigPicture( BitmapFactory.decodeFile(Uri.fromFile(_image).getPath() ) );

        builder.setStyle(style);

        // Expanded Notification Share Button
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(_image));
        PendingIntent pShare = PendingIntent.getActivity(this, 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(android.R.drawable.ic_menu_share, "Share Image", pShare);

        // Expanded Notification Delete Button
        Intent deleteIntent = new Intent(NewImageReceiver.ACTION_DELETE_NEW);
        deleteIntent.putExtra(NewImageReceiver.EXTRA_FILE_NAME, _fName);
        PendingIntent pDelete = PendingIntent.getBroadcast(this, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(android.R.drawable.ic_menu_delete, "Delete Image", pDelete);

        // when Clicking on the Notifications
        Intent clickIntent = new Intent(Intent.ACTION_VIEW);
        clickIntent.setDataAndType( Uri.fromFile(_image) ,"image/*");
        PendingIntent pClick = PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pClick);

        Notification notification = builder.build();
        mNotifMGR.notify(NOTIF_ID, notification);


    }

}
