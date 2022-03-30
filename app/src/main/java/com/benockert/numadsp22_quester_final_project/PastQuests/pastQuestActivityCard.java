package com.benockert.numadsp22_quester_final_project.PastQuests;

import java.util.ArrayList;

public class pastQuestActivityCard {
    String questName;
    ArrayList<String> participants;
    ArrayList<String> locationsVisited;

    pastQuestActivityCard(String questName, ArrayList<String> participants, ArrayList<String> locationsVisited){
        this.questName = questName;
        this.participants = participants;
        this.locationsVisited = locationsVisited;
    }

    /**
     * Getter for quest name
     * @return String QuestName
     */
    public String getQuestName(){
        return this.questName;
    }

    /**
     * gets the list of participants from a quest
     * @return ArrayList of Strings
     */
    public ArrayList<String> getParticipants(){
        return this.participants;
    }

    /**
     * gets a list of the locations visited during the quest
     * @return ArrayList of Locations represented by Strings
     */
    public ArrayList<String> getLocationsVisited(){
        return this.locationsVisited;
    }
}
