package com.benockert.numadsp22_quester_final_project.createQuest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.benockert.numadsp22_quester_final_project.R;
import com.benockert.numadsp22_quester_final_project.utils.GooglePlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class CreateQuestActivity extends AppCompatActivity {
    private String TAG = "LOG_QUESTER_CREATE_QUEST_ACTIVITY";

    private String apiKey = null;

    public TextView searchQueryInput;
    public Button submitSearchQueryButton;
    public TextView responseTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quest);

        searchQueryInput = findViewById(R.id.placeSearchQuery);
        submitSearchQueryButton = findViewById(R.id.submitSearchButton);
        responseTextView = findViewById(R.id.searchResponseView);

        // get the API key for the Places SDK to use
        try {
            ActivityInfo ai = getPackageManager().getActivityInfo(this.getComponentName(), PackageManager.GET_META_DATA);
            Bundle metaData = ai.metaData;
            apiKey = metaData.get("com.google.android.geo.API_KEY").toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "API_KEY not found in metadata");
        }
    }

    public void sendSearch(View v) {
        GooglePlacesClient client = new GooglePlacesClient(apiKey);

        HashMap<String, String> params = new HashMap<>();
        params.put("query", searchQueryInput.getText().toString());
        params.put("location", "42.33564,71.08369");
        params.put("radius", "2000");

        try {
            JSONArray response = client.textSearch(params);
            JSONObject place = response.getJSONObject(0);
            String placeName = place.get("name").toString();
            responseTextView.setText(placeName);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error sending text search request");
        }
    }
}