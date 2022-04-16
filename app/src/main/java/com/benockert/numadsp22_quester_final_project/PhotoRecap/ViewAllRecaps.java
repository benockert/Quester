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

import java.util.ArrayList;

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

        RecyclerView rView = findViewById(R.id.pastQuestsRecylerView);
        rView.setHasFixedSize(true);

        rviewAdapter = new ViewAllRecapsAdapter(recapCardList);

        rView.setAdapter(rviewAdapter);
        rView.setLayoutManager(rLayoutManager);
    }
}