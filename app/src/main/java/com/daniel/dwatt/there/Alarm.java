package com.daniel.dwatt.there;


import android.os.Parcel;
import android.os.Parcelable;

public class Alarm implements Parcelable {
    int _id;
    AlarmObject _alarmObject;

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Alarm createFromParcel(Parcel in) {
                    return new Alarm(in);
                }
                public Alarm[] newArray(int size) {
                    return new Alarm[size];
                }
            };

    public Alarm(){
    }

    public Alarm (Parcel in) {
        readFromParcel(in);
    }

    public int getid() {
        return _id;
    }

    public AlarmObject getAlarmObject() {
        return _alarmObject;
    }

    public void setid(int _id) {
        this._id = _id;
    }

    public void setAlarmObject(AlarmObject alarmObject) {
        this._alarmObject = alarmObject;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeParcelable(_alarmObject,flags);
    }

    private void readFromParcel(Parcel in) {
        _id = in.readInt();
        _alarmObject = in.readParcelable(AlarmObject.class.getClassLoader());
    }
}
