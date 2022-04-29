package com.benockert.numadsp22_quester_final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.benockert.numadsp22_quester_final_project.activeQuest.ActiveQuest;
import com.benockert.numadsp22_quester_final_project.myQuests.MyQuestsActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private TextView dateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateString = findViewById(R.id.datestring);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_quests:
                System.out.println("in onclick");
                Intent intent = new Intent(this, MyQuestsActivity.class);
                startActivity(intent);
                break;
            case R.id.openActiveQuest:
                intent = new Intent(this, ActiveQuest.class);
                intent.putExtra("joinCode", "62657d");
                startActivity(intent);
                break;
            case R.id.refresh:
//                refresh();
                break;

        }
    }

    public void logOut(View v){
        FirebaseAuth.getInstance().signOut();
        finish();
    }
}