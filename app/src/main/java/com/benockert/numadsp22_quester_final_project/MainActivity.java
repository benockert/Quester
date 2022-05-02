package com.benockert.numadsp22_quester_final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.benockert.numadsp22_quester_final_project.activeQuest.ActiveQuest;
import com.benockert.numadsp22_quester_final_project.createQuest.CreateQuestActivity;
import com.benockert.numadsp22_quester_final_project.myQuests.MyQuestsActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.my_quests:
                intent = new Intent(this, MyQuestsActivity.class);
                startActivity(intent);
                break;
            case R.id.user_profile:
                intent = new Intent(this, UserProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.openActiveQuest:
                intent = new Intent(this, ActiveQuest.class);
                intent.putExtra("joinCode", "q9754c");
                startActivity(intent);
                break;
            case R.id.create_quest:
                intent = new Intent(this, CreateQuestActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                break;

        }
    }
}