package com.benockert.numadsp22_quester_final_project.types;

import java.io.Serializable;
import java.util.List;

public class Quest {
    public boolean active;
    public List<Activity> activities;
    public boolean completed;
    public String location;
    public String datetime;
    public String photoReference;
    public int proximity;
    public List<String> users;
    public int currentActivity;

    public Quest() {
    }

    public Quest(boolean active, boolean completed, String location, String datetime, int proximity, String photoReference, List<Activity> activities, List<String> users, int currentActivity) {
        this.active = active;
        this.completed = completed;
        this.location = location;
        this.datetime = datetime;
        this.proximity = proximity;
        this.photoReference = photoReference;
        this.activities = activities;
        this.users = users;
        this.currentActivity = currentActivity;
    }

    public boolean isActive() {
        return this.active;
    }

    public String getLocation() {
        return this.location;
    }

    public String getDatetime() {
        return this.datetime;
    }

    public int getProximity() {
        return this.proximity;
    }

    public String getPhotoReference() {
        return this.photoReference;
    }

    public List<Activity> getActivities() {
        return this.activities;
    }

    public List<String> getUsers() {
        return this.users;
    }

    public boolean isUserInQuest(String username) {
        return this.users.contains(username);
    }

    public int getCurrentActivity() {
        return this.currentActivity;
    }

}
