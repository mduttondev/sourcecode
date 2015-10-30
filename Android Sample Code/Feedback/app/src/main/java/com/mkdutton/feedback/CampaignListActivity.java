package com.mkdutton.feedback;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mkdutton.feedback.fragments.CampaignListFragment;
import com.parse.ParseUser;


public class CampaignListActivity extends BaseActivity {

    public static final int REQUEST_ADD_CAMPAIGN = 0x0203;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(R.id.listContainer, CampaignListFragment.newInstance(), CampaignListFragment.TAG)
                .commit();

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_campaign_list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_campaign_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_addCampaign) {

            Intent addCampaignIntent = new Intent(this, AddCampaignActivity.class);
            ActivityCompat.startActivityForResult(this, addCampaignIntent, REQUEST_ADD_CAMPAIGN, null);
            return true;

        } else if(id == android.R.id.home){

            finish();
            return true;

        } else if (id == R.id.action_logout){

            ParseUser.logOut();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        CampaignListFragment frag = (CampaignListFragment)getFragmentManager().findFragmentByTag(
                CampaignListFragment.TAG);


        if (resultCode == RESULT_OK) {
            if (frag != null) {
                frag.getAllCampaigns();
            }

        } else {
            Toast.makeText(this, "Action Cancelled, No new campaign added",
                    Toast.LENGTH_LONG).show();
        }

    }


}
