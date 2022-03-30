package com.benockert.numadsp22_quester_final_project.types;

import java.util.List;

public class Quest {
    public int joinCode;
    public boolean active;
    public String dateTimeGenerated;
    public String location; // need to figure out string vs coordinates
    public float proximity;
    public List<String> activities;

    public Quest() {

    }

    public Quest(int joinCode, boolean active, String dateTimeGenerated, String location, float proximity, List<String> activities) {
        this.joinCode = joinCode;
        this.active = active;
        this.dateTimeGenerated = dateTimeGenerated;
        this.location = location;
        this.proximity = proximity;
        this.activities = activities;
    }

    public int getJoinCode() {
        return joinCode;
    }

    public boolean isActive() {
        return active;
    }

    public String getDateTimeGenerated() {
        return dateTimeGenerated;
    }

    public String getLocation() {
        return location;
    }

    public float getProximity() {
        return proximity;
    }

    public List<String> getActivities() {
        return activities;
    }
}
