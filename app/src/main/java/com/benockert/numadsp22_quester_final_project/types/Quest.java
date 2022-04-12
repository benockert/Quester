package com.benockert.numadsp22_quester_final_project.types;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Quest implements Serializable {
    public boolean active;
    public List<Activity> activities;
    public boolean completed;
    public int joinCode;
    public String location;
    public String date;
    public String photoReference;
    public float proximity;
    public List<String> users;

    public Quest() {
    }

    public Quest(int joinCode, boolean active, String location, String date, float proximity, String photoReference, List<Activity> activities, List<String> users) {
        this.joinCode = joinCode;
        this.active = active;
        this.location = location;
        this.date = date;
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

    public String getDate() {
        return this.date;
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

    public static Quest getQuestFromJSON(String data) {
        try {
            JSONObject jsonResults = new JSONObject(data);

            Iterator<String> activitiesIterator = jsonResults.getJSONObject("activities").keys();
            Iterator<String> usersIterator = jsonResults.getJSONObject("users").keys();

            JSONObject activitiesObj = jsonResults.getJSONObject("activities");
            JSONObject usersObj = jsonResults.getJSONObject("users");

            boolean active = jsonResults.getBoolean("active");
            List<Activity> activities = new ArrayList<>();
            boolean completed = jsonResults.getBoolean("completed");
            int joinCode = jsonResults.getInt("joinCode");
            String location = jsonResults.getString("location");
            String date = jsonResults.getString("date");
            String photoReference = jsonResults.getString("photoReference");
            float proximity = Float.parseFloat(jsonResults.getString("proximity"));
            List<String> users = new ArrayList<>();

            while (usersIterator.hasNext()) {
                users.add(usersIterator.next());
            }

            while (activitiesIterator.hasNext()) {
                String activity = activitiesIterator.next();
                activities.add(Activity.getActivityFromJSON(activitiesObj.get(activity).toString()));
            }

            return new Quest(joinCode, active, location, date, proximity, photoReference, activities, users);

        } catch (JSONException e) {
            Log.e("QUEST_PARSER", "JSONException");
            e.printStackTrace();
            return null;
        }
    }
}
