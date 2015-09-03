package com.daniel.dwatt.there;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.CursorLoader;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class EditorActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private MapFragment mapFrag;
    private GoogleMap mGoogleMap;
    private List<Marker> tempMarkerList = new ArrayList<Marker>();
    private Circle markerCircle = null;
    private Marker marker = null;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private LocationObject locationObject = null;
    private AlarmDataSource dataSource;
    private LatLng currentLatLngLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        final SearchView searchBox = (SearchView) findViewById(R.id.searchBox);
        searchBox.requestFocus();

        FragmentManager myFragmentManager = getFragmentManager();

        mapFrag = (MapFragment) myFragmentManager.findFragmentById(R.id.mainMapFrag);

        mGoogleMap = mapFrag.getMap();
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(false);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        handleIntent(getIntent());


        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //TO-DO find more efficient way to hide keyboard.
                hideKeyboard(searchBox);

                ConnectivityManager cm =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null &&
                        netInfo.isConnected()) {

                    ReverseLocationGeocoder reverseLocationGeocoder = new ReverseLocationGeocoder();
                    double markerCircleRadius = 100;
                    if (locationObject != null) {
                        markerCircleRadius = locationObject.getRadius();
                    }

                    locationObject = reverseLocationGeocoder.query(latLng);
                    locationObject.setRadius(markerCircleRadius);
                    clearTempMarkers();

                    LatLng position = locationObject.getLatLng();

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(position);
                    markerOptions.title(locationObject.getAddress());
                    markerOptions.draggable(false);
                    marker = mGoogleMap.addMarker(markerOptions);
                    marker.showInfoWindow();
                    tempMarkerList.add(marker);

                    addMarkerCircle(position);

                    enableSetLocationButton();
                } else {
                    Toast.makeText(getBaseContext(), "No Network Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                setTransparentLayoutVisibility();

                return false;
            }
        });

        RelativeLayout transparentLayoutForGesture = (RelativeLayout) findViewById(R.id.transparentLayoutForGesture);
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        gestureDetector = new GestureDetector(this, new SingleTapConfirm());

        transparentLayoutForGesture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    hideTransparentLayoutVisibility();
                } else {
                    scaleGestureDetector.onTouchEvent(event);
                }
                return true;
            }
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchBox.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        ImageButton setLocationButton = (ImageButton) findViewById(R.id.setLocationButton);
        setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationObject != null) {
                    try {
                        dataSource = new AlarmDataSource(getBaseContext());
                        dataSource.Open();
                        Alarm alarm = LocationObjectToAlarm(locationObject);
                        dataSource.AddAlarm(alarm);
                        dataSource.Close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(mainIntent);
                } else {
                    Toast.makeText(getBaseContext(), "Set a location first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                currentLatLngLoc = new LatLng(location.getLatitude(), location.getLongitude());
            }
        });

        ImageButton editor_myCurLocButton = (ImageButton) findViewById(R.id.editor_myCurLocButton);
        editor_myCurLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLatLngLoc != null) {
                    CameraUpdate currentLoc = CameraUpdateFactory.newLatLngZoom(currentLatLngLoc, 13);
                    mGoogleMap.animateCamera(currentLoc);
                } else {
                    Toast.makeText(getBaseContext(), "Current location unknown", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(

                "SavedLocation", Context.MODE_PRIVATE);

        currentLatLngLoc = new LatLng(getDouble(prefs, "SavedLat", 0), getDouble(prefs,"SavedLong", 0));

        if (currentLatLngLoc.latitude != 0 && currentLatLngLoc.longitude != 0)
        {
            CameraUpdate savedLoc = CameraUpdateFactory.newLatLngZoom(currentLatLngLoc, 13);
            mGoogleMap.animateCamera(savedLoc);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (currentLatLngLoc != null) {
            SharedPreferences prefs = getSharedPreferences(
                    "SavedLocation", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            putDouble(editor, "SavedLat", currentLatLngLoc.latitude);
            putDouble(editor, "SavedLong", currentLatLngLoc.longitude);
            editor.apply();
        }
    }

    private void setTransparentLayoutVisibility() {
        TextView radiusText = (TextView) findViewById(R.id.radiusText);
        TextView actionBarText = (TextView) findViewById(R.id.actionBarText);
        if (markerCircle != null) {
            radiusText.setText("Alarm Radius: " + (int) markerCircle.getRadius() + "m");
        } else {
            radiusText.setText("Cannot Find Alarm Radius");
        }

        actionBarText.setText(R.string.actionBarTextAdjust);

        RelativeLayout transparentLayoutForGesture = (RelativeLayout) findViewById(R.id.transparentLayoutForGesture);
        transparentLayoutForGesture.setVisibility(View.VISIBLE);
        transparentLayoutForGesture.setFocusable(true);
        transparentLayoutForGesture.setFocusableInTouchMode(true);
        transparentLayoutForGesture.requestFocus();
        ImageButton editor_myCurLocButton = (ImageButton) findViewById(R.id.editor_myCurLocButton);
        editor_myCurLocButton.setClickable(false);
    }

    private void hideTransparentLayoutVisibility() {
        TextView actionBarText = (TextView) findViewById(R.id.actionBarText);
        RelativeLayout transparentLayoutForGesture = (RelativeLayout) findViewById(R.id.transparentLayoutForGesture);

        actionBarText.setText(R.string.actionBarTextDefault);
        transparentLayoutForGesture.setVisibility(View.GONE);

        ImageButton editor_myCurLocButton = (ImageButton) findViewById(R.id.editor_myCurLocButton);
        editor_myCurLocButton.setClickable(true);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            doSearch(intent.getStringExtra(SearchManager.QUERY));
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            getPlace(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
        }
    }

    private void doSearch(String query) {
        Bundle data = new Bundle();
        data.putString("query", query);
        getLoaderManager().restartLoader(0, data, this);
    }

    private void getPlace(String query) {
        Bundle data = new Bundle();
        data.putString("query", query);
        getLoaderManager().restartLoader(1, data, this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle query) {
        CursorLoader cLoader = null;

        if (arg0 == 0) {
            Log.e("DWatt", "Search_URI");
            cLoader = new CursorLoader(getBaseContext(), PlaceProvider.SEARCH_URI, null, null, new String[]{query.getString("query")}, null);
        } else if (arg0 == 1) {
            Log.e("DWatt", "Query_URI");
            cLoader = new CursorLoader(getBaseContext(), PlaceProvider.DETAILS_URI, null, null, new String[]{query.getString("query")}, null);
        }
        return cLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {

        showLocations(c);
    }


    public void returnToMainActivity(View v) {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }


    private void hideKeyboard(SearchView searchBox) {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
        }
    }

    private void showLocations(Cursor c) {
        MarkerOptions markerOptions = null;
        LatLng position = null;
        clearTempMarkers();
        while (c.moveToNext()) {
            markerOptions = new MarkerOptions();
            position = new LatLng(Double.parseDouble(c.getString(1)), Double.parseDouble(c.getString(2)));
            markerOptions.position(position);
            markerOptions.title(c.getString(0));
            markerOptions.draggable(false);
            marker = mGoogleMap.addMarker(markerOptions);
            marker.showInfoWindow();
            double markerCircleRadius = 100;
            if (locationObject != null)
            {
                markerCircleRadius = locationObject.getRadius();
            }
            locationObject = new LocationObject(c.getString(0), c.getString(3), position, markerCircleRadius);
            tempMarkerList.add(marker);
        }

        if (position != null) {
            addMarkerCircle(position);
            enableSetLocationButton();
        }
    }

    private void addMarkerCircle(LatLng position) {
        if (markerCircle == null) {
            markerCircle = mGoogleMap.addCircle(new CircleOptions()
                    .center(position)
                    .radius(locationObject.getRadius())
                    .strokeColor(R.color.main_bg_color)
                    .strokeWidth(0)
                    .fillColor(R.color.transparent_bg_color));
        } else {
            markerCircle.setCenter(position);
            markerCircle.setRadius(locationObject.getRadius());
        }
        CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(position, 16);
        mGoogleMap.animateCamera(cameraPosition);

        setTransparentLayoutVisibility();
    }

    private void clearTempMarkers() {
        int listSize = tempMarkerList.size();
        for (int i = 0; i < listSize; i++) {
            tempMarkerList.get(0).remove();
            tempMarkerList.remove(0);
        }
    }

    private void enableSetLocationButton() {
        if (locationObject != null) {
            ImageButton setLocationButton = (ImageButton) findViewById(R.id.setLocationButton);
            setLocationButton.setColorFilter(getResources().getColor(R.color.checkmark_color), PorterDuff.Mode.SRC_IN);

    }
}

private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        double markerCircleRadius = markerCircle.getRadius();
        RelativeLayout transparentLayoutForGesture = (RelativeLayout) findViewById(R.id.transparentLayoutForGesture);
        if (transparentLayoutForGesture.isFocused() && markerCircle != null) {
            float scale = 1.f;

            scale *= detector.getScaleFactor();
            scale = Math.max(0.1f, Math.min(scale, 10.f));

            if (markerCircleRadius < 25 && scale > 1) {
                markerCircleRadius *= scale * 2;
            } else {
                markerCircleRadius *= scale;
            }

            //Radius minimum limit
            if (markerCircleRadius < 1) {
                markerCircleRadius = 1;
            }

            Circle tempCircle = mGoogleMap.addCircle(new CircleOptions()
                    .center(markerCircle.getCenter())
                    .radius(markerCircleRadius)
                    .strokeColor(R.color.main_bg_color)
                    .strokeWidth(0)
                    .fillColor(R.color.transparent_bg_color));

            markerCircle.setRadius(markerCircleRadius);
            locationObject.setRadius(markerCircleRadius);

            TextView radiusText = (TextView) findViewById(R.id.radiusText);

            radiusText.setText("Alarm Radius: " + (int) markerCircleRadius + "m");
            tempCircle.remove();

        }
        return true;
    }
}

private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }
}

    private Alarm LocationObjectToAlarm (LocationObject locationObject){
        Alarm alarm = new Alarm();
        AlarmObject alarmObject = new AlarmObject(locationObject,"default address here",true, true, true);
        alarm.setAlarmObject(alarmObject);

        return alarm;
    }

    private SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    private double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

}
