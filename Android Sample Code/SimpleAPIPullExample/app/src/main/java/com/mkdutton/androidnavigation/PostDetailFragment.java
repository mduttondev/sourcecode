package com.mkdutton.androidnavigation;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PostDetailFragment extends Fragment {

    public static final String TAG = "POST_DETAIL_FRAG";

    public static final String POST = "DISPLAY_POST";

    Context mContext;

    public static PostDetailFragment newInstance(RedditPosts post) {

        PostDetailFragment frag = new PostDetailFragment();

        if (post != null){

            Bundle args = new Bundle();
            args.putSerializable(POST, post);
            frag.setArguments(args);

        }

        return frag;
    }

    public PostDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mContext = activity;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_detail, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {

            final RedditPosts post = (RedditPosts)args.getSerializable(POST);

            ((TextView)getActivity().findViewById(R.id.title)).setText(post.getTitle());

            ((TextView)getActivity().findViewById(R.id.sub)).setText("Subreddit: " + post.getSubreddit());

            ((TextView)getActivity().findViewById(R.id.author)).setText("Author: " + post.getAuthor());

            ((TextView)getActivity().findViewById(R.id.date)).setText("Posted: " + post.getCreatedDate());

            (getActivity().findViewById(R.id.viewOnWebButton)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse( post.getPostURL() ));

                    startActivity(viewIntent);

                }
            });

        }

    }

}
