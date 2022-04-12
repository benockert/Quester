package com.benockert.numadsp22_quester_final_project.types;

import java.io.Serializable;
import java.util.List;

public class Quest implements Serializable {
    public boolean active;
    public List<Activity> activities;
    public boolean completed;
    public int joinCode;
    public String location;
    public String photoReference;
    public float proximity;
    public List<String> users;

    public Quest() {
    }

    public Quest(int joinCode, boolean active, String location, float proximity, String photoReference, List<Activity> activities, List<String> users) {
        this.joinCode = joinCode;
        this.active = active;
        this.location = location;
        this.proximity = proximity;
        this.photoReference = photoReference;
        this.activities = activities;
        this.users = users;
    }

    public int getJoinCode() {
        return this.joinCode;
    }

    public boolean isActive() {
        return this.active;
    }

    public String getLocation() {
        return this.location;
    }

    public float getProximity() {
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
}
