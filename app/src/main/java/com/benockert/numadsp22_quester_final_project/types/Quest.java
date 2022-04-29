package com.benockert.numadsp22_quester_final_project.types;

import java.util.List;
import android.util.Log;

import com.google.firebase.database.Exclude;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;

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

    @Exclude
    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.parse(this.datetime.replace("\\|", ":"));
    }

    @Exclude
    public String getDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        return LocalDateTime.parse(this.datetime.replace("\\|", ":")).format(formatter);
    }

    @Exclude
    public String getTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm:ssa");
        return LocalDateTime.parse(this.datetime.replace("\\|", ":")).format(formatter);
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

    public static Quest getQuestFromJSON(String data) {
        try {
            boolean completed = false;
            String location = "N/A";
            int proximity = 0;
            String date = null;
            String photoReference = "N/A";
            List<String> users = new ArrayList<>();
            List<Activity> activities = new ArrayList<>();
            int currentActivity = 0;
            boolean active = false;
            data=data.replace(", ",",").replace(" ","_");

            JSONObject jsonResults = new JSONObject(data);
            Log.i("json", data);

            JSONArray questActivities = jsonResults.getJSONArray("activities");

            Iterator<String> usersIterator = jsonResults.getJSONObject("users").keys();
            JSONObject usersObj = jsonResults.getJSONObject("users");

            active = jsonResults.getBoolean("active");
            activities = new ArrayList<>();
            completed = jsonResults.getBoolean("completed");
            location = jsonResults.getString("location").replaceAll("_", " ");;
            date = jsonResults.getString("datetime").replace("|", ":");
            photoReference = jsonResults.getString("photoReference");
            proximity = jsonResults.getInt("proximity");
            users = new ArrayList<>();
            currentActivity = jsonResults.getInt("currentActivity");

            while (usersIterator.hasNext()) {
                users.add(usersIterator.next());
            }

            for(int i=0; i<questActivities.length(); i++) {
                activities.add(Activity.getActivityFromJSON(questActivities.getJSONObject(i).toString()));
            }

            return new Quest(active, completed, location, date, proximity, photoReference, activities, users, currentActivity);
        } catch (JSONException e) {
            Log.e("QUEST_PARSER", e.toString());
            e.printStackTrace();
            return null;
        }
    }

}

