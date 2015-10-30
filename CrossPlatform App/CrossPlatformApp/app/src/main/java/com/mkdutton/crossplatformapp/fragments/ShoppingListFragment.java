package com.mkdutton.crossplatformapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Toast;

import com.mkdutton.crossplatformapp.R;
import com.mkdutton.crossplatformapp.ShoppingListAdapter;
import com.mkdutton.crossplatformapp.Utils;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Matt on 12/1/14.
 */
public class ShoppingListFragment extends ListFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    ActionMode mActionMode;

    int mSelected = -1;

    View mViewToAnimate;

    public Handler mHandler;

    public Runnable mRunnable;

    ShoppingListListener mListener;

    public static final String TAG = "SHOPPINGLIST_FRAG";

    public static ShoppingListFragment newInstance() {
        return new ShoppingListFragment();
    }

    public interface ShoppingListListener {
        public void launchEditActivity(String _objID);
    }

    public ShoppingListFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ShoppingListListener) {
            mListener = (ShoppingListListener) activity;
        } else {
            throw new IllegalArgumentException("Calling Class must implement ShoppingListListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText("No Items Found\n\n\nYou can use the plus\nicon to add shopping items");
        setListShown(true);
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);

        mHandler = new Handler();

        /*
        set a new Handler to run every 20 seconds and request a repoll of the server
        this is dismissed by calling the dissmissHandlerAndRunnableMethod.
        call originates in the containing activity
        */
        mHandler.postDelayed(
                mRunnable = new Runnable() {
                    @Override
                    public void run() {

                        mHandler.postDelayed(this, 10000);

                        // on the callback if theres a connection get updates
                        // if not dont
                        if (Utils.isConnected(getActivity())) {
                            //Toast.makeText(getActivity(), "Server Polled", Toast.LENGTH_SHORT).show();
                            loadShoppingItems();
                        }

                    }
                }, 10000);


    }

    @Override
    public void onResume() {
        super.onResume();

        loadShoppingItems();
    }

    public void dismissHandlerAndRunnable() {
        mHandler.removeCallbacks(mRunnable);
    }

    public void loadShoppingItems() {

        if (Utils.isConnected(getActivity())) {

            ProgressDialog progress = new ProgressDialog(getActivity());
            progress.setMessage("Gathering...");
            progress.setIndeterminate(true);
            progress.show();

            ParseQuery<ParseObject> query = ParseQuery.getQuery(Utils.PARSE_CLASS_LIST_ITEM);
            query.whereEqualTo(Utils.ITEM_OWNER, ParseUser.getCurrentUser());
            query.orderByAscending("createdAt");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        if (parseObjects != null && parseObjects.size() > 0) {
                            getListView().setAdapter(new ShoppingListAdapter(getActivity(), parseObjects));
                            getListView().setOnItemLongClickListener(ShoppingListFragment.this);

                        }
                    }
                }
            });

            progress.dismiss();

        } else {

            Toast.makeText(getActivity(), "No Network Connection, Try again later", Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (Utils.isConnected(getActivity())) {
            ParseObject obj = (ParseObject) parent.getItemAtPosition(position);
            Log.i(TAG, obj.getString("Item"));


            mListener.launchEditActivity(obj.getObjectId());


        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Network Error")
                    .setMessage("No Network Available\nPlease Try Again Later")
                    .setPositiveButton("Ok", null)
                    .show();
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (mActionMode != null) {
            return false;
        }

        getActivity().startActionMode(mDeleteCallback);
        mSelected = position;
        mViewToAnimate = view;

        return true;
    }


    ActionMode.Callback mDeleteCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.contextual_delete_item, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            if (item.getItemId() == R.id.action_delete_item) {
                if (mSelected != -1) {
                    ShoppingListAdapter adapter = ((ShoppingListAdapter) getListView().getAdapter());
                    ParseObject itemToDelete = (ParseObject) adapter.getItem(mSelected);

                    animateDeletion(itemToDelete);

                    if (Utils.isConnected(getActivity())) {

                        itemToDelete.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(getActivity(), "Delete Successful", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {

                        Toast.makeText(getActivity(), "No Network\nWill delete when a network becomes available", Toast.LENGTH_SHORT).show();

                        itemToDelete.deleteEventually(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(getActivity(), "Delete Successful", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }

            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };


    public void animateDeletion(final ParseObject deleteItem) {

        final Animation animation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                ((ShoppingListAdapter) getListView().getAdapter()).delete(deleteItem);
                ((ShoppingListAdapter) getListView().getAdapter()).notifyDataSetChanged();

            }
        });


        mViewToAnimate.startAnimation(animation);


    }


}
