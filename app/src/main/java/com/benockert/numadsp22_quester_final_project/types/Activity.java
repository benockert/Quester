package com.benockert.numadsp22_quester_final_project.types;

public class Activity {
    public String gFormattedAddress;
    public String gName;
    public String gPhotoReference;
    public String gPlaceId;
    public double gPlaceLat;
    public double gPlaceLng;
    public int uPriceLevel;
    public String uQuery;

    public Activity(String gFormattedAddress, String gName, String gPhotoReference, String gPlaceId, double gPlaceLat, double gPlaceLng, int uPriceLevel, String uQuery) {
        this.gFormattedAddress = gFormattedAddress;
        this.gName = gName;
        this.gPhotoReference = gPhotoReference;
        this.gPlaceId = gPlaceId;
        this.gPlaceLat = gPlaceLat;
        this.gPlaceLng = gPlaceLng;
        this.uPriceLevel = uPriceLevel;
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

    public double getgPlaceLat() {
        return gPlaceLat;
    }

    public double getgPlaceLng() {
        return gPlaceLng;
    }

    public int getuPriceLevel() {
        return uPriceLevel;
    }

    public String getuQuery() {
        return uQuery;
    }

}
