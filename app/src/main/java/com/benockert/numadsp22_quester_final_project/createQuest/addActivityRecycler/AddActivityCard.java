package com.benockert.numadsp22_quester_final_project.createQuest.addActivityRecycler;

import com.google.android.material.slider.Slider;
import com.google.maps.model.PriceLevel;

import java.util.Arrays;
import java.util.List;

public class AddActivityCard {
    final PriceLevel[] priceLevels = {PriceLevel.FREE, PriceLevel.INEXPENSIVE, PriceLevel.MODERATE, PriceLevel.EXPENSIVE, PriceLevel.VERY_EXPENSIVE};
    final List<String> priceLevelStrings = Arrays.asList("", "$", "$$", "$$$", "$$$");

    public String searchQuery;
    public int priceLevel;
    public int popularity;
    public boolean isCollapsed;

    // default constructor for creating a new blank card
    public AddActivityCard() {
        this.searchQuery = "";
        this.priceLevel = 2;
        this.popularity = 2;
        this.isCollapsed = false;
    }

    public AddActivityCard(String searchQuery, int priceLevel, float popularity, boolean isCollapsed) {
        this.searchQuery = searchQuery;
        this.priceLevel = priceLevel;
        this.popularity = Math.round(popularity);
        this.isCollapsed = isCollapsed;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public String getPriceLevelString() {
        return priceLevelStrings.get(priceLevel);
    }

    public double getPopularity() {
        return popularity;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void setPriceLevel(int priceLevel) {
        this.priceLevel = priceLevel;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public void setIsCollapsed(boolean isCollapsed) {
        this.isCollapsed = isCollapsed;
    }

}
