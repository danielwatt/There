package com.daniel.dwatt.there;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_ALARM = "alarm";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LOCALITY = "locality";
    public static final String COLUMN_SHORTNAME = "shortname";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_RADIUS = "radius";
    public static final String COLUMN_RINGTONELOCATION = "ringtone_location";
    public static final String COLUMN_REPEAT = "repeat";
    public static final String COLUMN_VIBRATE = "vibrate";
    public static final String COLUMN_ACTIVE = "active";

    private static final String DATABASE_NAME = "alarm.db";
    private static final int DATABASE_VERSION = 2;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table "
                + TABLE_ALARM + "(" + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_ADDRESS + " text not null, "
                + COLUMN_LOCALITY + " text not null, "
                + COLUMN_SHORTNAME + " text not null, "
                + COLUMN_LATITUDE + " real, "
                + COLUMN_LONGITUDE + " real, "
                + COLUMN_RADIUS + " real, "
                + COLUMN_RINGTONELOCATION + " text not null, "
                + COLUMN_REPEAT + " integer, "
                + COLUMN_VIBRATE + " integer, "
                + COLUMN_ACTIVE + " integer);";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARM);
        onCreate(db);
    }


}
