package com.mkdutton.labfour;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mkdutton.labfour.fragments.MainFragment;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null){





        } else {

            Intent intent = getIntent();
            Bundle extras = intent.getExtras();

            if (extras != null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.mainContainer, MainFragment.newInstance(extras), MainFragment.TAG)
                        .commit();
            } else {
                getFragmentManager().beginTransaction()
                        .replace(R.id.mainContainer, MainFragment.newInstance(), MainFragment.TAG)
                        .commit();
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
