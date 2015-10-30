package com.mkdutton.feedback.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mkdutton.feedback.BaseActivity;
import com.mkdutton.feedback.R;
import com.mkdutton.feedback.Utils;

/**
 * Created by Matt on 11/5/14.
 */
public class AddCampaignFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener{

    public static final String TAG = "ADD_QR_FRAG";

    private TextView mPrettyName;

    private TextView mCampaignID;

    private Bitmap mQRImage = null;

    ActionMode mActionMode;


    public static AddCampaignFragment newInstance(){

        return new AddCampaignFragment();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_campaign, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPrettyName = (TextView)getActivity().findViewById(R.id.add_prettyName);
        mCampaignID = (TextView)getActivity().findViewById(R.id.add_campaignID);

        (getActivity().findViewById(R.id.generateNewIDBtn)).setOnClickListener(this);
        (getActivity().findViewById(R.id.generateNewQRBtn)).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.generateNewIDBtn){

            String campaignID = Utils.generateRandomIdOfLength(Utils.RANDOM_CAMPAIGN_ID);

            ((TextView)getActivity().findViewById(R.id.add_campaignID)).setText(campaignID);
            (getActivity().findViewById(R.id.generateNewIDBtn)).setEnabled(false);

        } else if (id == R.id.generateNewQRBtn){

            if ( !(mPrettyName.getText().toString().isEmpty() || mCampaignID.getText().toString().isEmpty() ) ){

                mQRImage = Utils.generateQRCode( mCampaignID.getText().toString() );

                ((ImageView)getActivity().findViewById(R.id.qr_image)).setImageBitmap(mQRImage);
                (getActivity().findViewById(R.id.qr_image)).setOnLongClickListener(this);


                (getActivity().findViewById(R.id.generateNewQRBtn)).setEnabled(false);
                (getActivity().findViewById(R.id.add_saveText)).setVisibility(View.VISIBLE);


            } else {
                Toast.makeText(getActivity(),
                        "All fields must be completed before QR is generated",
                        Toast.LENGTH_SHORT).show();

            }

        }

    }

    @Override
    public boolean onLongClick(View v) {
        int id = v.getId();

        if (id == R.id.qr_image){


            if (mActionMode != null){
                return false;
            }

            getActivity().startActionMode(mActionCallback);
            return true;
        }

        return false;
    }

    ActionMode.Callback mActionCallback = new ActionMode.Callback() {

        private Toolbar toolbar;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            toolbar = ((BaseActivity)getActivity()).getToolbar();
            toolbar.setVisibility(View.GONE);

            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.save_share_contextual, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();

            /*
            ImageView iv = ((ImageView)getActivity().findViewById(R.id.qr_image));
            iv.buildDrawingCache();
            Bitmap bmp = iv.getDrawingCache();
            */

            if (id == R.id.action_save_QR){

                /* User saving the bitmap to device gallery */
                Utils.saveBitmapToStorage( getActivity(), Utils.LOCATION_GALLERY, mQRImage,
                        ((TextView)getActivity().findViewById(R.id.add_campaignID)).getText().toString());

                mode.finish();

                return true;

            } else if (id == R.id.action_share_QR){

                Utils.shareQRCode( getActivity(), mQRImage );

                mode.finish();

                return true;
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            toolbar.setVisibility(View.VISIBLE);
            mActionMode = null;
        }
    };

    public TextView getmPrettyName() {
        return mPrettyName;
    }

    public TextView getmCampaignID() {
        return mCampaignID;
    }

    public Bitmap getmQRImage() {
        return mQRImage;
    }


}
