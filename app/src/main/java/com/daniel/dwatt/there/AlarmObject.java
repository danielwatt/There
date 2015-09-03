package com.daniel.dwatt.there;


public class AlarmObject {

    private LocationObject locationObject;
    private String ringtoneLocation;
    private boolean repeat;
    private boolean vibrate;
    private boolean active;

    public AlarmObject(){

    }


    public AlarmObject(LocationObject locationObject, String ringtoneLocation, boolean repeat, boolean vibrate, boolean active){
        this.locationObject = locationObject;
        this.ringtoneLocation = ringtoneLocation;
        this.repeat = repeat;
        this.vibrate = vibrate;
        this.active = active;
    }

    public void setLocationObject(LocationObject locationObject) {
        this.locationObject = locationObject;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void setRingtoneLocation(String ringtoneLocation) {
        this.ringtoneLocation = ringtoneLocation;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocationObject getLocationObject() {
        return locationObject;
    }

    public String getRingtoneLocation() {
        return ringtoneLocation;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public boolean isActive() {
        return active;
    }
}
