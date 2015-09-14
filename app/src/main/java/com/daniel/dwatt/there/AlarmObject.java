package com.daniel.dwatt.there;


import android.os.Parcel;
import android.os.Parcelable;

public class AlarmObject implements Parcelable {

    private LocationObject locationObject;
    private String ringtoneLocation;
    private boolean repeat;
    private boolean vibrate;
    private boolean active;
    private boolean inFence;
    private String ringtoneTitle;

    public AlarmObject() {
    }

    public AlarmObject(Parcel in) {
        readFromParcel(in);
    }


    public AlarmObject(LocationObject locationObject, String ringtoneLocation, boolean repeat, boolean vibrate, boolean active, String ringtoneTitle) {
        this.locationObject = locationObject;
        this.ringtoneLocation = ringtoneLocation;
        this.repeat = repeat;
        this.vibrate = vibrate;
        this.active = active;
        this.inFence = false;
        if(ringtoneTitle != null) {
            this.ringtoneTitle = ringtoneTitle;
        }
        else
        {
            this.ringtoneTitle = "None";
        }
    }

    public void setInFence(boolean inFence) {
        this.inFence = inFence;
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

    public void setRingtoneTitle(String ringtoneTitle) {
        this.ringtoneTitle = ringtoneTitle;
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

    public boolean isInFence() {
        return inFence;
    }

    public String getRingtoneTitle() {
        return ringtoneTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(locationObject, flags);
        dest.writeString(ringtoneLocation);
        dest.writeByte((byte) (repeat ? 1 : 0));
        dest.writeByte((byte) (vibrate ? 1 : 0));
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeByte((byte) (inFence ? 1 : 0));
        dest.writeString(ringtoneTitle);
    }

    private void readFromParcel(Parcel in) {
        locationObject = in.readParcelable(LocationObject.class.getClassLoader());
        ringtoneLocation = in.readString();
        repeat = in.readByte() != 0;
        vibrate = in.readByte() != 0;
        active = in.readByte() != 0;
        inFence = in.readByte() != 0;
        ringtoneTitle = in.readString();
    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public AlarmObject createFromParcel(Parcel in) {
                    return new AlarmObject(in);
                }

                public AlarmObject[] newArray(int size) {
                    return new AlarmObject[size];
                }
            };
}
