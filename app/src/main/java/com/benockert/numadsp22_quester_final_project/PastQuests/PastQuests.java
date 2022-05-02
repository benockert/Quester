package com.benockert.numadsp22_quester_final_project.PastQuests;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.PhotoRecap.ViewRecap;
import com.benockert.numadsp22_quester_final_project.R;
import com.benockert.numadsp22_quester_final_project.CreateRecap.ChooseTemplate;
import com.benockert.numadsp22_quester_final_project.types.Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class PastQuests extends AppCompatActivity {
    private DatabaseReference dr;
    private String questId;
    private String qRecap;
    private final ArrayList<Activity> pastQuestActivities = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
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

        String questName = this.getIntent().getExtras().get("questName").toString();
        Log.i("questName", questName);
        questId = this.getIntent().getExtras().get("questId").toString();
        qRecap = "q" + questName + "_recap";
        Log.i("questRecap", qRecap);
        Log.i("id", questId);
        if (currentUser != null) {

            String username = currentUser.getDisplayName();

            //determining whether create quest recap or view quest recap is displayed
            if (username != null) {
                dr.child("users").child(username).child("recaps")
                        .child(questName).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().getValue() == null) {
                            findViewById(R.id.createQRecap).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.viewQRecap).setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }

        Log.i("questName", questName);
        context = this;
        TextView qName = findViewById(R.id.pastQuest_questName);
        qName.setText(questName);

        createRecyclerView();
        getAndPlaceAllParticipants();
        getAllActivities();
    }

    /**
     * retrieves all participants of the quest and displays them to the user
     */
    private void getAndPlaceAllParticipants() {
        TextView particpantV = findViewById(R.id.participants);
        //query to retrieve quest participants
        dr.child("quests").child(questId).child("users").get().addOnCompleteListener(task -> {
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
                    //if the query fails for some reason, log the exception
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

    /**
     * gets all the activities for the current quest and adds them to a list of activities
     */
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

        dr.child("quests").child(questId).child("activities").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try {
                    String json = convertStringToJson(String.valueOf(task.getResult().getValue())).replaceAll(" ", "_");

                    JSONArray arr = new JSONArray(json);

                    for (int x = 0; x < arr.length(); x++) {
                        JSONObject currActivity = new JSONObject(convertStringToJson(arr.get(x).toString()));
                        String activityName;
                        String address;
                        int price;
                        String photoRef;

                        try {
                            activityName = currActivity.getString("gName")
                                    .replaceAll("_", " ");
                        } catch (JSONException e) {
                            activityName = "N/A";
                        }
                        try {
                            address = currActivity.getString("gFormattedAddress")
                                    .replaceAll("_", " ");
                        } catch (JSONException e) {
                            address = "N/A";
                        }
                        try {
                            price = currActivity.getInt("gPriceLevel");
                        } catch (JSONException e) {
                            price = 0;
                        }
                        try {
                            photoRef = currActivity.getString("gPhotoReference");
                        } catch (JSONException e) {
                            photoRef = "N/A";
                        }

                        Activity temp = new Activity(activityName, price, photoRef, address);
                        pastQuestActivities.add(temp);
                        rviewAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    Log.e("ERROR1", e.toString());
                }
            } else {
                Log.e("ERROR2", task.getResult().toString());
            }
        });
    }

    /**
     * when the maps image on an activity card is pressed, maps will open
     *
     * @param address String representing the address associated with the activity card
     */
    public static void openInMaps(String address) {
        Uri uri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        i.setPackage("com.google.android.apps.maps");
        context.startActivity(i);
    }

    /**
     * if a quest recap is generated, when clicked this method will allow users to view all of their
     * quest recaps
     *
     * @param v represents the current view the user sees
     */
    public void viewQuestRecap(View v) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
        String userName = mAuth.getCurrentUser().getDisplayName();
        Context context = this;
        dr.child("users").child(userName).child("id").get().addOnCompleteListener(
                new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getValue() != null) {
                                String tempid = String.valueOf(task.getResult().getValue());
                                Intent i = new Intent(context, ViewRecap.class);
                                i.putExtra("recapName", qRecap);
                                i.putExtra("userId", tempid);
                                startActivity(i);
                            }
                        }
                    }
                });
    }

    /**
     * if a quest recap was not generated, when clicked this method will bring the user to a page
     * to generate a recap
     *
     * @param v represents the current view the user sees
     */
    public void createQuestRecap(View v) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
        String userName = mAuth.getCurrentUser().getDisplayName();
        Context context = this;
        dr.child("users").child(userName).child("id").get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().getValue() != null) {
                            String tempid = String.valueOf(task.getResult().getValue());
                            Intent i = new Intent(context, ChooseTemplate.class);
                            i.putExtra("recapName", qRecap);
                            i.putExtra("userId", tempid);
                            startActivity(i);
                        }
                    }
                });
    }

    /**
     * converts the String to a parsable json string
     *
     * @param s String of information gotten from the get request
     * @return String of json info to parse through
     */
    private String convertStringToJson(String s) {
        Scanner sc = new Scanner(s).useDelimiter("\\A");
        return sc.hasNext() ? sc.next().replace(", ", ",\n") : "";
    }
}