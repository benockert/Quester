package com.benockert.numadsp22_quester_final_project.types;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Activity {
    public String gFormattedAddress;
    public String gName;
    public String gPhotoReference;
    public String gPlaceId;
    public float gPlaceLat;
    public float gPlaceLng;
    public int gPriceLevel;
    public float gPopularity;
    public String uQuery;

    public Activity() {

    }

    public Activity(String gName, int gPriceLevel,
                    String gPhotoReference, String gFormattedAddress) {
        this.gName = gName;
        this.gPriceLevel = gPriceLevel;
        this.gPhotoReference = gPhotoReference;
        this.gFormattedAddress = gFormattedAddress;
    }

    public Activity(String gFormattedAddress, String gName, String gPhotoReference, String gPlaceId, float gPlaceLat, float gPlaceLng, int gPriceLevel, float gPopularity, String uQuery) {

        this.gFormattedAddress = gFormattedAddress;
        this.gName = gName;
        this.gPhotoReference = gPhotoReference;
        this.gPlaceId = gPlaceId;
        this.gPlaceLat = gPlaceLat;
        this.gPlaceLng = gPlaceLng;
        this.gPriceLevel = gPriceLevel;
        this.gPopularity = gPopularity;
        this.uQuery = uQuery;
    }

    /**
     * getter for the address of the location
     *
     * @return String representing the address of the location
     */
    public String getgFormattedAddress() {
        return gFormattedAddress;
    }

    /**
     * getter for the name of the activity location
     *
     * @return String representing the name of the activities location
     */
    public String getgName() {
        return gName;
    }

    /**
     * getter for the String referencing the photo of the activity location
     *
     * @return String representing a reference to a photo of the activity location
     */
    public String getgPhotoReference() {
        return gPhotoReference;
    }

    public String getgPlaceId() {
        return gPlaceId;
    }

    public float getgPlaceLat() {
        return gPlaceLat;
    }

    public float getgPlaceLng() {
        return gPlaceLng;
    }

    /**
     * getter for the price range of the activity
     *
     * @return String representing the number of $signs the price range is
     */
    public int getgPriceLevel() {
        return gPriceLevel;
    }

    public float getgPopularity() {
        return gPopularity;
    }

    public String getuQuery() {
        return uQuery;
    }

    public static Activity getActivityFromJSON(String data) {
        try {
            JSONObject activityObj = new JSONObject(data);
            Log.i("activities", activityObj.toString());
            String gFormattedAddress = activityObj.getString("gFormattedAddress").replaceAll("_", " ");;
            String gName = activityObj.getString("gName").replaceAll("_", " ");
            String gPhotoReference = activityObj.getString("gPhotoReference");
            String gPlaceId = activityObj.getString("gPlaceId");
            float gPlaceLat = Float.parseFloat(activityObj.getString("gPlaceLat"));
            float gPlaceLng = Float.parseFloat(activityObj.getString("gPlaceLng"));
            int gPriceLevel = activityObj.getInt("gPriceLevel");
            float gPopularity = Float.parseFloat(activityObj.getString("uPopularity"));
            String uQuery = activityObj.getString("uQuery").replaceAll("_", " ");

            return new Activity(gFormattedAddress,
                    gName,
                    gPhotoReference,
                    gPlaceId,
                    gPlaceLat,
                    gPlaceLng,
                    gPriceLevel,
                    gPopularity,
                    uQuery);
        } catch (JSONException e) {
            Log.e("ACTIVITY_PARSER", "JSONException");
            e.printStackTrace();
            return null;
        }
    }
}
