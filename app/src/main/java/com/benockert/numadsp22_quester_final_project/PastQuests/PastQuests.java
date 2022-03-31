package com.benockert.numadsp22_quester_final_project.PastQuests;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class PastQuests extends AppCompatActivity {
    private DatabaseReference dr;
    private String questName;
    private final ArrayList<pastQuestActivityCard> pastQuestActivities = new ArrayList<>();


    private pastQuestActivityCardAdapter rviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_quests);
        dr = FirebaseDatabase.getInstance().getReference();

        TextView qName = findViewById(R.id.pastQuest_questName);
        qName.setText(this.getIntent().getExtras().get("questName").toString());
        questName = this.getIntent().getExtras().get("questName").toString();
        createRecyclerView();
        getAndPlaceAllParticipants();
        getAllActivities();
    }

    /**
     * retrieves all participants of the quest and displays them to the user
     */
    private void getAndPlaceAllParticipants() {
        TextView particpantV = findViewById(R.id.participants);
        dr.child("quests").child(questName).child("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try {
                    String results = String.valueOf(task.getResult().getValue());
                    JSONObject jsonResults = new JSONObject(results);

                    Iterator<String> objs = jsonResults.keys();
                    StringBuilder participants = new StringBuilder();
                    while (objs.hasNext()) {
                        participants.append(objs.next());
                        participants.append(" ");
                    }
                    particpantV.setText(participants.toString());
                } catch (Exception e) {
                    Log.i("exception", e.toString());
                }
            }
        });
    }

    /**
     * Instantiates the recycler view and adapter to display available stickers
     */
    private void createRecyclerView() {
        pastQuestActivities.clear();
        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(this);

        RecyclerView rView = findViewById(R.id.pastQuestsRecylerView);
        rView.setHasFixedSize(true);

        rviewAdapter = new pastQuestActivityCardAdapter(pastQuestActivities);

        rView.setAdapter(rviewAdapter);
        rView.setLayoutManager(rLayoutManager);
    }

    private void getAllActivities() {
        String apiKey = "";
        try {
            ActivityInfo ai = getPackageManager().getActivityInfo(this.getComponentName(), PackageManager.GET_META_DATA);
            Bundle metaData = ai.metaData;
            apiKey = metaData.get("com.google.android.geo.API_KEY").toString();
        } catch (PackageManager.NameNotFoundException | RuntimeException e) {
            e.printStackTrace();
            Log.e("TAG", "API_KEY not found in metadata");
        }

        String finalApiKey = apiKey;

        dr.child("quests").child(questName).child("activities").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Object> map = (Map<String, Object>) task.getResult().getValue();
                try {
                    for (Map.Entry<String, Object> result : map.entrySet()) {
                        JSONObject currActivity = new JSONObject(result.getValue().toString()
                                .replaceAll(" u", "u")
                                .replaceAll(" g", "g")
                                .replaceAll(" ", "_"));
                        Log.i("json", currActivity.toString());

                        int prange = currActivity.getInt("gPriceLevel");
                        String actName = currActivity.getString("gName")
                                .replaceAll("_", " ");
                        String photoRef = currActivity.getString("gPhotoReference");
                        pastQuestActivityCard temp = new pastQuestActivityCard(actName, prange,
                                photoRef, finalApiKey);
                        pastQuestActivities.add(temp);
                        rviewAdapter.notifyItemInserted(0);
                    }
                } catch (Exception e) {
                    Log.e("ERROR1", e.toString());
                    e.printStackTrace();
                }
            } else {
                Log.e("ERROR2", task.getResult().toString());
            }
        });
    }
}