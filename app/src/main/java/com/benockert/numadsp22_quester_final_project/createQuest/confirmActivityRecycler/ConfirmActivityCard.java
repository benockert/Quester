package com.benockert.numadsp22_quester_final_project.createQuest.confirmActivityRecycler;

import android.view.View;

public class ConfirmActivityCard implements RegenerateButtonClickListener {

    public String placeName;
    public String placeAddress;
    public String searchQuery;
    public String photoReference;

    public ConfirmActivityCard(String placeName, String placeAddress, String searchQuery, String photoReference) {
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.searchQuery = searchQuery;
        this.photoReference = photoReference;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    @Override
    public void onClick(View v, int position) {

    }
}
