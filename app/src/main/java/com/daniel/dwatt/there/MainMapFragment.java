package com.daniel.dwatt.there;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class MainMapFragment extends Fragment implements OnMapReadyCallback {

    MapFragment mainMapFrag;
    GoogleMap mainMap;
    LatLng currentLatLngLoc;
    MainMapListener activityCommander;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main_map, container, false);

        ImageButton myCurLocButton = (ImageButton) rootView.findViewById(R.id.myCurLocButton);
        myCurLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLatLngLoc != null) {
                    CameraUpdate currentLoc = CameraUpdateFactory.newLatLngZoom(currentLatLngLoc, 14);
                    mainMap.animateCamera(currentLoc);
                } else {
                    Toast.makeText(getActivity(), "Current location unknown", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainMapFrag = (MapFragment) getChildFragmentManager().findFragmentById(R.id.mainMapFrag);
        mainMap = mainMapFrag.getMap();
        mainMap.setMyLocationEnabled(true);
        mainMap.getUiSettings().setMyLocationButtonEnabled(false);

        mainMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                currentLatLngLoc = new LatLng(location.getLatitude(), location.getLongitude());
            }
        });


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            activityCommander = (MainMapListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        activityCommander = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (currentLatLngLoc != null) {
            activityCommander.setSavedLocation(currentLatLngLoc);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        LatLng savedLatLngLoc;
        savedLatLngLoc = activityCommander.getSavedLocation();

        if (savedLatLngLoc != null && (savedLatLngLoc.latitude != 0 && savedLatLngLoc.longitude != 0))
        {
            CameraUpdate savedLoc = CameraUpdateFactory.newLatLngZoom(savedLatLngLoc, 14);
            mainMap.animateCamera(savedLoc);
        }
    }

    public interface MainMapListener {
        public void setSavedLocation(LatLng currentLatLngLoc);
        public LatLng getSavedLocation();
    }
}