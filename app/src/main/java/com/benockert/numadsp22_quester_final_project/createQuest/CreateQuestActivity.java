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
import com.google.maps.GeoApiContext;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.PriceLevel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreateQuestActivity extends AppCompatActivity {
    private String TAG = "LOG_QUESTER_CREATE_QUEST_ACTIVITY";

    final HashMap<String, PriceLevel> priceLevels = setPriceLevels();

    private String apiKey;
    private GeoApiContext apiContext;

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

        // create the Google Maps Geo API context
        apiContext = new GeoApiContext.Builder().apiKey(apiKey).build();
    }

    public void sendSearch(View v) {
        GooglePlacesClient client = new GooglePlacesClient(apiContext);

        String query = searchQueryInput.getText().toString();
        LatLng location = new LatLng(42.33564, 71.08369); // todo user location
        int radius = 2000;
        PriceLevel priceLevel = priceLevels.get("$$");

        PlacesSearchResult[] places = client.textSearch(query, location, radius, priceLevel);
        if (places != null) {
            // TODO do shit here
            responseTextView.setText(places[0].name);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        apiContext.shutdown();
    }

    private HashMap<String, PriceLevel> setPriceLevels() {
        HashMap<String, PriceLevel> priceLevels = new HashMap<>();
        priceLevels.put("$", PriceLevel.INEXPENSIVE);
        priceLevels.put("$$", PriceLevel.MODERATE);
        priceLevels.put("$$$", PriceLevel.EXPENSIVE);
        priceLevels.put("$$$$", PriceLevel.VERY_EXPENSIVE);
        return priceLevels;
    }
}