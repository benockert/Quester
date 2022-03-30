package com.benockert.numadsp22_quester_final_project.PastQuests;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.benockert.numadsp22_quester_final_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PastQuests extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference dr;
    private String username;
    private ArrayList<pastQuestActivityCard> pastQuestActivities = new ArrayList<>();

    private String sendToUserName;

    private RecyclerView recyclerView;
    private pastQuestActivityCardAdapter rviewAdapter;
    private RecyclerView.LayoutManager rLayoutManager;
    private static final String TAG = "SendStickerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_quests);
        mAuth = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            username = user.getDisplayName();
        }

        TextView qName = findViewById(R.id.pastQuest_questName);
        qName.setText(this.getIntent().getExtras().get("questName").toString());

        createPastActivitiesList();
        getAllActivities();
    }

    private void createPastActivitiesList(){

    }

    private void getAllActivities(){

    }
}