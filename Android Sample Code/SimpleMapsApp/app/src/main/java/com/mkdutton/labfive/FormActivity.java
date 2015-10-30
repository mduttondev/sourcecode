package com.mkdutton.labfive;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.mkdutton.labfive.fragments.FormFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FormActivity extends Activity {

    public static final int CAMERA_REQUEST_CODE = 0x01234;

    Uri mFileUri;
    LatLng mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        mLocation = null;

        Intent intent = getIntent();
        if (intent.hasExtra(MainActivity.EXTRA_LOCATION_DATA)){
            mLocation = intent.getParcelableExtra(MainActivity.EXTRA_LOCATION_DATA);
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.formContainer, FormFragment.newInstance(), FormFragment.TAG)
                .commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_Camera) {

            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            mFileUri = getOutputUri();

            if (mFileUri != null){
                i.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
            }
            startActivityForResult(i, CAMERA_REQUEST_CODE);

            return true;

        } else if (id == R.id.action_Save){

            FormFragment frag = (FormFragment) getFragmentManager().findFragmentByTag(FormFragment.TAG);

            if (mFileUri != null) {


                String[] array = frag.getDataToSave();

                if (array != null) {

                    SavedLocation savedLoc = new SavedLocation(array[0], array[1], mLocation.latitude, mLocation.longitude, mFileUri.toString());

                    File storage = this.getExternalFilesDir(null);
                    File file = new File(storage, array[0]);

                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        ObjectOutputStream oos = new ObjectOutputStream(fos);

                        oos.writeObject(savedLoc);
                        oos.flush();
                        oos.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    finish();
                }

                return true;
            } else {
                frag.alertUser("Image Capture:", "You must include an image to Save");
                return false;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FormFragment frag  = (FormFragment)getFragmentManager().findFragmentByTag(FormFragment.TAG);

        if (frag != null) {

            if (requestCode == CAMERA_REQUEST_CODE && resultCode != RESULT_CANCELED) {
                if (mFileUri == null) {
                    frag.setImage((Bitmap) data.getParcelableExtra("data"));

                } else {

                    Bitmap map = BitmapFactory.decodeFile(mFileUri.getPath());
                    frag.setImage(map);
                    scanFile(mFileUri);
                }
            }
        }
    }



    public Uri getOutputUri(){
        String imageName = new SimpleDateFormat("MMddyyyy_HHmmss")
                .format(new Date(System.currentTimeMillis()));

        File imageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);


        File appDir = new File(imageDir, "labfive");
        appDir.mkdirs();

        File image = new File(appDir, imageName + ".jpg");
        try{
            image.createNewFile();
        }catch (Exception e){
            e.printStackTrace();
        }

        return Uri.fromFile(image);

    }

    private void scanFile(Uri imageUri){
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        sendBroadcast(scanIntent);

    }
}
