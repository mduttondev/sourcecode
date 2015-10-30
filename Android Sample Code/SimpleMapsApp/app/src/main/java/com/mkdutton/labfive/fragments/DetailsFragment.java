package com.mkdutton.labfive.fragments;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mkdutton.labfive.R;
import com.mkdutton.labfive.SavedLocation;

/**
 * Created by Matt on 10/9/14.
 */
public class DetailsFragment extends Fragment {

    public static final String TAG = "DETAIL_FRAGMENT";

    public static DetailsFragment newInstance(SavedLocation _location){
        DetailsFragment frag = new DetailsFragment();
        Bundle args= new Bundle();
        args.putSerializable(GoogleMapsFragment.EXTRA_DETAILS, _location);
        frag.setArguments(args);



        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();

        if (args != null && args.containsKey(GoogleMapsFragment.EXTRA_DETAILS)){

            SavedLocation loc = (SavedLocation)args.getSerializable(GoogleMapsFragment.EXTRA_DETAILS);

            ((TextView)getActivity().findViewById(R.id.topField)).setText(loc.getmTitle());
            ((TextView)getActivity().findViewById(R.id.bottomField)).setText(loc.getmNote());
            ((ImageView)getActivity().findViewById(R.id.imageView)).setImageURI(Uri.parse(loc.getmImagePath()));


        }

    }
}
