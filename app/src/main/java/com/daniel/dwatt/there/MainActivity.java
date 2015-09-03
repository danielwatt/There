package com.daniel.dwatt.there;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff.Mode;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.ImageButton;

import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLException;
import java.util.List;


public class MainActivity extends Activity implements ExpandableListFragment.ExpandableListViewListener, MainMapFragment.MainMapListener {

    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private LatLng savedLatLngLoc;
    private AlarmDataSource dataSource;
    private List<Alarm> listAlarm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkGPSEnabled();

        FragmentManager fm = getFragmentManager();
        addFragmentMenuListener(R.id.alarm_button, R.id.map_button, fm.findFragmentById(R.id.alarmFragment), fm.findFragmentById(R.id.mapFragment));

        

    }

    void addFragmentMenuListener(int abutton, int mbutton, final Fragment afragment, final Fragment mfragment) {
        final ImageButton alarmButton = (ImageButton) findViewById(abutton);
        final ImageButton mapButton = (ImageButton) findViewById(mbutton);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.hide(mfragment);
        alarmButton.setColorFilter(getResources().getColor(R.color.text_color), Mode.SRC_IN);

        ft.commit();


        alarmButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                alarmButton.setColorFilter(getResources().getColor(R.color.text_color), Mode.SRC_IN);
                mapButton.setColorFilter(getResources().getColor(R.color.secondary_text_color), Mode.SRC_IN);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                //Temporary
                ft.show(afragment);
                ft.hide(mfragment);
                ft.commit();
            }
        });

        mapButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                alarmButton.setColorFilter(getResources().getColor(R.color.secondary_text_color), Mode.SRC_IN);
                mapButton.setColorFilter(getResources().getColor(R.color.text_color), Mode.SRC_IN);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                //Temporary
                ft.show(mfragment);
                ft.hide(afragment);

                ft.commit();
            }
        });

        try{
            dataSource = new AlarmDataSource(this);
            dataSource.Open();
            listAlarm = dataSource.GetAllAlarms();
        }catch (SQLException e)
        {
            e.printStackTrace();
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!gps_enabled && !network_enabled) {
            checkGPSEnabled();
        }

        SharedPreferences prefs = getSharedPreferences(
                "SavedLocation", Context.MODE_PRIVATE);

        savedLatLngLoc = new LatLng(getDouble(prefs, "SavedLat", 0), getDouble(prefs,"SavedLong", 0));

    }

    protected void checkGPSEnabled() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(this.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(this.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    System.exit(0);
                }
            });
            dialog.show();
        }
    }

    //Gets called by ExpandableListFragment when user clicks addAlarmButton
    @Override
    public void launchEditor() {
        Intent editorIntent = new Intent(this, EditorActivity.class);
        startActivity(editorIntent);

    }

    @Override
    public List<Alarm> queryAlarmList() {
        return listAlarm;
    }

    @Override
    public void setSavedLocation(LatLng savedLatLngLoc) {
        this.savedLatLngLoc = savedLatLngLoc;
    }

    @Override
    public LatLng getSavedLocation() {
        return savedLatLngLoc;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (savedLatLngLoc != null) {
            SharedPreferences prefs = getSharedPreferences(
                    "SavedLocation", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            putDouble(editor, "SavedLat", savedLatLngLoc.latitude);
            putDouble(editor, "SavedLong", savedLatLngLoc.longitude);
            editor.apply();
        }
    }

    private SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    private double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

}
