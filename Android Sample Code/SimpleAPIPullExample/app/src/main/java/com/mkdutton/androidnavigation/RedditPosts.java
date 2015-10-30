package com.mkdutton.androidnavigation;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Matt on 9/22/14.
 */
public class RedditPosts implements Serializable {

    private static final long serialVersionUID = 86753098675309L;

    private String title;
    private String author;
    private String subreddit;
    private String createdDate;
    private String postURL;

    public RedditPosts(String _title, String _author, String _subreddit, long _epoch, String _postUrl) {

        this.title = _title;
        this.author = _author;
        this.subreddit = _subreddit;
        this.postURL = _postUrl;

        createdDate = getDateFromEpoch((_epoch * 1000));

    }


    protected String getDateFromEpoch(long time) {

        Date d = new Date();

        d.setTime(time);

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");

        return formatter.format(d);

    }


    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getSubreddit() {
        return subreddit;
    }


    public String getCreatedDate() {
        return createdDate;
    }

    public String getPostURL() {
        return postURL;
    }

}
