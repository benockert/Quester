package com.benockert.numadsp22_quester_final_project.types;

public class Activity {
    public Integer orderInQuest;
    public String uQuery;
    public Integer uPriceRange;
    public Float uPopularity;
    public String gPlaceId;

    public Activity() {

    }

    public Activity(Integer orderInQuest, String uQuery, Integer uPriceRange, Float uPopularity, String gPlaceId) {
        this.orderInQuest = orderInQuest;
        this.uQuery = uQuery;
        this.uPriceRange = uPriceRange;
        this.uPopularity = uPopularity;
        this.gPlaceId = gPlaceId;
    }

    public Integer getOrderInQuest() {
        return orderInQuest;
    }

    public String getuQuery() {
        return uQuery;
    }

    public Integer getuPriceRange() {
        return uPriceRange;
    }

    public Float getuPopularity() {
        return uPopularity;
    }

    public String getgPlaceId() {
        return gPlaceId;
    }
}
