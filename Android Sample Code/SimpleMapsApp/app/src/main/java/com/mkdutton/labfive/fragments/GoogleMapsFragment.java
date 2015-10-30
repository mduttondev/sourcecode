package com.mkdutton.labfive.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mkdutton.labfive.DetailsActivity;
import com.mkdutton.labfive.FormActivity;
import com.mkdutton.labfive.MainActivity;
import com.mkdutton.labfive.MarkerViewAdapter;
import com.mkdutton.labfive.SavedLocation;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class GoogleMapsFragment extends MapFragment implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, GoogleMap.OnMapLongClickListener,
GoogleMap.OnInfoWindowClickListener{

    public static final String TAG = "GOOGLE_MAP_FRAGMENT";
    public static final String EXTRA_DETAILS = "com.mkdutton.labfive.EXTRA_DETAILS";

    GoogleMap mMap;

    LocationClient mLocationClient;
    public Location mLocation;
    ArrayList<SavedLocation> mLocations;

    public static GoogleMapsFragment newInstance(){
        return new GoogleMapsFragment();
    }

    public GoogleMapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationClient = new LocationClient( getActivity(), this, this );

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMap = getMap();
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        mMap.setMyLocationEnabled(true);

    }

    @Override
    public void onStart() {
        super.onStart();
        mLocationClient.connect();


    }

    @Override
    public void onResume() {
        super.onResume();



    }

    @Override
    public void onStop() {

        mLocationClient.disconnect();

        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {


        mLocation = mLocationClient.getLastLocation();

        mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLocation.getLatitude(),mLocation.getLongitude()), 15)  );


    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        SavedLocation location = null;
        double markerLat = marker.getPosition().latitude;

        for (SavedLocation loc: mLocations){

            if (loc.getmLat() == markerLat){
                location = loc;
            }

        }

        Intent detailIntent = new Intent(getActivity(), DetailsActivity.class);
        detailIntent.putExtra(EXTRA_DETAILS, location);
        startActivity(detailIntent);



    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Intent addPinIntent = new Intent(getActivity(), FormActivity.class);
        addPinIntent.putExtra(MainActivity.EXTRA_LOCATION_DATA, latLng);
        startActivity(addPinIntent);
    }


    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void updateMap(ArrayList<SavedLocation> _locations){

        mLocations = _locations;

        mMap.clear();

        MarkerViewAdapter adapter = new MarkerViewAdapter(getActivity(), mLocations);

        mMap.setInfoWindowAdapter(adapter);

        for (SavedLocation location : _locations){

           LatLng pos = new LatLng(location.getmLat(), location.getmLong());

            MarkerOptions option = new MarkerOptions();
            option.position(pos);

            mMap.addMarker(option);

        }

    }


}
