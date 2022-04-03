package com.benockert.numadsp22_quester_final_project.types;

import java.util.List;

public class Quest {
    public int joinCode;
    public boolean active;
    public String location;
    public float proximity;
    public String photoReference;
    public List<Activity> activities;

    public Quest() {
    }

    public Quest(int joinCode, boolean active, String location, float proximity, String photoReference, List<Activity> activities) {
        this.joinCode = joinCode;
        this.active = active;
        this.location = location;
        this.proximity = proximity;
        this.photoReference = photoReference;
        this.activities = activities;
    }

    public int getJoinCode() {
        return joinCode;
    }

    public boolean isActive() {
        return active;
    }

    public String getLocation() {
        return location;
    }

    public float getProximity() {
        return proximity;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public List<Activity> getActivities() {
        return activities;
    }
}
