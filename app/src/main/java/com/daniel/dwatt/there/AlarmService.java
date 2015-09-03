package com.daniel.dwatt.there;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlarmService extends Service {
    // An alarm for rising in special times to fire the
    // pendingIntentList
    private AlarmManager alarmManagerPositioning = null;
    // A PendingIntent for calling a receiver in special times
    public ArrayList<PendingIntent> pendingIntentList = null;

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManagerPositioning = (AlarmManager)
                getSystemService(Context.ALARM_SERVICE);
    }

    ;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Dwattservice", "startcom");
        cancelAlarm();
        setAlarm();
        if (pendingIntentList != null) {
            int listLength = pendingIntentList.size();

            for (int i = 0; i < listLength; i++) {
                try {

                    long interval = 60 * 1000;
                    int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
                    long timetoRefresh = SystemClock.elapsedRealtime();
                    alarmManagerPositioning.setInexactRepeating(alarmType,
                            timetoRefresh, interval, pendingIntentList.get(i));
                } catch (NumberFormatException e) {
                    Toast.makeText(this,
                            "error running service: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        cancelAlarm();
        Log.d("Dwattservice", "destroycom");
    }

    private void setAlarm() {
        pendingIntentList = new ArrayList<PendingIntent>();
        try {
            AlarmDataSource dataSource = new AlarmDataSource(this);
            dataSource.Open();
            List<Alarm> alarmList = dataSource.GetAllAlarms();
            for (Alarm alarm : alarmList)
            {
                Log.d("alarm", alarm.getAlarmObject().getLocationObject().getAddress());
            }
            dataSource.Close();
            if (alarmList != null) {
                for (Alarm alarm : alarmList) {
                    if (alarm.getAlarmObject().isActive()) {
                        pendingIntentList.add(createPendingIntent(this, alarm));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void cancelAlarm() {

        if (pendingIntentList != null) {

            int listLength = pendingIntentList.size();

            for (int i = 0; i < listLength; i++) {
                this.alarmManagerPositioning.cancel(pendingIntentList.get(i));
                Log.d("stopLocation", "cancelAlarm " + i);
            }
        }
    }

    private PendingIntent createPendingIntent(Context context, Alarm alarm) {
        Intent intentToFire = new Intent(
                AlarmManagerHelper.ACTION_REFRESH_SCHEDULE_ALARM);
        intentToFire.putExtra("Alarm Address",
                alarm.getAlarmObject().getLocationObject().getAddress());
        intentToFire.putExtra("Alarm Longitude",
                alarm.getAlarmObject().getLocationObject().getLatLng().longitude);
        intentToFire.putExtra("Alarm Latitude",
                alarm.getAlarmObject().getLocationObject().getLatLng().latitude);
        intentToFire.putExtra("Alarm Radius",
                alarm.getAlarmObject().getLocationObject().getRadius());
        intentToFire.putExtra("Alarm Vibrate",
                alarm.getAlarmObject().isVibrate());
        intentToFire.putExtra("Alarm Ringtone",
                alarm.getAlarmObject().getRingtoneLocation());
        intentToFire.putExtra("Alarm Repeat",
                alarm.getAlarmObject().isRepeat());

        return PendingIntent.getBroadcast(this, alarm.getid(),
                intentToFire, 0);
    }
}