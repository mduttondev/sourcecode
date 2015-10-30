package com.mkdutton.feedback;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mkdutton.feedback.fragments.AddCampaignFragment;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class AddCampaignActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(R.id.addCampaignContainer, AddCampaignFragment.newInstance(), AddCampaignFragment.TAG)
                .commit();

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_add_campaign;
    }



    public void addCampaignToUser() {

        AddCampaignFragment frag = (AddCampaignFragment)getFragmentManager().findFragmentByTag(AddCampaignFragment.TAG);

        if (frag != null) {
            final String prettyName = frag.getmPrettyName().getText().toString();
            String campID = frag.getmCampaignID().getText().toString();
            Bitmap qrImage = frag.getmQRImage();

            if ( !(prettyName.isEmpty() || campID.isEmpty() || qrImage == null) ){

                Utils.saveBitmapToStorage(this, Utils.LOCATION_PROTECTED_EXTERNAL, qrImage, campID);

                /* NOTE: Parse Object "Campaign" is used to to save and read the different campaign
                 * objects that may be stored to a user. To get feed back I need to make sure that
                  * I am searching in the Parse Objects for "Feedback" that i will create later */

                ParseObject campaign = new ParseObject( Utils.PARSE_CLASS_CAMPAIGN );
                campaign.put(Utils.CAMPAIGN_PRETTYNAME, prettyName );
                campaign.put(Utils.CAMPAIGN_ID, campID );
                campaign.put(Utils.CAMPAIGN_USER, ParseUser.getCurrentUser() );
                campaign.put(Utils.CREATION_TIME, System.currentTimeMillis() );

                if (Utils.isConnected(this)) {
                    campaign.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                // Success
                                Toast.makeText(AddCampaignActivity.this, "your campaign: " +
                                        prettyName + " was saved successfully", Toast.LENGTH_SHORT).show();

                                returnResult(RESULT_OK);

                            } else {
                                // Save failed

                                returnResult(RESULT_CANCELED);
                                Toast.makeText(AddCampaignActivity.this, "your campaign: " +
                                        prettyName + " FAILED to save", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                } else {

                    Toast.makeText(this, "No Network\nCampaign will be created when network is available",
                            Toast.LENGTH_SHORT).show();

                    campaign.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                // Success
                                Toast.makeText(AddCampaignActivity.this, "your campaign: " +
                                        prettyName + " was saved successfully", Toast.LENGTH_SHORT).show();

                                returnResult(RESULT_OK);

                            } else {
                                // Save Failed
                                Toast.makeText(AddCampaignActivity.this, "your campaign: " +
                                        prettyName + " FAILED to save", Toast.LENGTH_SHORT).show();
                                returnResult(RESULT_CANCELED);
                            }
                        }
                    });
                }

                finish();

            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);

                alert.setTitle("Error:")
                        .setMessage("Please Complete all fields before saving")
                        .setPositiveButton("Ok", null)
                        .show();

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_campaign, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_save_campaign) {

            addCampaignToUser();

            return true;

        } else if (id == android.R.id.home){

            finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }


    public void returnResult(int resultCode){

        Intent newCampaign = new Intent();

        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, newCampaign);
        } else {
            setResult(RESULT_CANCELED, newCampaign);
        }



        finish();

    }



}
