package com.benockert.numadsp22_quester_final_project.activeQuest.previewStopCard;

public class PreviewCard {

    public String placeName;
    public String searchQuery;
    public String activityCount;

    public PreviewCard(String placeName, String searchQuery, String activityCount) {
        this.placeName = placeName;
        this.searchQuery = searchQuery;
        this.activityCount = activityCount;
    }

    public PreviewCard(String placeName, String searchQuery) {
        this.placeName = placeName;
        this.searchQuery = searchQuery;
    }

    public String getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(String activityCount) {
        this.activityCount = activityCount;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }


}

