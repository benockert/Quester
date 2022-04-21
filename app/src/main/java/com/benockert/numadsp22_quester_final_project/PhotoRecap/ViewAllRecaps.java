package com.benockert.numadsp22_quester_final_project.PhotoRecap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ViewAllRecaps extends AppCompatActivity {
    DatabaseReference dr;
    String username;
    private final ArrayList<RecapCard> recapCardList = new ArrayList<>();
    private ViewAllRecapsAdapter rviewAdapter;
    TextView noRecaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_recaps);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
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
                i.putExtra("recapName", temp.getRecapName(pos));
                startActivity(i);
            }

            @Override
            public String getRecapName(int pos) {
                temp = recapCardList.get(pos);
                return temp.getRecapName(pos);
            }

            @Override
            public String getRecapDate(int pos) {
                temp = recapCardList.get(pos);
                return temp.getRecapDate(pos);
            }
        };

        rviewAdapter.setRc_listener(tempListener);
        rView.setAdapter(rviewAdapter);
        rView.setLayoutManager(rLayoutManager);
    }

    private void getAllRecaps() {
        dr.child("users").child(username).child("recaps").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try {
                    JSONObject recaps = new JSONObject(String.valueOf(task.getResult().getValue()));
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