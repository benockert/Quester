package com.benockert.numadsp22_quester_final_project.types;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Quest implements Serializable {
    public boolean active;
    public List<Activity> activities;
    public boolean completed;
    public int joinCode;
    public String location;
    public LocalDateTime datetime;
    public String photoReference;
    public float proximity;
    public List<String> users;
    public String name;

    public Quest(String name, int joinCode, boolean active, boolean completed, String location, LocalDateTime datetime, float proximity, String photoReference, List<Activity> activities, List<String> users) {
        this.joinCode = joinCode;
        this.active = active;
        this.completed = completed;
        this.location = location;
        this.datetime = datetime;
        this.proximity = proximity;
        this.photoReference = photoReference;
        this.activities = activities;
        this.users = users;
        this.name = name;
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

    public LocalDateTime getDateTime() {
        return this.datetime;
    }

    public String getDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        return this.datetime.format(formatter);
    }

    public String getName(){
        return this.name;
    }

    public String getTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm:ssa");
        return this.datetime.format(formatter);
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

    public boolean isUserInQuest(String username) {
        return this.users.contains(username) || this.users.contains("_" + username);
    }

    public static Quest getQuestFromJSON(String name, String data) {
        try {
            int joinCode = 0;
            boolean completed = false;
            String location = "N/A";
            float proximity = 0;
            LocalDateTime date = null;
            String photoReference = "N/A";
            List<String> users = new ArrayList<>();
            List<Activity> activities = new ArrayList<>();
            boolean active = false;

            JSONObject jsonResults = new JSONObject(data);
            Log.i("json", data);
            Iterator<String> activitiesIterator = jsonResults.getJSONObject("_activities").keys();

            Iterator<String> usersIterator = jsonResults.getJSONObject("_users").keys();

            JSONObject activitiesObj = jsonResults.getJSONObject("_activities");
            JSONObject usersObj = jsonResults.getJSONObject("_users");

            active = jsonResults.getBoolean("_active");
            activities = new ArrayList<>();
            completed = jsonResults.getBoolean("_completed");
            joinCode = jsonResults.getInt("_joinCode");
            location = jsonResults.getString("_location").replaceAll("_", " ");;
            date = LocalDateTime.parse(jsonResults.getString("datetime").replace("|", ":"));
            photoReference = jsonResults.getString("_photoReference");
            proximity = Float.parseFloat(jsonResults.getString("_proximity"));
            users = new ArrayList<>();

            while (usersIterator.hasNext()) {
                users.add(usersIterator.next());
            }

            while (activitiesIterator.hasNext()) {
                String activity = activitiesIterator.next();
                activities.add(Activity.getActivityFromJSON(activitiesObj.get(activity).toString()));
            }
            return new Quest(name, joinCode, active, completed, location, date, proximity, photoReference, activities, users);
        } catch (JSONException e) {
            Log.e("QUEST_PARSER", e.toString());
            e.printStackTrace();
            return null;
        }
    }
}
