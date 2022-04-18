package com.benockert.numadsp22_quester_final_project.PhotoRecap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.benockert.numadsp22_quester_final_project.R;

public class ViewRecap extends AppCompatActivity {
    String questRecapName;
    TextView recapName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recap);
        questRecapName = this.getIntent().getExtras().getString("recapName");
        recapName = findViewById(R.id.viewRecapRName);
        recapName.setText(questRecapName);
        Log.i("inViewRecap", questRecapName);
    }

    public void backToViewAll(View v){
        finish();
    }
}