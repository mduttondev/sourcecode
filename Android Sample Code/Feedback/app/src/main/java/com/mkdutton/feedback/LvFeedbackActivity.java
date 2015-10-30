package com.mkdutton.feedback;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import static android.widget.CompoundButton.OnCheckedChangeListener;


public class LvFeedbackActivity extends BaseActivity implements View.OnClickListener, OnCheckedChangeListener{

    public static final String EXTRA_SCAN_CONTENTS = "EXTRA_SCAN_CONTENTS";
    public static final int REQUEST_QR_SCAN = 0x00006563;
    public static final String TAG = "LEAVE_FEEDBACK_ACTIVITY";


    EditText mCampaignID;
    EditText mFeedbackText;
    LinearLayout mLinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCampaignID = (EditText)findViewById(R.id.enterCampaignIDText);
        mFeedbackText = (EditText)findViewById(R.id.feedbackMessageText);
        mLinearLayout = (LinearLayout) findViewById(R.id.contactView);

        (findViewById(R.id.submitFeedbackBtn)).setOnClickListener(this);
        ((Switch)findViewById(R.id.anon_switch)).setOnCheckedChangeListener(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ((Switch)findViewById(R.id.anon_switch)).setChecked(PreferenceManager
                .getDefaultSharedPreferences(this).getBoolean("anon", false));


    }

    @Override
    protected int getLayout() {
        return R.layout.activity_lv_feedback;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lv_feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_scan_qr) {

            scanQRCode();
            return true;

        } else if (id == android.R.id.home){

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void scanQRCode() {
        Intent scanQR = new Intent(this, QRScannerActivity.class);
        startActivityForResult(scanQR, REQUEST_QR_SCAN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_QR_SCAN && resultCode == RESULT_OK){
            mCampaignID.setText(data.getStringExtra(EXTRA_SCAN_CONTENTS));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submitFeedbackBtn){
            Log.i(TAG, "SUBMIT BUTTON PRESSED");

            gatherAndSubmitFeedback();


        }
    }

    private void gatherAndSubmitFeedback() {
        String forCampaign = mCampaignID.getText().toString();
        String feedbackText = mFeedbackText.getText().toString();

        String contactName = ((TextView)findViewById(R.id.contactName)).getText().toString();
        String contactInfo = ((TextView)findViewById(R.id.contactInfo)).getText().toString();

        if ( !(forCampaign.isEmpty() || feedbackText.isEmpty() ) ){

            if (forCampaign.length() == 20) {

                ParseObject feedback = new ParseObject( Utils.PARSE_CLASS_FEEDBACK );
                feedback.put(Utils.FEEDBACK_FOR_CAMPAIGN, forCampaign);
                feedback.put(Utils.FEEDBACK_TEXT, feedbackText);
                feedback.put(Utils.FEEDBACK_ID, Utils.generateRandomIdOfLength(Utils.RANDOM_FEEDBACK_ID));

                if (contactName != null && !contactName.isEmpty()) {
                    feedback.put(Utils.FEEDBACK_CONTACT_NAME, contactName);
                } else {
                    feedback.put(Utils.FEEDBACK_CONTACT_NAME, "None Given");
                }

                if (contactInfo != null && !contactInfo.isEmpty()) {
                    feedback.put(Utils.FEEDBACK_CONTACT_INFO, contactInfo);
                } else {
                    feedback.put(Utils.FEEDBACK_CONTACT_INFO, "None Given");
                }

                feedback.put(Utils.CREATION_TIME, System.currentTimeMillis());

                if (Utils.isConnected(this)) {
                    feedback.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                alertUser("Success", "Your Feedback was submitted successfully", true);
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {

                    feedback.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                alertUser("Success", "Your Feedback was submitted successfully", true);
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            } else {
                alertUser("Error", "Campaign ID must be 20 characters\nplease double check the entry", false);
            }

        } else {


            alertUser("Error", "Please complete required fields to submit feedback", false);


        }



    }

    private void alertUser(String title, String message, boolean success) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(title);
        alert.setMessage(message);

        if(success) {

            alert.setPositiveButton("Leave another", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCampaignID.setText("");
                        mFeedbackText.setText("");
                    }
                });
            alert.setNegativeButton("I'm done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();

                }
            });

            alert.setIcon(getResources().getDrawable(R.drawable.ic_success));


        } else {
            alert.setPositiveButton("Ok", null);
            alert.setIcon(getResources().getDrawable(R.drawable.ic_problem));
        }

        alert.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView.getId() == R.id.anon_switch){
            Log.i(TAG, "ANON SWITCH CHANGED");

            if (isChecked){
                expand();

            } else {
                collapse();
                ((TextView)findViewById(R.id.contactName)).setText("");
                ((TextView)findViewById(R.id.contactInfo)).setText("");
            }

        }

    }


    private void expand() {
        // Setting the preferences for retaining last usage setting
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("anon", true).commit();

        //set Visible
        mLinearLayout.setVisibility(View.VISIBLE);

        // making a measure specification that tells the view it can be from 0 to an unspecified size
        // essentially meaning it can be any size the layout needs to be
        int layoutWidth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int layoutHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        //setting constraints for when the view draws (width and height)
        mLinearLayout.measure(layoutWidth, layoutHeight);

        //get an animator that starts at 0 and expands to the needed height
        ValueAnimator mAnimator = slideAnimator(0, mLinearLayout.getMeasuredHeight());
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end) {

        //get an animator with the starting and ending values which are the height
        // animates between those 2 values
        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            //Update Height for every frame of the animation
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //get the animation value
                int value = (Integer)valueAnimator.getAnimatedValue();
                // get the layoutparams the linear layout currently has
                ViewGroup.LayoutParams layoutParams = mLinearLayout.getLayoutParams();
                //adjust the height to the current animation value
                layoutParams.height = value;
                //assign the new layoutparams back to the layout
                mLinearLayout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }


    private void collapse() {
        // Setting the preferences for retaining last usage setting
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("anon", false).commit();

        //gets the height of the expanded layout
        int expandedHeight = mLinearLayout.getHeight();

        //gets an animator going from full expanded to 0
        ValueAnimator mAnimator = slideAnimator(expandedHeight, 0);

        // set a listener for when the animation finishes set the visibility to gone
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                mLinearLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationStart(Animator animation) {

            }
        });
        //animate
        mAnimator.start();
    }

}
