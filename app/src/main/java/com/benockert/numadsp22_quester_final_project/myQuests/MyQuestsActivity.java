package com.benockert.numadsp22_quester_final_project.myQuests;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.PastQuests.PastQuests;
import com.benockert.numadsp22_quester_final_project.R;
import com.benockert.numadsp22_quester_final_project.types.Quest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.GeoApiContext;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class MyQuestsActivity extends AppCompatActivity {
    private DatabaseReference dr;

    private String apiKey;
    private GeoApiContext apiContext;

    private ArrayList<QuestCard> questList = new ArrayList<>();

    private RecyclerViewAdaptor recyclerViewAdapter;

    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_quests);

        //init data from db
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();

        // get the API key for the Places SDK to use
        try {
            ActivityInfo ai = getPackageManager().getActivityInfo(this.getComponentName(), PackageManager.GET_META_DATA);
            Bundle metaData = ai.metaData;
            apiKey = metaData.get("com.google.android.geo.API_KEY").toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e("MY_QUESTS_ACTIVITY", "API_KEY not found in metadata");
        }

        // create the Google Maps Geo API context
        apiContext = new GeoApiContext.Builder().apiKey(apiKey).build();

        //changed how current username is gotten, was: this.getIntent().getExtras().get("currentUser").toString()
        currentUser = Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();

        createRecyclerView();

        getAllQuests();
    }

    private void createRecyclerView() {
        recyclerViewAdapter = new RecyclerViewAdaptor(questList, apiContext);
        RecyclerView.LayoutManager recyclerLayoutManger = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        RecyclerView recyclerView = findViewById(R.id.quest_recycler_view);
        recyclerView.setHasFixedSize(true);

        // Listener to open Quest when card is clicked
        LinkClickListener itemClickListener = (position, view) -> {
            QuestCard questCard = questList.get(position);
//                if (questCard.getQuest().isActive()) {
//                     Intent intent = new Intent(this, ActiveQuests.class);
//                     intent.putExtra("questName", questCard.getQuest().getName());
//                     view.getContext().startActivity(intent);
//                } else {
            //startIntent(questCard);
            Intent intent = new Intent(this, PastQuests.class);
            intent.putExtra("questName", String.valueOf(questCard.getQuest().getDateTime()));
            startActivity(intent);

//            generateJoinCode();
//                }
            recyclerViewAdapter.notifyItemChanged(position);
        };
        recyclerViewAdapter.setOnItemClickListener(itemClickListener);

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(recyclerLayoutManger);
    }

    /**
     * converts the String to a parsable json string
     *
     * @param s String of information gotten from the get request
     * @return String of json info to parse through
     */
    private String convertStingToJson(String s) {
        Scanner sc = new Scanner(s).useDelimiter("\\A");
        return sc.hasNext() ? sc.next().replace(", ", ",\n") : "";
    }

    private void getAllQuests() {
        dr.child("quests").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String result = convertStingToJson(String.valueOf(task.getResult().getValue())).replaceAll(" ", "_");
                Log.i("result", result);

                //loop through quest here and accumulate list
                List<Quest> usersActiveQuests = new ArrayList<>();
                List<Quest> usersInactiveQuests = new ArrayList<>();

                try {
                    JSONObject jsonResults = new JSONObject(result);
                    Iterator<String> questsIterator = jsonResults.keys();
                    while (questsIterator.hasNext()) {
                        String name = questsIterator.next();
                        Quest quest = Quest.getQuestFromJSON(name, jsonResults.getString(name));
                        assert quest != null;
                        if (quest.isUserInQuest(currentUser)) {
                            if (quest.isActive()) {
                                usersActiveQuests.add(quest);
                            } else {
                                usersInactiveQuests.add(quest);
                            }
                        }
                    }

                    usersActiveQuests.sort(Comparator.comparing(Quest::getDateTime).reversed());
                    usersInactiveQuests.sort(Comparator.comparing(Quest::getDateTime).reversed());

                    for (Quest quest : usersActiveQuests) {
                        QuestCard questCard = new QuestCard(quest);
                        Log.d("MY_QUESTS_ACTIVITY", "Active Quest added at location: " + questCard.getLocation());
                        questList.add(questCard);
                    }

                    for (Quest quest : usersInactiveQuests) {
                        QuestCard questCard = new QuestCard(quest);
                        Log.d("MY_QUESTS_ACTIVITY", "Inactive Quest added at location: " + questCard.getLocation());
                        questList.add(questCard);
                    }

                    findViewById(R.id.no_quests_text).setVisibility(View.GONE);
                    recyclerViewAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}