package com.benockert.numadsp22_quester_final_project.createQuest.confirmQuestRecycler;

import com.google.maps.model.PriceLevel;

import java.util.Arrays;
import java.util.List;

public class ConfirmActivityCard {
    final PriceLevel[] priceLevels = {PriceLevel.FREE, PriceLevel.INEXPENSIVE, PriceLevel.MODERATE, PriceLevel.EXPENSIVE, PriceLevel.VERY_EXPENSIVE};
    final List<String> priceLevelStrings = Arrays.asList("", "$", "$$", "$$$", "$$$");

    public String placeName;
    public String searchQuery;
    public String photoReference;

    public ConfirmActivityCard() {

    }


}
