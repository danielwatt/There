package com.daniel.dwatt.there;


public class Alarm {
    int _id;
    AlarmObject _alarmObject;

    public Alarm(){
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
}
