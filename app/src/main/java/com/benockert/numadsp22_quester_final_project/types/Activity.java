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

    public String getgFormattedAddress() {
        return gFormattedAddress;
    }

    public String getgName() {
        return gName;
    }

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
