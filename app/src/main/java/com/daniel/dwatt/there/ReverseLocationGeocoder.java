package com.daniel.dwatt.there;


import android.os.StrictMode;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class ReverseLocationGeocoder {

    String mKey = "key=AIzaSyC6B-_hd8kLofTaoqILx532gXS-xe6SD7A";


    public LocationObject query(LatLng latLng) {

        LocationObject locationObject = new LocationObject(null, null, null, 0);
        PlaceDetailsJSONParser detailsParser = new PlaceDetailsJSONParser();

        String jsonPlaceDetails = "";
        List<HashMap<String, String>> detailsList = null;


        detailsParser = new PlaceDetailsJSONParser();
        jsonPlaceDetails = getPlaceDetails(latLng.latitude, latLng.longitude);
        try {
            detailsList = detailsParser.reverseParse(new JSONObject(jsonPlaceDetails));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HashMap<String, String> hMapDetails = detailsList.get(0);

        if (!(hMapDetails.get("formatted_address")).equals(null)) {
            locationObject = new LocationObject(hMapDetails.get("formatted_address"), hMapDetails.get("locality"), latLng, 100);

        }
        return locationObject;
    }

    private String downloadUrl(String strUrl) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();

        StrictMode.setThreadPolicy(policy);
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception dl url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private String getPlaceDetailsUrl(double lat, double lng) {

        // reference of place
        String reference = "latlng=" + lat + "," + lng;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = reference + "&" + sensor + "&" + mKey;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/geocode/" + output + "?" + parameters;

        return url;
    }

    private String getPlaceDetails(double lat, double lng) {
        String data = "";
        String url = getPlaceDetailsUrl(lat, lng);
        try {
            data = downloadUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

}
