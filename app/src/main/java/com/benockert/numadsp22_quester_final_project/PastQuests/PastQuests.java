package com.benockert.numadsp22_quester_final_project.PastQuests;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.R;
import com.benockert.numadsp22_quester_final_project.types.Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class PastQuests extends AppCompatActivity {
    private DatabaseReference dr;
    private String questName;
    private final ArrayList<Activity> pastQuestActivities = new ArrayList<>();
    private static Context context;
    private pastQuestActivityCardAdapter rviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_quests);

        findViewById(R.id.createQRecap).setVisibility(View.INVISIBLE);
        findViewById(R.id.viewQRecap).setVisibility(View.INVISIBLE);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        dr = FirebaseDatabase.getInstance().getReference();

        questName = this.getIntent().getExtras().get("questName").toString();

        String qRecap = questName + "_recap";
        Log.i("name", qRecap);
        if(currentUser != null) {

            String username = currentUser.getDisplayName();

            if(username != null){
                dr.child("users").child(username).child("recaps")
                        .child(qRecap).child("generated").get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.i(TAG, String.valueOf(task.getResult().getValue()));
                                if(task.getResult().getValue()!=null){
                                    if (task.getResult().getValue().toString().equals("true")) {
                                        findViewById(R.id.viewQRecap).setVisibility(View.VISIBLE);
                                    }
                                    else{
                                        findViewById(R.id.createQRecap).setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        });
            }
        }

        context = this;
        TextView qName = findViewById(R.id.pastQuest_questName);
        qName.setText(this.getIntent().getExtras().get("questName").toString());

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
                        participants.append(",  ");
                    }
                    String finalParticipants = participants.substring(0, participants.length() - 3);
                    particpantV.setText(finalParticipants);
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
//        String apiKey = "";
//        try {
//            ActivityInfo ai = getPackageManager().getActivityInfo(this.getComponentName(), PackageManager.GET_META_DATA);
//            Bundle metaData = ai.metaData;
////            apiKey = metaData.get("com.google.android.geo.API_KEY").toString();
//        } catch (PackageManager.NameNotFoundException | RuntimeException e) {
//            e.printStackTrace();
//            Log.e("TAG", "API_KEY not found in metadata");
//        }

        dr.child("quests").child(questName).child("activities").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Object> map = (Map<String, Object>) task.getResult().getValue();
                try {
                    for (Map.Entry<String, Object> result : map.entrySet()) {
                        JSONObject currActivity = new JSONObject(result.getValue().toString()
                        .replaceAll(" g", "g")
                        .replaceAll(" u", "u")
                        .replaceAll(" ", "_"));

                        String actName = currActivity.getString("gName")
                                .replaceAll("_", " ");
                        String address = currActivity.getString("gFormattedAddress")
                                .replaceAll("_", " ");
                        Activity temp = new Activity(actName,
                                currActivity.getInt("gPriceLevel"),
                                currActivity.getString("gPhotoReference"), address);
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

    public static void openInMaps(String address) {
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q=" + address));
        i.setPackage("com.google.android.apps.maps");
        context.startActivity(i);
    }

    public void viewQuestRecap(View v) {
        Intent i = new Intent();
        i.putExtra("questName", questName);
        startActivity(i);
    }

    public void createQuestRecap(View v){
        Intent i = new Intent();
        i.putExtra("questName", questName);
        startActivity(i);
    }

    public void toCreateQuest(View v) {
        Intent i = new Intent();
        startActivity(i);
    }
}