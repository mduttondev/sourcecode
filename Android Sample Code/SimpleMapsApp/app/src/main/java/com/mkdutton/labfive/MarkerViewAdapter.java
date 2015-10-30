package com.mkdutton.labfive;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

/**
 * Created by Matt on 10/9/14.
 */
public class MarkerViewAdapter implements GoogleMap.InfoWindowAdapter {

    Context mContext;
    ArrayList<SavedLocation> mLocations;


    public MarkerViewAdapter(Context _context, ArrayList<SavedLocation> _locations) {
        this.mContext = _context;
        this.mLocations = _locations;
    }

    @Override
    public View getInfoWindow(Marker marker) {

        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        SavedLocation location = null;
        double markerLat = marker.getPosition().latitude;

        for (SavedLocation loc: mLocations){


            if (loc.getmLat() == markerLat){
                location = loc;
            }


        }

        View view = View.inflate(mContext, R.layout.custom_marker_window, null);

        if (location != null) {



            TextView field = (TextView) view.findViewById(R.id.window_title);
            field.setText(location.getmTitle());

            field = (TextView) view.findViewById(R.id.window_summary);
            field.setText(location.getmNote());

            ImageView image = (ImageView) view.findViewById(R.id.window_image);
            image.setImageURI(Uri.parse(location.getmImagePath()));
        }

        return view;
    }
}
