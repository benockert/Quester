package com.benockert.numadsp22_quester_final_project.createQuest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.benockert.numadsp22_quester_final_project.R;
import com.benockert.numadsp22_quester_final_project.types.Activity;

import java.util.ArrayList;

public class ConfirmQuestActivity extends AppCompatActivity {
    private String TAG = "LOG_QUESTER_CONFIRM_QUEST_ACTIVITY";

    private ArrayList<Activity> activites = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_quest);

        ArrayList<Activity> activities = getIntent().getParcelableArrayListExtra(CreateQuestActivity.CONFIRM_ACTIVITY_INTENT_MESSAGE);

        Activity a = activities.get(0);
        Log.v(TAG, "Activity name in new intent: " + a.gName);
        activites.add(a);

    }
}