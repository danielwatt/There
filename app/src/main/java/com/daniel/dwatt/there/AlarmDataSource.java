package com.daniel.dwatt.there;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlarmDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_ADDRESS,
            MySQLiteHelper.COLUMN_LOCALITY,
            MySQLiteHelper.COLUMN_SHORTNAME,
            MySQLiteHelper.COLUMN_LATITUDE,
            MySQLiteHelper.COLUMN_LONGITUDE,
            MySQLiteHelper.COLUMN_RADIUS,
            MySQLiteHelper.COLUMN_RINGTONELOCATION,
            MySQLiteHelper.COLUMN_REPEAT,
            MySQLiteHelper.COLUMN_VIBRATE,
            MySQLiteHelper.COLUMN_ACTIVE};

    public AlarmDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void Open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void Close() {
        dbHelper.close();
    }

    public void AddAlarm(Alarm alarm) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ADDRESS, alarm.getAlarmObject().getLocationObject().getAddress()); //1
        values.put(MySQLiteHelper.COLUMN_LOCALITY, alarm.getAlarmObject().getLocationObject().getLocality()); //2
        values.put(MySQLiteHelper.COLUMN_SHORTNAME, alarm.getAlarmObject().getLocationObject().getShortname()); //3
        values.put(MySQLiteHelper.COLUMN_LATITUDE, alarm.getAlarmObject().getLocationObject().getLatLng().latitude);//4
        values.put(MySQLiteHelper.COLUMN_LONGITUDE, alarm.getAlarmObject().getLocationObject().getLatLng().longitude);//5

        values.put(MySQLiteHelper.COLUMN_RADIUS, alarm.getAlarmObject().getLocationObject().getRadius()); //6
        values.put(MySQLiteHelper.COLUMN_RINGTONELOCATION, alarm.getAlarmObject().getRingtoneLocation());//7
        values.put(MySQLiteHelper.COLUMN_REPEAT, alarm.getAlarmObject().isRepeat());//8
        values.put(MySQLiteHelper.COLUMN_VIBRATE, alarm.getAlarmObject().isVibrate());//9
        values.put(MySQLiteHelper.COLUMN_ACTIVE, alarm.getAlarmObject().isActive());//10

        database.insert(MySQLiteHelper.TABLE_ALARM, null, values);
    }

    public void deleteAlarm(Alarm alarm) {
        database.delete(MySQLiteHelper.TABLE_ALARM, MySQLiteHelper.COLUMN_ID + " = " + alarm.getid(), null);
    }

    public void setAlarmOn(Alarm alarm, boolean alarmActive){
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(MySQLiteHelper.COLUMN_ACTIVE, alarmActive);
        database.update(MySQLiteHelper.TABLE_ALARM, dataToInsert, MySQLiteHelper.COLUMN_ID + " = " + alarm.getid(), null);
    }

    public void setRepeatOn (Alarm alarm, boolean repeatActive){
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(MySQLiteHelper.COLUMN_REPEAT, repeatActive);
        database.update(MySQLiteHelper.TABLE_ALARM,dataToInsert, MySQLiteHelper.COLUMN_ID + " = " + alarm.getid(),null);
    }

    public void setVibrateOn (Alarm alarm, boolean vibrateActive){
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(MySQLiteHelper.COLUMN_VIBRATE, vibrateActive);
        database.update(MySQLiteHelper.TABLE_ALARM,dataToInsert, MySQLiteHelper.COLUMN_ID + " = " + alarm.getid(),null);
    }

    public List<Alarm> GetAllAlarms() {
        List<Alarm> alarmList = new ArrayList<Alarm>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ALARM,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Alarm alarm = cursorToAlarm(cursor);
            alarmList.add(alarm);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return alarmList;
    }

    private Alarm cursorToAlarm(Cursor cursor){
        Alarm alarm = new Alarm();
        AlarmObject alarmObject = new AlarmObject();
        LocationObject locationObject = new LocationObject();

        alarm.setid(cursor.getInt(0));
        alarmObject.setRingtoneLocation(cursor.getString(7));
        alarmObject.setRepeat(cursor.getInt(8) == 1);
        alarmObject.setVibrate(cursor.getInt(9) == 1);
        alarmObject.setActive(cursor.getInt(10) == 1);

        locationObject.setAddress(cursor.getString(1));
        locationObject.setLocality(cursor.getString(2));
        locationObject.setShortname(cursor.getString(3));
        locationObject.setLatLng(new LatLng(cursor.getDouble(4), cursor.getDouble(5)));
        locationObject.setRadius(cursor.getDouble(6));

        alarmObject.setLocationObject(locationObject);
        alarm.setAlarmObject(alarmObject);

        return alarm;
    }


}
