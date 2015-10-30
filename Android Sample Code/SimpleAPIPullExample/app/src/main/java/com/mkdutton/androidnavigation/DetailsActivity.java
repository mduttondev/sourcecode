package com.mkdutton.androidnavigation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class DetailsActivity extends Activity {

    public static final String POST_EXTRA = "com.mkdutton.androidnavigation.DetailsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        RedditPosts rp = (RedditPosts)intent.getSerializableExtra(POST_EXTRA);

        if (rp != null) {

            getFragmentManager().beginTransaction()
                    .replace(R.id.postDetailsContainer, PostDetailFragment.newInstance(rp), PostDetailFragment.TAG)
                    .commit();

        } else {
            // This condition should never happen but if it does
            getFragmentManager().beginTransaction()
                    .replace(R.id.postDetailsContainer, PostDetailFragment.newInstance(null), PostDetailFragment.TAG)
                    .commit();

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

        if (id == R.id.action_settings) {

            showSettingsActivity();

            return true;

        } else if (id == android.R.id.home){

            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSettingsActivity() {

        /* creates an explicit intent to show the correct view.
        this Activity will then load and show the correct fragment, see the
        SettingsActivity.class file for further exlanation */
            Intent settingsIntent = new Intent(this, SettingsActivity.class);

            // start the activity
            startActivity(settingsIntent);



    }


    private void alertUser(String _title, String _message) {

        /* method containing a generic alert that can be used to alert the user of no cache data */
        AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());

        alert.setTitle(_title);

        alert.setMessage(_message);

        alert.setPositiveButton("OK", null);

        alert.show();


    }
}
