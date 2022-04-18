package com.benockert.numadsp22_quester_final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.benockert.numadsp22_quester_final_project.myQuests.MyQuestsActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_quests:
                System.out.println("in onclick");
                Intent intent = new Intent(this, MyQuestsActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void logOut(View v){
        FirebaseAuth.getInstance().signOut();
        finish();
    }
}