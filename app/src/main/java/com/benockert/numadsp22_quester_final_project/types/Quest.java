package com.benockert.numadsp22_quester_final_project.types;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Quest{
    public boolean active;
    public List<Activity> activities;
    public boolean completed;
    public String joinCode;
    public String location;
    public LocalDateTime datetime;
    public String photoReference;
    public float proximity;
    public List<String> users;
    public String name;
    public int currentActivity;

    public Quest(String name
            , String joinCode
            , boolean active
            , boolean completed
            , String location
            , LocalDateTime datetime
            , float proximity
            , String photoReference
            , List<Activity> activities
            , List<String> users
            , int currentActivity) {
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
        this.currentActivity = currentActivity;
    }

    public String getJoinCode() {
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
        return this.users.contains(username);
    }

    public int getCurrentActivity() {
        return this.currentActivity;
    }

    public static Quest getQuestFromJSON(String name, String data) {
        try {
            String joinCode = "0";
            boolean completed = false;
            String location = "N/A";
            float proximity = 0;
            LocalDateTime date = null;
            String photoReference = "N/A";
            List<String> users = new ArrayList<>();
            List<Activity> activities = new ArrayList<>();
            int currentActivity = 0;
            boolean active = false;
            data=data.replace(", ",",").replace(" ","_");

            JSONObject jsonResults = new JSONObject(data);
            Log.i("json", data);
            Iterator<String> activitiesIterator = jsonResults.getJSONObject("activities").keys();

            Iterator<String> usersIterator = jsonResults.getJSONObject("users").keys();

            JSONObject activitiesObj = jsonResults.getJSONObject("activities");
            JSONObject usersObj = jsonResults.getJSONObject("users");

            active = jsonResults.getBoolean("active");
            activities = new ArrayList<>();
            completed = jsonResults.getBoolean("completed");
            joinCode = jsonResults.getString("joinCode");
            location = jsonResults.getString("location").replaceAll("_", " ");;
            date = LocalDateTime.parse(jsonResults.getString("datetime").replace("|", ":"));
            photoReference = jsonResults.getString("photoReference");
            proximity = Float.parseFloat(jsonResults.getString("proximity"));
            users = new ArrayList<>();
            currentActivity = jsonResults.getInt("currentActivity");

            while (usersIterator.hasNext()) {
                users.add(usersIterator.next());
            }

            while (activitiesIterator.hasNext()) {
                String activity = activitiesIterator.next();
                activities.add(Activity.getActivityFromJSON(activitiesObj.get(activity).toString()));
            }

            return new Quest(name, joinCode, active, completed, location, date, proximity, photoReference, activities, users, currentActivity);
        } catch (JSONException e) {
            Log.e("QUEST_PARSER", e.toString());
            e.printStackTrace();
            return null;
        }
    }
}

