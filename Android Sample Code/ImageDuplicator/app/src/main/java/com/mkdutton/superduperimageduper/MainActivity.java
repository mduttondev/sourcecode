package com.mkdutton.superduperimageduper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mkdutton.superduperimageduper.fragments.GalleryFragment;

import java.io.File;


public class MainActivity extends Activity implements DeleteDialogFragment.DeleteDialogListener{

    public static final String ACTION_UPDATE_GRID = "com.mkdutton.superduperimageduper.ACTION_UPDATE_GRID";
    public static final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, GalleryFragment.newInstance(), GalleryFragment.TAG)
                .commit();


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

        if (id == R.id.action_delete_all){

            File storageLoc = this.getExternalFilesDir(null);

            String[] files = storageLoc.list();

            if (files != null) {

                for (String name : files) {

                    new File(storageLoc, name).delete();

                    Log.i(TAG, "Deleted: " + name );

                }

            }

        }

        updateGridView();

        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        IntentFilter updateFilter = new IntentFilter();
        updateFilter.addAction(ACTION_UPDATE_GRID);
        registerReceiver(updateGridReceiver, updateFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        unregisterReceiver(updateGridReceiver);
    }



    BroadcastReceiver updateGridReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ACTION_UPDATE_GRID)){

                updateGridView();

            }

        }
    };

    private void updateGridView() {
        GalleryFragment frag = (GalleryFragment)getFragmentManager()
                .findFragmentByTag(GalleryFragment.TAG);

        if (frag != null) {
            frag.updateGrid();
        }
    }

    @Override
    public void deleteImage(int _image) {

        GalleryFragment frag = (GalleryFragment)getFragmentManager().findFragmentByTag(GalleryFragment.TAG);

        if (frag != null) {
            frag.deleteImage(_image);
        }

    }
}
