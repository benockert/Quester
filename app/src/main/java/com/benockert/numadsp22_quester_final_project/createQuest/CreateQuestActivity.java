package com.benockert.numadsp22_quester_final_project.createQuest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.benockert.numadsp22_quester_final_project.R;
import com.benockert.numadsp22_quester_final_project.createQuest.addActivityRecycler.AddActivityCard;
import com.benockert.numadsp22_quester_final_project.createQuest.addActivityRecycler.AddActivityCardAdapter;
import com.benockert.numadsp22_quester_final_project.types.Activity;
import com.benockert.numadsp22_quester_final_project.utils.GooglePlacesClient;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.google.maps.GeoApiContext;
import com.google.maps.model.LatLng;
import com.google.maps.model.PriceLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CreateQuestActivity extends AppCompatActivity {
    private String TAG = "LOG_QUESTER_CREATE_QUEST_ACTIVITY";
    private int LOCATION_PERMISSION_ID = 1;
    private int METERS_IN_ONE_MILE = 1609;

    private LocationManager locationManager;
    private String apiKey;
    private GeoApiContext apiContext;
    private GooglePlacesClient placesClient;

    private Context context;
    private LatLng mLocation;

    public TextView startLocationTextView;
    public Button useCurrentLocationButton;
    public Slider proximitySlider;
    public Button addActivityButton;
    public Button generateQuestButton;
    public TextView addFirstActivityTextView;

    public ArrayList<AddActivityCard> activityCards;
    private RecyclerView recyclerView;
    private AddActivityCardAdapter activityCardAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private MaterialCardView progressCardView;
    private TextView progressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quest);
        context = getApplicationContext();

        activityCards = new ArrayList<>();
        createRecyclerView();

        startLocationTextView = findViewById(R.id.questLocationTextInput);
        useCurrentLocationButton = findViewById(R.id.useCurrentLocationButton);
        proximitySlider = findViewById(R.id.proximitySlider);
        addActivityButton = findViewById(R.id.addActivityButton);
        generateQuestButton = findViewById(R.id.generateQuestButton);
        addFirstActivityTextView = findViewById(R.id.noActivityMessageText);
        progressCardView = findViewById(R.id.progressPopup);
        progressTextView = findViewById(R.id.progressText);

        proximitySlider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.US, "%.2f", value) + " mi";
            }
        });

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

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(TAG, "SAVING INSTANCE STATE?");
    }

    private void createRecyclerView() {
        recyclerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView = findViewById(R.id.activityCardRecyclerView);
        recyclerView.setHasFixedSize(true);
        activityCardAdapter = new AddActivityCardAdapter(activityCards, context);
        recyclerView.setAdapter(activityCardAdapter);
        recyclerView.setLayoutManager(recyclerLayoutManager);
    }

    public void addNewActivity(View v) {
        AddActivityCard newCard = new AddActivityCard();
        activityCards.add(newCard);

        if (activityCards.size() > 0) {
            addFirstActivityTextView.setVisibility(View.GONE);
        }
        activityCardAdapter.notifyItemInserted(activityCards.indexOf(newCard));

        // collapse all other cards except last, retain state
        for (int i=0; i < activityCards.size() - 1; i++) {
            activityCards.get(i).setIsCollapsed(true);
            activityCardAdapter.notifyItemChanged(i);
        }
    }

    public void onGenerateQuestButtonClick(View v) {
        Log.d(TAG, "Generate quest button clicked");
        CharSequence locationString = startLocationTextView.getText();
        int radiusInMeters = Math.round(proximitySlider.getValue() * METERS_IN_ONE_MILE);
        if (locationString != "") {
            placesClient = new GooglePlacesClient(apiContext, radiusInMeters, locationString.toString());
        } else if (mLocation != null) {
            placesClient = new GooglePlacesClient(apiContext, radiusInMeters, mLocation);
        }

        // start loading icon
        progressCardView.setVisibility(View.VISIBLE);
        GenerateQuest task = new GenerateQuest();
        task.execute(activityCards);
    }

    @SuppressLint("StaticFieldLeak")
    private class GenerateQuest extends AsyncTask<ArrayList<AddActivityCard>, String, List<Activity>> {

        @Override
        protected List<Activity> doInBackground(ArrayList<AddActivityCard>... addActivityCards) {
            Log.d(TAG, "In doInBackground");
            List<Activity> questActivities = new ArrayList<>();
            for (AddActivityCard activity : addActivityCards[0]) {
                try {
                    publishProgress(activity.searchQuery);
                    Log.d(TAG, "Activity card being sent: " + activity.getSearchQuery());
                    Activity currentActivity = placesClient.textSearch(activity.getSearchQuery(), PriceLevel.values()[activity.getPriceLevel()]);
                    if (currentActivity != null) {
                        Log.d(TAG, "Activity generated: " + currentActivity.gName);
                        questActivities.add(currentActivity);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            Log.d(TAG, "Returning from doInBackground");
            return questActivities;
        }

        @Override
        protected void onPostExecute(List<Activity> activities) {
            Log.d(TAG, "In onPostExecute");
            super.onPostExecute(activities); // potentially don't need to call
            // stop loading icon
            progressCardView.setVisibility(View.GONE);
            // open new activity with intent and activities
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressTextView.setText("Looking for " + values[0] + "...");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        apiContext.shutdown(); // todo NetworkOnMainThreadException
    }

    // LOCATION method

    public void useCurrentLocation(View v) {
        startLocationTextView.setTypeface(startLocationTextView.getTypeface(), Typeface.ITALIC);
        boolean locationPermission = checkLocationPermissions();
        if (locationPermission) {
            Log.v(TAG, "Permission already granted!");
            getLocation();
        } else {
            Log.v(TAG, "Requesting permission...");
            requestLocationPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        startLocationTextView.setHint("Getting current location...");
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        Criteria criteria = setLocationCriteria();
        locationManager.requestSingleUpdate(criteria, locationListener, null);
    }

    private boolean checkLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
    }

    private Criteria setLocationCriteria() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        return criteria;
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Location permission granted!");
                getLocation();
            } else {
                startLocationTextView.setHint("Location permission not given."); // todo make string constant
            }
        }
    }

    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLocation = new LatLng(location.getLatitude(), location.getLongitude());
            Log.d(TAG, "Location Changed to: " + location.toString());
            startLocationTextView.setHint(location.getLongitude() + ", " + location.getLatitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "Status Changed to: " + String.valueOf(status));
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "Provider Enabled - " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "Provider Disabled - " + provider);
        }
    };
}