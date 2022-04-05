package com.benockert.numadsp22_quester_final_project.createQuest.recycler;

import android.content.Context;

public class AddActivityCard {

    public String searchQuery;
    public int priceLevel;
    public int popularity;

    public AddActivityCard(String searchQuery, int priceLevel, int popularity) {
        this.searchQuery = searchQuery;
        this.priceLevel = priceLevel;
        this.popularity = popularity;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public int getPopularity() {
        return popularity;
    }
}
