package com.daniel.dwatt.there;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlaceDetailsJSONParser {

    /**
     * Receives a JSONObject and returns a list
     */
    public List<HashMap<String, String>> searchParse(JSONObject jObject) {

        Double lat = Double.valueOf(0);
        Double lng = Double.valueOf(0);
        String locality = "";
        String formattedAddress = "";

        HashMap<String, String> hm = new HashMap<String, String>();
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        try {
                lat = (Double) jObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").get("lat");
                lng = (Double) jObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").get("lng");
                formattedAddress = (String) jObject.getJSONObject("result").get("formatted_address");
                locality = getLocalityString(jObject.getJSONObject("result").getJSONArray("address_components"));


            Log.e("Locality", locality);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        hm.put("lat", Double.toString(lat));
        hm.put("lng", Double.toString(lng));
        hm.put("formatted_address", formattedAddress);
        hm.put("locality", locality);

        list.add(hm);

        return list;
    }

    public List<HashMap<String, String>> reverseParse(JSONObject jObject) {

        Double lat = Double.valueOf(0);
        Double lng = Double.valueOf(0);
        String locality = "";
        String formattedAddress = "";

        HashMap<String, String> hm = new HashMap<String, String>();
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        try {
            lat = (Double) jObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat");
            lng = (Double) jObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng");
            formattedAddress = (String) jObject.getJSONArray("results").getJSONObject(0).get("formatted_address");
            locality = getLocalityString(jObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components"));


            Log.e("Locality", locality);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        hm.put("lat", Double.toString(lat));
        hm.put("lng", Double.toString(lng));
        hm.put("formatted_address", formattedAddress);
        hm.put("locality", locality);

        list.add(hm);

        return list;
    }

    private String getLocalityString(JSONArray jAddressComponent) {
        JSONObject addressesName = null;
        String locality = "";
        int addressComponentSize = jAddressComponent.length();
        String temp = "";
        try {
            for (int i = 0; i < addressComponentSize; i++) {
                temp = jAddressComponent.getJSONObject(i).getJSONArray("types").getString(0);
                if ((jAddressComponent.getJSONObject(i).getJSONArray("types").getString(0)).equals("locality")) {
                    locality = jAddressComponent.getJSONObject(i).getString("short_name");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return locality;
    }
}