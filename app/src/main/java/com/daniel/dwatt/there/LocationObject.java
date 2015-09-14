package com.daniel.dwatt.there;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class LocationObject implements Parcelable {
    private String address;
    private String locality;
    private String shortname;
    private LatLng latLng;
    private double radius;

    public LocationObject() {

    }

    public LocationObject(String address, String locality, LatLng latLng, double radius) {
        this.address = address;
        this.locality = locality;
        this.latLng = latLng;
        this.shortname = convertToShortName(address);
        this.radius = radius;
    }

    public LocationObject(Parcel in) {
        readFromParcel(in);
    }

    public String getAddress() {
        return address;
    }

    public String getLocality() {
        return locality;
    }

    public String getShortname() {
        return shortname;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public double getRadius() {
        return radius;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    private String convertToShortName(String address) {
        String ret = "";

        if (address != null) {
            int index = address.indexOf(",");
            if (index != -1) {
                ret = address.substring(0, index);
            }

        }
        return ret;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel in) {

        address = in.readString();
        locality = in.readString();
        shortname = in.readString();

        Double latitude = in.readDouble();
        Double longitude = in.readDouble();
        latLng = new LatLng(latitude, longitude);

        radius = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(locality);
        dest.writeString(shortname);
        dest.writeDouble(latLng.latitude);
        dest.writeDouble(latLng.longitude);
        dest.writeDouble(radius);
    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public LocationObject createFromParcel(Parcel in) {
                    return new LocationObject(in);
                }

                public LocationObject[] newArray(int size) {
                    return new LocationObject[size];
                }
            };
}
