package com.mkdutton.androidnavigation;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Matt on 9/22/14.
 */
public class DetailsFragment extends ListFragment {


    public static final String TAG = "DETAILS_FRAGMENT";

    public static final String POSTS = "POSTS";

    ArrayList<RedditPosts> mPosts;

    ActionMode mActionMode;

    private int mSelectedPost = -1;

    PostsAdapter adapter;



    public static DetailsFragment newInstance(ArrayList<RedditPosts> postsArrayList){

        /* Factory method to create new instances of the details frag and set its args */
        DetailsFragment fragment = new DetailsFragment();

        Bundle args = new Bundle();

        args.putSerializable(POSTS, postsArrayList);

        fragment.setArguments(args);

        return fragment;

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_details, container, false);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* getting the arguments out of the bundle and finishing up the list view setup by applying the
         * adapter with the ArrayList sent in with the args */
        Bundle args = getArguments();

        if (args != null && args.containsKey(POSTS)) {

            mPosts = (ArrayList<RedditPosts>) args.get(POSTS);

            adapter = new PostsAdapter(getActivity(), mPosts );

            setListAdapter(adapter);

        }

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                RedditPosts rp = mPosts.get(position);

                Intent detailIntent = new Intent(getActivity(), DetailsActivity.class);

                detailIntent.putExtra(DetailsActivity.POST_EXTRA, rp);

                startActivity(detailIntent);

            }
        });


        getListView().setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                // if the Actionmode != null then one already exists so exit out of this.
                if (mActionMode != null){
                    return false;
                }

                // if it is null then set the selected index and start the action mode with its call back
                mSelectedPost = i;
                mActionMode = getActivity().startActionMode(mActionCallback);
                return true;
            }
        });

    }



    /* ViewHolder Pattern for the listview since i am displaying 50 posts, all in different cells */
    static class ViewHolder {

        public TextView mHolder_title;


        /*

        public TextView mHolder_subreddit;

        public TextView mHolder_date;

        public TextView mHolder_author;

        */


    }


    // method called from the mainActivity after delete is called
    public void deletePost(int _selected){

        adapter.remove( _selected );
        adapter.notifyDataSetChanged();

    }

    ActionMode.Callback mActionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.delete_post_contextual, menu);
            return true;
        }


        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }


        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

            DeleteDialogFragment dialog = new DeleteDialogFragment();

            Bundle args = new Bundle();
            args.putInt(POSTS, mSelectedPost);
            dialog.setArguments(args);

            dialog.show(getActivity().getFragmentManager(), DeleteDialogFragment.TAG);

            mActionMode.finish();

            return true;
        }


        @Override
        public void onDestroyActionMode(ActionMode actionMode) {

            mActionMode = null;
        }
    };


}
