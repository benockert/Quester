package com.benockert.numadsp22_quester_final_project.PastQuests;

public class pastQuestActivityCard {
    private final String locationName;
    private final int price_range;
    private final String locationPhotoRef;

    pastQuestActivityCard(String locationName, int price_range, String locationPhotoRef) {
        this.locationName = locationName;
        this.price_range = price_range;
        this.locationPhotoRef = locationPhotoRef;
    }

    /**
     * getter for the price range of the activity
     *
     * @return String representing the number of $signs the price range is
     */
    public int getPrice_range() {
        return this.price_range;
    }

    /**
     * getter for the name of the activity location
     *
     * @return String representing the name of the location
     */
    public String getLocationName() {
        return this.locationName;
    }


    /**
     * getter for the String referencing the photo of the activity location
     *
     * @return String representing a reference to a photo of the activity location
     */
    public String getLocationPhotoRef() {
        return this.locationPhotoRef;
    }
}
