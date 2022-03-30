package com.benockert.numadsp22_quester_final_project.PastQuests;

import android.graphics.drawable.Drawable;

public class pastQuestActivityCard {
    private final String questName;
    private final String participants;
    private final Drawable locationPhoto;
    final String locationPhotoName;

    pastQuestActivityCard(String questName, String participants,
                          Drawable locationPhoto, String locationPhotoName) {
        this.questName = questName;
        this.participants = participants;
        this.locationPhoto = locationPhoto;
        this.locationPhotoName = locationPhotoName;
    }

    /**
     * getter for questLocation photo name
     *
     * @return String representing the photo name
     */
    public String getLocationPhotoName() {
        return this.locationPhotoName;
    }

    /**
     * Getter for quest name
     *
     * @return String QuestName
     */
    public String getQuestName() {
        return this.questName;
    }

    /**
     * gets the list of participants from a quest
     *
     * @return String holding the name of all participants
     */
    public String getParticipants() {
        return this.participants;
    }

    /**
     * gets a list of the locations visited during the quest
     *
     * @return ArrayList of Locations represented by Strings
     */
    public Drawable getLocationPhoto() {
        return this.locationPhoto;
    }
}
