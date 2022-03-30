package com.benockert.numadsp22_quester_final_project.types;

public class Place {
    public String gName;
    public String gFormattedAddress;
    public String gBusinessStatus;
    public Float gLocationLat;
    public Float gLocationLng;
    public Integer gPriceLevel;
    public Integer gAvgRating;
    public Integer gNumRatings;
    public String gMainType;

    public Place() {

    }

    public Place(String gName, String gFormattedAddress, String gBusinessStatus, Float gLocationLat, Float gLocationLng, Integer gPriceLevel, Integer gAvgRating, Integer gNumRatings, String gMainType) {
        this.gName = gName;
        this.gFormattedAddress = gFormattedAddress;
        this.gBusinessStatus = gBusinessStatus;
        this.gLocationLat = gLocationLat;
        this.gLocationLng = gLocationLng;
        this.gPriceLevel = gPriceLevel;
        this.gAvgRating = gAvgRating;
        this.gNumRatings = gNumRatings;
        this.gMainType = gMainType;
    }

    public String getgName() {
        return gName;
    }

    public String getgFormattedAddress() {
        return gFormattedAddress;
    }

    public String getgBusinessStatus() {
        return gBusinessStatus;
    }

    public Float getgLocationLat() {
        return gLocationLat;
    }

    public Float getgLocationLng() {
        return gLocationLng;
    }

    public Integer getgPriceLevel() {
        return gPriceLevel;
    }

    public Integer getgAvgRating() {
        return gAvgRating;
    }

    public Integer getgNumRatings() {
        return gNumRatings;
    }

    public String getgMainType() {
        return gMainType;
    }
}
