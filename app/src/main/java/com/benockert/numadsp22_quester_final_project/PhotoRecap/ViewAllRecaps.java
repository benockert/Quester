package com.benockert.numadsp22_quester_final_project.PhotoRecap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_recaps);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        username = currentUser.getDisplayName();
        dr = FirebaseDatabase.getInstance().getReference();

        createRecyclerView();
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

        rView.setAdapter(rviewAdapter);
        rView.setLayoutManager(rLayoutManager);
    }

    private void getAllRecaps(){
        dr.child("users").child(username).child("recaps").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try {
                    JSONObject recaps = new JSONObject(String.valueOf(task.getResult().getValue()));
                    Iterator<String> recapIterator = recaps.keys();

                    while(recapIterator.hasNext()){
                        String recapName = recapIterator.next();
                        JSONObject recap = new JSONObject(recapName);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}