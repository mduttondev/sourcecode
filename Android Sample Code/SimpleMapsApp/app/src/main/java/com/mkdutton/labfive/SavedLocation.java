package com.mkdutton.labfive;

import java.io.Serializable;

/**
 * Created by Matt on 10/9/14.
 */
public class SavedLocation implements Serializable {


    public static final long serialVersionUID = 86753098675309L;

    private String mTitle;
    private String mNote;
    private double mLat;
    private double mLong;
    private String mImagePath;

    public SavedLocation(String _title, String _note, double _lat, double _long, String _imagePath) {
        this.mTitle = _title;
        this.mNote = _note;
        this.mLat = _lat;
        this.mLong = _long;
        this.mImagePath = _imagePath;
    }


    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmNote() {
        return mNote;
    }

    public void setmNote(String mNote) {
        this.mNote = mNote;
    }

    public double getmLat() {
        return mLat;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public double getmLong() {
        return mLong;
    }

    public void setmLong(double mLong) {
        this.mLong = mLong;
    }

    public String getmImagePath() {
        return mImagePath;
    }

    public void setmImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
    }
}
