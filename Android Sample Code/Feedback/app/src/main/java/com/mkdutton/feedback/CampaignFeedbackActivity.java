package com.mkdutton.feedback;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mkdutton.feedback.adapters.FeedbackAdapter;
import com.mkdutton.feedback.fragments.DetailFragment;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.List;


public class CampaignFeedbackActivity extends BaseActivity implements View.OnLongClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private Toolbar mToolbar;

    Bitmap mQRImage;

    ListView mListView;

    ActionMode mSaveShareAction = null;
    ActionMode mDeleteCampaignActionMode = null;

    ParseObject mFeedback;
    View mViewToAnimate;
    String mPrettyName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if ( !(intent.hasExtra(Utils.CAMPAIGN_PRETTYNAME) && intent.hasExtra(Utils.CAMPAIGN_ID)) ){
            throw new IllegalArgumentException("Must contain CAMPAIGN_ID and CAMPAIGN_PRETTYNAME as extras");

        } else {

            // used in the details fragment
            mPrettyName = intent.getStringExtra(Utils.CAMPAIGN_PRETTYNAME);

            ((TextView)findViewById(R.id.camp_userName))
                    .setText("Username: " + ParseUser.getCurrentUser().getUsername());
            ((TextView)findViewById(R.id.camp_prettyName))
                    .setText("Campaign: " + mPrettyName);
            ((TextView)findViewById(R.id.camp_idField))
                    .setText("ID: " + intent.getStringExtra(Utils.CAMPAIGN_ID));

        }

        ((ImageView)findViewById(R.id.camp_QRImage)).setImageBitmap(showQRInImageView(intent.getStringExtra(Utils.CAMPAIGN_ID)));
        (findViewById(R.id.camp_QRImage)).setOnLongClickListener(this);

        mListView = (ListView)findViewById(R.id.camp_feedBackList);
        mListView.setEmptyView( findViewById(R.id.camp_emptyText) );
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);


        gatherFeedback(intent.getStringExtra(Utils.CAMPAIGN_ID));


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void gatherFeedback(String forCampaign) {

        if (Utils.isConnected(this)) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery(Utils.PARSE_CLASS_FEEDBACK);
            query.whereEqualTo(Utils.FEEDBACK_FOR_CAMPAIGN, forCampaign);
            query.orderByDescending(Utils.CREATION_TIME);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        if (parseObjects != null && parseObjects.size() > 0) {

                            mListView.setAdapter(new FeedbackAdapter(CampaignFeedbackActivity.this, parseObjects));
                        }
                    }
                }
            });


        } else{

            new AlertDialog.Builder(this)
                    .setTitle("Network Error")
                    .setMessage("No Netowrk, Please reconnect and try again")
                    .setPositiveButton("Ok", null)
                    .show();
        }

    }

    private Bitmap showQRInImageView(String campaign) {

        Bitmap bmp = Utils.readImageFromStorage(this, Utils.LOCATION_PROTECTED_EXTERNAL, campaign);

        if (bmp != null){
            mQRImage = bmp;
            return mQRImage;

        } else {

            bmp = Utils.generateQRCode(campaign);

            Utils.saveBitmapToStorage(this, Utils.LOCATION_PROTECTED_EXTERNAL, bmp, campaign);

            mQRImage = bmp;

            return mQRImage;

        }

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_campaign_feedback;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_campaign_feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            if (getFragmentManager().getBackStackEntryCount() > 0){
                getFragmentManager().popBackStack();

                findViewById(R.id.seperator).setVisibility(View.VISIBLE);

            }else {
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onLongClick(View v) {

        if (mSaveShareAction != null){
            return false;
        }

        startSupportActionMode(saveShareCallback);

        return true;
    }

    android.support.v7.view.ActionMode.Callback saveShareCallback = new android.support.v7.view.ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
            mToolbar = getToolbar();
            mToolbar.setVisibility(View.GONE);

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.save_share_contextual, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(android.support.v7.view.ActionMode mode, MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.action_save_QR){

                /* User saving the bitmap to device gallery */
                Utils.saveBitmapToStorage( CampaignFeedbackActivity.this, Utils.LOCATION_GALLERY, mQRImage,
                        getIntent().getStringExtra(Utils.CAMPAIGN_ID));

                Toast.makeText( CampaignFeedbackActivity.this, "Saved to Gallery", Toast.LENGTH_SHORT).show();

                mode.finish();

                return true;

            } else if (id == R.id.action_share_QR){

                Utils.shareQRCode( CampaignFeedbackActivity.this, mQRImage );

                mode.finish();

                return true;
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(android.support.v7.view.ActionMode actionMode) {
            mToolbar.setVisibility(View.VISIBLE);
            mSaveShareAction = null;
        }
    };

    public void launchDetailFragment(String prettyName, String fbText, String name, String contactInfo, String creationTime ){

        if (prettyName == null || fbText == null || name == null || contactInfo == null || creationTime == null ){
            throw new IllegalArgumentException("Detail information cannot be passed in as null");
        }

        Bundle info = new Bundle();
        info.putString(Utils.CAMPAIGN_PRETTYNAME, prettyName);
        info.putString(Utils.FEEDBACK_TEXT, fbText);
        info.putString(Utils.FEEDBACK_CONTACT_NAME, name);
        info.putString(Utils.FEEDBACK_CONTACT_INFO, contactInfo);
        info.putString(Utils.CREATION_TIME, creationTime);

        getFragmentManager().beginTransaction()
                .replace(R.id.feedbackContainer, DetailFragment.newInstance( info ), DetailFragment.TAG)
                .addToBackStack(DetailFragment.TAG)
                .commit();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (getFragmentManager().getBackStackEntryCount() > 0){

            getFragmentManager().popBackStack();

            findViewById(R.id.seperator).setVisibility(View.VISIBLE);

        } else {

            finish();

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        mFeedback = (ParseObject)parent.getItemAtPosition(position);

        launchDetailFragment(
                mPrettyName,
                mFeedback.getString(Utils.FEEDBACK_TEXT),
                mFeedback.getString(Utils.FEEDBACK_CONTACT_NAME),
                mFeedback.getString(Utils.FEEDBACK_CONTACT_INFO),
                new SimpleDateFormat("MMM dd, yyyy hh:mm a").format(mFeedback.getLong(Utils.CREATION_TIME))

        );

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        if (mDeleteCampaignActionMode != null){
            return false;
        }

        mFeedback = (ParseObject)parent.getItemAtPosition(position);
        mViewToAnimate = view;
        startSupportActionMode(deleteCampaignCallback);

        return true;
    }



    android.support.v7.view.ActionMode.Callback deleteCampaignCallback = new android.support.v7.view.ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
            mToolbar = getToolbar();
            mToolbar.setVisibility(View.GONE);

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.delete_contextual, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(android.support.v7.view.ActionMode actionMode, MenuItem menuItem) {

            if (menuItem.getItemId() == R.id.contextual_delete_button){

                if (Utils.isConnected(CampaignFeedbackActivity.this)){

                    mFeedback.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {

                                animateDeletion();

                                Toast.makeText(CampaignFeedbackActivity.this,
                                        "Deleted",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(CampaignFeedbackActivity.this,
                                        "Oops Something happened, Please try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {

                    mFeedback.deleteEventually(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {

                                animateDeletion();

                                Toast.makeText(CampaignFeedbackActivity.this,
                                        "Deleted",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(CampaignFeedbackActivity.this,
                                        "Oops Something happened, Please try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    new AlertDialog.Builder(CampaignFeedbackActivity.this)
                            .setTitle("Network Error")
                            .setMessage("No Network, this will be deleted when your data connection is re-established")
                            .setPositiveButton("Ok", null)
                            .show();

                    actionMode.finish();
                    return false;
                }

            }

            actionMode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(android.support.v7.view.ActionMode actionMode) {
            mToolbar.setVisibility(View.VISIBLE);
            mDeleteCampaignActionMode = null;
        }
    };



    public void animateDeletion() {

        final Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart (Animation animation){

            }

            @Override
            public void onAnimationRepeat (Animation animation){
            }

            @Override
            public void onAnimationEnd (Animation animation){
                ((FeedbackAdapter) mListView.getAdapter()).delete(mFeedback);
                ((FeedbackAdapter) mListView.getAdapter()).notifyDataSetChanged();

            }
        });

        mViewToAnimate.startAnimation(animation);

    }
}
