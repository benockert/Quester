package com.benockert.numadsp22_quester_final_project.myQuests;

import android.content.Intent;
import android.view.View;

import com.benockert.numadsp22_quester_final_project.types.Activity;
import com.benockert.numadsp22_quester_final_project.types.Quest;

import java.util.List;

public class QuestCard {
    private Quest quest;
    private String photoReference;
    private String location;
    private int numUsers;
    private int numActivities;
    private String date;

    public QuestCard() {

    }

    public QuestCard(Quest quest) {
        this.quest = quest;
        this.location = quest.getLocation();
        this.photoReference = quest.getPhotoReference();
        this.numUsers = quest.getUsers().size();
        this.numActivities = quest.getActivities().size();
        this.date = quest.getDate();
    }

    public Quest getQuest() {
        return this.quest;
    }

    public String getLocation() {
        return this.location;
    }

    public String getDate() {
        return this.date;
    }

    public String getPhotoReference() {
        return this.photoReference;
    }

    public int getNumUsers() {
        return this.numUsers;
    }

    public int getNumActivities() {
        return this.numActivities;
    }
}
