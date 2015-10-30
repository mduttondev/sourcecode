package com.mkdutton.androidnavigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Matt on 9/22/14.
 */
public class PostsAdapter extends BaseAdapter{

    private static final long CONSTANT_ID = 0x010000000;

    Context mContext;

    ArrayList<RedditPosts> mPostArray;


    public PostsAdapter(Context context, ArrayList<RedditPosts> post) {

        this.mContext = context;

        this.mPostArray = post;

    }




    @Override
    public int getCount() {

        if (mPostArray != null) {

            return mPostArray.size();

        } else {

            return 0;

        }

    }


    @Override
    public Object getItem(int position) {

        if (mPostArray != null && position < mPostArray.size() && position >= 0){

            return mPostArray.get(position);

        } else {

            return null;

        }
    }

    @Override
    public long getItemId(int position) {

        return CONSTANT_ID + position;

    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DetailsFragment.ViewHolder holder;

        RedditPosts post = mPostArray.get(position);

        if (convertView == null) {
            /* if there are no views to recycle then inflate the layout */
            convertView = LayoutInflater.from(mContext).inflate(R.layout.detail_cell_layout, parent, false);

            /* get a reference to the ViewHolder */
            holder = new DetailsFragment.ViewHolder();

            /* assign the UI elements to the ViewHolder */
            holder.mHolder_title = (TextView)convertView.findViewById(R.id.titleText);


            /*
            holder.mHolder_subreddit = (TextView)convertView.findViewById(R.id.subredditText);

            holder.mHolder_author = (TextView)convertView.findViewById(R.id.authorText);

            holder.mHolder_date = (TextView)convertView.findViewById(R.id.dateText);
            */


            /* set the tag to be the holder object so it can be accessed when these views are recycled
             * and there for you dont have to make the findViewById calls again */
            convertView.setTag(holder);

        } else {

            /* if there are views to recyle the get the viewholder from the tag so that the info
             * can be set to its new value */
            holder = (DetailsFragment.ViewHolder) convertView.getTag();

        }


        /* setting the text for the cell */
        holder.mHolder_title.setText(post.getTitle());

        /*
        holder.mHolder_subreddit.setText("Subreddit:\n"+ post.getSubreddit());

        holder.mHolder_author.setText("Author:\n"+ post.get);

        holder.mHolder_date.setText("Date:\n" + post.createdDate);
        */




        /* return the cell */
        return convertView;

    }

    public void remove(int _selected){

        mPostArray.remove( _selected );

    }




}
