package com.benockert.numadsp22_quester_final_project.PhotoRecap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.MainActivity;
import com.benockert.numadsp22_quester_final_project.R;
import com.benockert.numadsp22_quester_final_project.UserProfileActivity;
import com.benockert.numadsp22_quester_final_project.createQuest.CreateQuestActivity;
import com.benockert.numadsp22_quester_final_project.myQuests.MyQuestsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class ViewAllRecaps extends AppCompatActivity {
    DatabaseReference dr;
    String username;
    private final ArrayList<RecapCard> recapCardList = new ArrayList<>();
    private ViewAllRecapsAdapter rviewAdapter;
    TextView noRecaps;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_recaps);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        assert currentUser != null;
        username = currentUser.getDisplayName();
        dr = FirebaseDatabase.getInstance().getReference();

        noRecaps = findViewById(R.id.noRecapsLabel);
        noRecaps.setVisibility(View.INVISIBLE);

        dr.child("users").child(username).child("recaps").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().getValue() != null) {
                    createRecyclerView();
                    getAllRecaps();
                } else {
                    noRecaps.setVisibility(View.VISIBLE);
                }
            } else {
                noRecaps.setVisibility(View.VISIBLE);
            }
        });

        BottomNavigationView bNavView = findViewById(R.id.bottomNavigationView);
        bNavView.setSelectedItemId(R.id.nav_recap);

        Context context = this;
        bNavView.setOnItemSelectedListener(item -> {
            Intent i;
            if (item.getItemId() == R.id.nav_home) {
                i = new Intent(context, MyQuestsActivity.class);
                startActivity(i);
            } else if (item.getItemId() == R.id.nav_profile) {
                    i = new Intent(context, UserProfileActivity.class);
                    startActivity(i);
            } else if (item.getItemId() == R.id.nav_createQuest) {
                    i = new Intent(context, CreateQuestActivity.class);
                    startActivity(i);
            }else if (item.getItemId() == R.id.nav_currActivity) {
//                    i = new Intent(context, MainActivity.class);
//                    startActivity(i);
            }
            return false;
        });
        bNavView.setOnItemReselectedListener(item -> {
            Snackbar.make(bNavView.getRootView(), "Already At Location", BaseTransientBottomBar.LENGTH_LONG).show();
        });
    }

    /**
     * Instantiates the recycler view and adapter to display available stickers
     */
    private void createRecyclerView() {
        recapCardList.clear();
        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(this);

        RecyclerView rView = findViewById(R.id.allRecapsRView);
        rView.setHasFixedSize(true);

        rviewAdapter = new ViewAllRecapsAdapter(recapCardList);

        RecapCardListener tempListener = new RecapCardListener() {
            private final Context contxt = getBaseContext();
            RecapCard temp;

            @Override
            public void onCardClick(int pos) {
                temp = recapCardList.get(pos);
                Intent i = new Intent(this.contxt, ViewRecap.class);
                i.putExtra("recapName", temp.getRecapName());
                startActivity(i);
            }

            @Override
            public String getRecapName(int pos) {
                temp = recapCardList.get(pos);
                return temp.getRecapName();
            }

            @Override
            public String getRecapDate(int pos) {
                temp = recapCardList.get(pos);
                return temp.getRecapDate();
            }
        };

        rviewAdapter.setRc_listener(tempListener);
        rView.setAdapter(rviewAdapter);
        rView.setLayoutManager(rLayoutManager);
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

    private void getAllRecaps() {
        dr.child("users").child(username).child("recaps").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try {
                    String json = convertStringToJson(String.valueOf(task.getResult().getValue()));
                    JSONObject recaps = new JSONObject(json);
                    Log.i("recaps", recaps.toString());
                    Iterator<String> recapIterator = recaps.keys();

                    while (recapIterator.hasNext()) {
                        String recapName = recapIterator.next();
                        RecapCard rc = new RecapCard(recapName, recaps.getJSONObject(recapName).getString("dateGenerated"));
                        recapCardList.add(rc);
                        rviewAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}