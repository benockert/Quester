package com.benockert.numadsp22_quester_final_project.myQuests;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.benockert.numadsp22_quester_final_project.R;
import com.benockert.numadsp22_quester_final_project.types.Quest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.GeoApiContext;

import java.util.ArrayList;
import java.util.Collections;

public class MyQuestsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference dr;

    private String apiKey;
    private GeoApiContext apiContext;

    private ArrayList<QuestCard> questList = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerViewAdaptor recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManger;

    private String currentUser;

    private static final String KEY_OF_INSTANCE = "KEY_OF_INSTANCE";
    private static final String NUMBER_OF_ITEMS = "NUMBER_OF_ITEMS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_quests);

        //init data from db
        mAuth = FirebaseAuth.getInstance();
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

        currentUser = this.getIntent().getExtras().get("currentUser").toString();

        createRecyclerView();

        initLinkData(savedInstanceState);

        getAllQuests();
    }

    private void initLinkData(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_OF_ITEMS)) {
            if (questList == null || questList.size() == 0) {
                int size = savedInstanceState.getInt(NUMBER_OF_ITEMS);

                // Retrieve keys we stored in the instance
                for (int i = 0; i < size; i++) {
                    Quest quest = (Quest) savedInstanceState.getSerializable(KEY_OF_INSTANCE + i);

                    QuestCard questCard = new QuestCard(quest);

                    questList.add(questCard);
                }
            }
        }
    }

    private void createRecyclerView() {
        recyclerViewAdapter = new RecyclerViewAdaptor(questList, apiContext);
        recyclerLayoutManger = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView = findViewById(R.id.quest_recycler_view);
        recyclerView.setHasFixedSize(true);

        // Listener to open Quest when card is clicked
        LinkClickListener itemClickListener = new LinkClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                questList.get(position).onItemClick(position, view);
                recyclerViewAdapter.notifyItemChanged(position);
            }
        };
        recyclerViewAdapter.setOnItemClickListener(itemClickListener);

        Collections.sort(questList, new QuestComparator());

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(recyclerLayoutManger);
    }

    //todo only find quests this user is a part of and add those to list
    private void getAllQuests() {
        dr.child("quests").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String result = String.valueOf(task.getResult().getValue());
                Quest quest = Quest.getQuestFromJSON(result);
                QuestCard questCard = new QuestCard(quest);
                Log.d("MY_QUESTS_ACTIVITY", "Quest added at location: " + questCard.getLocation());

                questList.add(questCard);
                findViewById(R.id.no_quests_text).setVisibility(View.GONE);
                Collections.sort(questList, new QuestComparator());
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    // Handling Orientation Changes
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        int size = questList == null ? 0 : questList.size();
        outState.putInt(NUMBER_OF_ITEMS, size);

        for (int i = 0; i < size; i++) {
            outState.putSerializable(KEY_OF_INSTANCE + i, questList.get(i).getQuest());
        }
        super.onSaveInstanceState(outState);
    }
}