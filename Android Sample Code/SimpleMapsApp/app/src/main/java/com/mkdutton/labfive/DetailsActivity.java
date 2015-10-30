package com.mkdutton.labfive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mkdutton.labfive.fragments.DetailsFragment;
import com.mkdutton.labfive.fragments.GoogleMapsFragment;

import java.io.File;


public class DetailsActivity extends Activity {


    SavedLocation mLocation;
    public static final String TAG = "DETAILS_ACTIVITY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);



        Intent intent = getIntent();
        if (intent.hasExtra(GoogleMapsFragment.EXTRA_DETAILS)){
            mLocation = (SavedLocation)intent.getSerializableExtra(GoogleMapsFragment.EXTRA_DETAILS);


            getFragmentManager().beginTransaction()
                    .replace(R.id.detailsContainer, DetailsFragment.newInstance(mLocation), DetailsFragment.TAG)
                    .commit();

        } else {

            finish();
        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_delete) {

            File storage = this.getExternalFilesDir(null);

            if (storage.list() != null) {

                String[] files = storage.list();

                for (String name : files) {

                    if (name.equals(mLocation.getmTitle())) {

                        new File(storage, name).delete();
                        Log.i(TAG, "Deleted: " + name);

                    }

                }
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
