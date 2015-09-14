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

    private AlarmManager alarmManagerPositioning = null;
    public PendingIntent pendingIntent = null;

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManagerPositioning = (AlarmManager)
                getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Dwattservice", "startcom");
        cancelPendingIntent();
        createPendingIntent();
        if (pendingIntent != null) {
            try {

                long interval = 30 * 1000;
                int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
                long timetoRefresh = SystemClock.elapsedRealtime();
                alarmManagerPositioning.setInexactRepeating(alarmType,
                        timetoRefresh, interval, pendingIntent);
            } catch (NumberFormatException e) {
                Toast.makeText(this,
                        "error running service: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
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
        cancelPendingIntent();
        Log.d("Dwattservice", "destroy");
    }

    private void cancelPendingIntent() {

        if (pendingIntent != null) {
            this.alarmManagerPositioning.cancel(pendingIntent);
            Log.d("stopLocation", "cancelPendingIntent ");
        } else
            this.alarmManagerPositioning.cancel(PendingIntent.getBroadcast(this,0,new Intent (AlarmManagerHelper.ACTION_REFRESH_SCHEDULE_ALARM), 0));
    }

    private void createPendingIntent() {
        Intent intentToFire = new Intent(
                AlarmManagerHelper.ACTION_REFRESH_SCHEDULE_ALARM);
        intentToFire.putExtra(AlarmManagerHelper.COMMAND,
                AlarmManagerHelper.SENDER_SRV_POSITIONING);
        //intentToFire.putParcelableArrayListExtra("com.daniel.dwatt.there.Alarm", activeAlarmList);
        pendingIntent = PendingIntent.getBroadcast(this, 0,
                intentToFire, 0);
    }
}