package com.daniel.dwatt.there;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlarmManagerHelper extends BroadcastReceiver {

    public static final String COMMAND = "SENDER";
    public static final int SENDER_ACT_DOCUMENT = 0;
    public static final int SENDER_SRV_POSITIONING = 1;
    public static final int MIN_TIME_REQUEST = 5 * 1000;
    public static final String ACTION_REFRESH_SCHEDULE_ALARM =
            "com.daniel.dwatt.there.ACTION_REFRESH_SCHEDULE_ALARM";
    public static final String SHORTNAME =
            "com.daniel.dwatt.there.shortName";
    public static final String RINGTONELOCATION =
            "com.daniel.dwatt.there.ringTone";
    public static final String VIBRATE =
            "com.daniel.dwatt.there.vibrate";

    private static Location currentLocation;
    private static Location prevLocation;
    private static Context context;
    private static String addressName;
    private String provider = LocationManager.GPS_PROVIDER;
    private static Intent intent;
    private static LocationManager locationManager;
    private static ArrayList<Alarm> activeAlarmList;

    // received request from the calling service
    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
        setActiveAlarmList();
        //activeAlarmList = intent.getParcelableArrayListExtra("com.daniel.dwatt.there.Alarm");
        Log.d("NEW BROADCAST", "new Broadcast");
        Toast.makeText(context, "new broadcast",
                Toast.LENGTH_SHORT).show();
        for (Alarm alarm : activeAlarmList) {
            Log.d("Active Alarms", alarm.getAlarmObject().getLocationObject().getShortname());
        }

        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(provider) && activeAlarmList.size() != 0) {
            locationManager.requestLocationUpdates(provider,
                    MIN_TIME_REQUEST, 0, locationListener);
            Location gotLoc = locationManager
                    .getLastKnownLocation(provider);
            gotLocation(gotLoc);
        } else {
            stopLocationListener();
            Log.d("Listener", "No request sent");
        }
    }

    private static void gotLocation(Location location) {
        if (location != null) {
            prevLocation = currentLocation == null ? null : new Location(
                    currentLocation);

            currentLocation = location;
            Log.d("Locations", "curLoc.getTime = " + currentLocation.getTime());
            if (isLocationNew()) {
                Toast.makeText(context, "new location saved " + location.getLatitude() + ", " + location.getLatitude(),
                        Toast.LENGTH_SHORT).show();
            }

        checkAlarms(location);
        }
        else
        {
            Log.d("Locations", "Is Null");
            Toast.makeText(context, "no location saved because its null",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean isLocationNew() {
        if (currentLocation == null) {
            return false;
        } else if (prevLocation == null) {
            return true;
        } else if (currentLocation.getTime() == prevLocation.getTime()) {
            return false;
        } else {
            return true;
        }
    }

    private static void checkAlarms(Location location) {
        for (Alarm alarm : activeAlarmList) {
            Location alarmLocation = new Location("alarmLocation");
            alarmLocation.setLatitude((float) alarm.getAlarmObject().getLocationObject().getLatLng().latitude);
            alarmLocation.setLongitude((float) alarm.getAlarmObject().getLocationObject().getLatLng().longitude);

            float distance = alarmLocation.distanceTo(location);

            if (distance < (float) alarm.getAlarmObject().getLocationObject().getRadius() && alarm.getAlarmObject().isActive()) {
                if (!(alarm.getAlarmObject().isInFence())) {
                    Log.d("We're In!", "We're nearing: " + alarm.getAlarmObject().getLocationObject().getAddress());
                    Toast.makeText(context, "We're nearing: " + alarm.getAlarmObject().getLocationObject().getAddress(),
                            Toast.LENGTH_SHORT).show();
                    alarm.getAlarmObject().setInFence(true);
                    updateInFenceInDB(alarm, true);

                    if(!alarm.getAlarmObject().isRepeat()){
                        //Not sure if this changes the alarm in active alarmlist
                        alarm.getAlarmObject().setActive(false);
                        try {
                            AlarmDataSource dataSource = new AlarmDataSource(context);
                            dataSource.Open();
                            dataSource.setAlarmOn(alarm, false);
                            dataSource.Close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }

                    Intent alarmIntent = new Intent(context, AlarmScreenActivity.class);
                    alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    alarmIntent.putExtra(SHORTNAME, alarm.getAlarmObject().getLocationObject().getShortname());
                    alarmIntent.putExtra(RINGTONELOCATION, alarm.getAlarmObject().getRingtoneLocation());
                    alarmIntent.putExtra(VIBRATE, alarm.getAlarmObject().isVibrate());
                    context.startActivity(alarmIntent);

                } else {
                    Log.d("We're In!", "ALREADY IN FENCE THOUGH " + alarm.getAlarmObject().getLocationObject().getAddress());
                }
            } else {
                Log.d("We're NOT In!", "Not even close: " + alarm.getAlarmObject().getLocationObject().getAddress() + " by " + distance + "m");
                if (alarm.getAlarmObject().isInFence()) {
                    alarm.getAlarmObject().setInFence(false);
                    updateInFenceInDB(alarm, false);
                }
            }


        }
    }

    private static LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            try {
                String strStatus = "";
                switch (status) {
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        strStatus = "GPS_EVENT_FIRST_FIX";
                        break;
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                        strStatus = "GPS_EVENT_SATELLITE_STATUS";
                        break;
                    case GpsStatus.GPS_EVENT_STARTED:
                        strStatus = "GPS_EVENT_STARTED";
                        break;
                    case GpsStatus.GPS_EVENT_STOPPED:
                        strStatus = "GPS_EVENT_STOPPED";
                        break;
                    default:
                        strStatus = String.valueOf(status);
                        break;
                }
                Toast.makeText(context, "Status: " + strStatus,
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            try {
                Toast.makeText(context, "***new location***",
                        Toast.LENGTH_SHORT).show();
                gotLocation(location);
            } catch (Exception e) {
            }
        }
    };

    public static void stopLocationListener() {
        locationManager.removeUpdates(locationListener);
        Log.d("Listener", "stop Listening");
    }

    //Look for a better way to keep status of Alarm isInFence
    private static void updateInFenceInDB(Alarm alarm, boolean isInFence) {
        //android.os.Debug.waitForDebugger();
        try {
            AlarmDataSource dataSource = new AlarmDataSource(context);
            dataSource.Open();
            dataSource.setInFenceActive(alarm, isInFence);
            dataSource.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setActiveAlarmList() {
        try {
            AlarmDataSource dataSource = new AlarmDataSource(context);
            dataSource.Open();
            List<Alarm> alarmList = dataSource.GetAllAlarms();
            dataSource.Close();
            activeAlarmList = new ArrayList<Alarm>();
            if (alarmList != null) {
                for (Alarm alarm : alarmList) {
                    if (alarm.getAlarmObject().isActive()) {
                        activeAlarmList.add(alarm);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
