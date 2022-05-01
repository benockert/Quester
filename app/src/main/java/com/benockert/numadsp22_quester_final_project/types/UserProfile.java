package com.benockert.numadsp22_quester_final_project.types;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserProfile {
    public static class UserEmail {
        String email;

        public UserEmail(String email) {
            this.email = email;
        }

        public String getEmail() {
            return this.email;
        }
    }

    public static class UserRecap {
        String name;
        String dateGenerated;

        public UserRecap(String name, String dateGenerated) {
            this.name = name;
            this.dateGenerated = dateGenerated;
        }

        public String getName() {
            return this.name;
        }

        public String getDateGenerated() {
            return this.dateGenerated;
        }
    }

    public static List<UserRecap> getRecapsFromJson(String data) {
        List<UserRecap> userRecapsList = new ArrayList<>();
        try {
            JSONObject jsonResults = new JSONObject(data);
            Log.i("json", data);

            try {
                Iterator<String> recapsIterator = jsonResults.getJSONObject("recaps").keys();
                JSONObject recapsObj = jsonResults.getJSONObject("recaps");

                while (recapsIterator.hasNext()) {
                    String nextRecapName = recapsIterator.next();
                    JSONObject recapObj = new JSONObject(recapsObj.get(nextRecapName).toString());
                    userRecapsList.add(new UserRecap(nextRecapName, recapObj.getString("dateGenerated")));
                }
            } catch (Exception e) {
                Log.d("USER_PROFILE", "no recaps for this user");
            }
        } catch (JSONException e) {
            Log.e("QUEST_PARSER", e.toString());
            e.printStackTrace();
            return null;
        }
        return userRecapsList;
    }
}
