package com.benockert.numadsp22_quester_final_project.types;

public class Activity {
    public String gFormattedAddress;
    public String gName;
    public String gPhotoReference;
    public String gPlaceId;
    public float gPlaceLat;
    public float gPlaceLng;
    public int gPriceLevel;
    public String uQuery;

    public Activity(String gFormattedAddress, String gName, String gPhotoReference, String gPlaceId, float gPlaceLat, float gPlaceLng, int gPriceLevel, String uQuery) {
        this.gFormattedAddress = gFormattedAddress;
        this.gName = gName;
        this.gPhotoReference = gPhotoReference;
        this.gPlaceId = gPlaceId;
        this.gPlaceLat = gPlaceLat;
        this.gPlaceLng = gPlaceLng;
        this.gPriceLevel = gPriceLevel;
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

    public String getuQuery() {
        return uQuery;
    }
}
