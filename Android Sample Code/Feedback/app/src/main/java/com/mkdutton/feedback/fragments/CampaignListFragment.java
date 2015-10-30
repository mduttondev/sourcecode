package com.mkdutton.feedback.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mkdutton.feedback.BaseActivity;
import com.mkdutton.feedback.CampaignFeedbackActivity;
import com.mkdutton.feedback.R;
import com.mkdutton.feedback.Utils;
import com.mkdutton.feedback.adapters.AllCampaignsAdapter;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class CampaignListFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public static final String TAG = "CAMPAIGN_LIST_FRAG";
    ListView mListView;

    ActionMode mActionMode;

    private Toolbar toolbar;

    ParseObject mObjectToDelete = null;
    ParseObject mCampaignPressed = null;

    View mViewToAnimate;


    public static CampaignListFragment newInstance() {

        CampaignListFragment frag = new CampaignListFragment();
        return frag;
    }

    public CampaignListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_campaign_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ParseUser currentUser = ParseUser.getCurrentUser();
        ((TextView) getActivity().findViewById(R.id.cam_UNText)).setText(currentUser.getUsername());

        mListView = (ListView) getActivity().findViewById(R.id.campaignsList);
        mListView.setEmptyView(getActivity().findViewById(R.id.emptyText));

        getAllCampaigns();


    }

    @Override
    public void onResume() {
        super.onResume();
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        mCampaignPressed = (ParseObject)parent.getItemAtPosition(position);

        Intent specCampIntent = new Intent(getActivity(), CampaignFeedbackActivity.class);
        specCampIntent.putExtra(Utils.CAMPAIGN_ID, mCampaignPressed.getString(Utils.CAMPAIGN_ID));
        specCampIntent.putExtra(Utils.CAMPAIGN_PRETTYNAME, mCampaignPressed.getString(Utils.CAMPAIGN_PRETTYNAME));
        startActivity(specCampIntent);

    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        mObjectToDelete = (ParseObject) parent.getItemAtPosition( position );

        if (mActionMode != null) {
            return false;
        }

        //mSelectedIndex = position;
        mViewToAnimate = view;
        mActionMode = getActivity().startActionMode(mDeleteCallback);

        return true;

    }

    public void getAllCampaigns() {


        ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setIndeterminate(true);
        progress.setMessage("Fetching....");
        progress.show();

        if (Utils.isConnected(getActivity())) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery(Utils.PARSE_CLASS_CAMPAIGN);
            query.whereEqualTo(Utils.CAMPAIGN_USER, ParseUser.getCurrentUser());
            query.orderByDescending(Utils.CREATION_TIME);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {

                        if (parseObjects != null && parseObjects.size() > 0) {

                            mListView.setAdapter(new AllCampaignsAdapter(getActivity(), parseObjects));
                            ((TextView) getActivity().findViewById(R.id.numCampaignsValue))
                                    .setText("" + (mListView.getAdapter()).getCount());

                        }
                    }
                }
            });

        } else {

            new AlertDialog.Builder(getActivity())
                    .setTitle("Network Error")
                    .setMessage("No Network, Please reconnect and try again")
                    .setPositiveButton("Ok", null)
                    .show();

        }

        progress.dismiss();

        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

    }


    ActionMode.Callback mDeleteCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            toolbar = ((BaseActivity) getActivity()).getToolbar();
            toolbar.setVisibility(View.GONE);

            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.delete_contextual, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            int id = item.getItemId();

            final String camp_id = mObjectToDelete.getString(Utils.CAMPAIGN_ID);

            if (id == R.id.contextual_delete_button) {

                if (mObjectToDelete != null) {

                    if (Utils.isConnected(getActivity())) {

                        mObjectToDelete.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {

                                    animateDeletion();

                                    Utils.deleteImagefromStorage(getActivity(),
                                            Utils.LOCATION_PROTECTED_EXTERNAL, camp_id);

                                    Toast.makeText(getActivity(),
                                            mObjectToDelete.getString(Utils.CAMPAIGN_PRETTYNAME) + " was deleted",
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    Toast.makeText(getActivity(),
                                            "Oops Something happened, Please try again",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    } else {

                        mObjectToDelete.deleteEventually(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {

                                    animateDeletion();

                                    Utils.deleteImagefromStorage(getActivity(),
                                            Utils.LOCATION_PROTECTED_EXTERNAL, camp_id);

                                    Toast.makeText(getActivity(),
                                            mObjectToDelete.getString(Utils.CAMPAIGN_PRETTYNAME) + " was deleted",
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    Toast.makeText(getActivity(),
                                            "Oops Something happened, Please try again",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }

                }
                mode.finish();

            }

            return true;

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            toolbar.setVisibility(View.VISIBLE);
            mActionMode = null;


        }
    };

    public void animateDeletion() {

        final Animation animation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart (Animation animation){

            }

            @Override
            public void onAnimationRepeat (Animation animation){
            }

            @Override
            public void onAnimationEnd (Animation animation){

                ((AllCampaignsAdapter) mListView.getAdapter()).delete(mObjectToDelete);
                ((AllCampaignsAdapter) mListView.getAdapter()).notifyDataSetChanged();

                ((TextView) getActivity().findViewById(R.id.numCampaignsValue))
                .setText("" + (mListView.getAdapter()).getCount());
            }
        });


        mViewToAnimate.startAnimation(animation);


    }


}
