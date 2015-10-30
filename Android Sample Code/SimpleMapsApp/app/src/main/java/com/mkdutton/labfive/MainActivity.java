package com.mkdutton.labfive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.mkdutton.labfive.fragments.GoogleMapsFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;


public class MainActivity extends Activity {

    public static final String TAG = "MAIN_ACTIVITY";
    public static final String EXTRA_LOCATION_DATA = "com.mkdutton.labfive.EXTRA_LOCATION_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, GoogleMapsFragment.newInstance(), GoogleMapsFragment.TAG)
                .commit();
    }


    @Override
    protected void onResume() {
        super.onResume();

        ArrayList<SavedLocation> savedLocations = null;

        File storage = this.getExternalFilesDir(null);

        if (storage.list() != null) {

            savedLocations = new ArrayList<SavedLocation>();

            String[] files = storage.list();

            for (String name : files) {

                File file = new File(storage, name);

                try {

                    FileInputStream fin = new FileInputStream(file);
                    ObjectInputStream oin = new ObjectInputStream(fin);

                    SavedLocation location = (SavedLocation) oin.readObject();
                    oin.close();

                    savedLocations.add(location);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }


        }


        GoogleMapsFragment frag = (GoogleMapsFragment) getFragmentManager()
                .findFragmentByTag(GoogleMapsFragment.TAG);

        if (frag != null && savedLocations != null ) {

                frag.updateMap(savedLocations);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add_pin) {

            GoogleMapsFragment frag = (GoogleMapsFragment) getFragmentManager().findFragmentByTag(GoogleMapsFragment.TAG);

            if (frag.mLocation != null) {

                LatLng loc = new LatLng(frag.mLocation.getLatitude(), frag.mLocation.getLongitude());

                Intent addPinIntent = new Intent(this, FormActivity.class);
                addPinIntent.putExtra(EXTRA_LOCATION_DATA, loc);
                startActivity(addPinIntent);

            }
            return true;
        } else if (id == R.id.action_delete_Files) {

            deleteFiles();
            onResume();

        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteFiles() {
        File storage = this.getExternalFilesDir(null);

        if (storage.list() != null) {

            String[] files = storage.list();

            for (String name : files) {

                new File(storage, name).delete();
                Log.i(TAG, "Deleted: " + name);


            }
        }
    }



}