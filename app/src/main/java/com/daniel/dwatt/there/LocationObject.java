package com.daniel.dwatt.there;


import com.google.android.gms.maps.model.LatLng;

public class LocationObject {
    String address;
    String locality;
    String shortname;
    LatLng latLng;
    double radius;

    public LocationObject(String address, String locality, LatLng latLng, double radius) {
        this.address = address;
        this.locality = locality;
        this.latLng = latLng;
        this.shortname = convertToShortName(address);
        this.radius = radius;
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
}
