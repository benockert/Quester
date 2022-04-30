package com.benockert.numadsp22_quester_final_project.createQuest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.benockert.numadsp22_quester_final_project.R;
import com.benockert.numadsp22_quester_final_project.createQuest.addActivityRecycler.AddActivityCard;
import com.benockert.numadsp22_quester_final_project.createQuest.addActivityRecycler.AddActivityCardAdapter;
import com.benockert.numadsp22_quester_final_project.types.Activity;
import com.benockert.numadsp22_quester_final_project.utils.GooglePlacesClient;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.google.maps.GeoApiContext;
import com.google.maps.model.LatLng;
import com.google.maps.model.PriceLevel;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class CreateQuestActivity extends AppCompatActivity {
    private String TAG = "LOG_QUESTER_CREATE_QUEST_ACTIVITY";

    // Intent strings
    public static String APIKEY_INTENT_MESSAGE = "com.benockert.numadsp22_quester_final_project.createQuest.APIKEY";
    public static String CONFIRM_ACTIVITIES_INTENT_MESSAGE = "com.benockert.numadsp22_quester_final_project.createQuest.ConfirmQuestActivities";
    public static String LOCATION_STRING_INTENT_MESSAGE = "com.benockert.numadsp22_quester_final_project.createQuest.UserLocationString";
    public static String PROXIMITY_METERS_INTENT_MESSAGE = "com.benockert.numadsp22_quester_final_project.createQuest.UserQuestProximity";
    public static String PHOTO_REFERENCE_INTENT_MESSAGE = "com.benockert.numadsp22_quester_final_project.createQuest.QuestLocationPhotoReference";


    private int LOCATION_PERMISSION_ID = 1;
    public static int METERS_IN_ONE_MILE = 1609;

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
    private Group progressIndicatorGroup;
    private TextView progressTextView;
    private TextView missingLocationTextView;

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

        missingLocationTextView = findViewById(R.id.missingLocationText);
        progressIndicatorGroup = findViewById(R.id.progressIndicatorGroup);
        progressTextView = findViewById(R.id.progressText);

        proximitySlider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.US, "%.2f", value) + " mi";
            }
        });

        // text change listener to remove the "missing text" message when a location is added manually
        startLocationTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                missingLocationTextView.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {
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
        Log.d(TAG, "SAVING INSTANCE STATE");
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
        // if first card is being added
        if (activityCards.size() == 0) {
            addFirstActivityTextView.setVisibility(View.GONE);
            if (mLocation != null || startLocationTextView.getText() != "") {
                generateQuestButton.setEnabled(true);
            }
        }

        AddActivityCard newCard = new AddActivityCard();
        activityCards.add(newCard);
        activityCardAdapter.notifyItemInserted(activityCards.indexOf(newCard));

        // collapse all other cards except last, retain state
        for (int i=0; i < activityCards.size() - 1; i++) {
            activityCards.get(i).setIsCollapsed(true);
            activityCardAdapter.notifyItemChanged(i);
        }
    }

    public void onGenerateQuestButtonClick(View v) {
        Log.d(TAG, "Generate quest button clicked");
        // start loading icon
        progressIndicatorGroup.setVisibility(View.VISIBLE);

        // get location and proximity (in meters)
        String locationString = startLocationTextView.getText().toString();
        int radiusInMeters = Math.round(proximitySlider.getValue() * METERS_IN_ONE_MILE);

        if (locationString.equals("") && mLocation == null) {
            missingLocationTextView.setText("Please enter a start location.");
        } else if (!locationString.equals("")) {
            progressIndicatorGroup.setVisibility(View.VISIBLE);
            placesClient = new GooglePlacesClient(apiContext, radiusInMeters, locationString);
            // get location info from location string
            Log.d(TAG, "Using manual location entry: getting place info");
            Map<String, String> result = placesClient.locationTextSearch();
            if (result != null) {
                Log.d(TAG, "Result not null, generating request");
                generateQuest(result.get("placeName"), result.get("photoReference"));
            }
        } else if (mLocation != null) {
            progressIndicatorGroup.setVisibility(View.VISIBLE);
            placesClient = new GooglePlacesClient(apiContext, radiusInMeters, mLocation);
            // reverse geo code to get location info from coordinates
            Log.d(TAG, "Using current location: getting reverse geocode information");
            Map<String, String> result = placesClient.getReverseGeoLocationInfo(mLocation);
            if (result != null) {
                Log.d(TAG, "Result not null, generating request");
                generateQuest(result.get("placeName"), result.get("photoReference"));
            }
        }
    }

    private void generateQuest(String questLocation, String questLocationPhotoReference) {
        // find activities asynchronously
        GenerateQuest task = new GenerateQuest(questLocation, questLocationPhotoReference);
        task.execute(activityCards);
    }

    @SuppressLint("StaticFieldLeak")
    private class GenerateQuest extends AsyncTask<ArrayList<AddActivityCard>, String, ArrayList<Activity>> {
        String questLocation;
        String questLocationPhotoReference;

        public GenerateQuest(String questLocation, String questLocationPhotoReference) {
            this.questLocation = questLocation;
            this.questLocationPhotoReference = questLocationPhotoReference;
        }

        @SafeVarargs
        @Override
        protected final ArrayList<Activity> doInBackground(ArrayList<AddActivityCard>... addActivityCards) {
            Log.d(TAG, "In doInBackground");
            ArrayList<Activity> questActivities = new ArrayList<>();
            for (AddActivityCard activity : addActivityCards[0]) {
                try {
                    publishProgress(activity.searchQuery);
                    Log.d(TAG, "Activity card being sent: " + activity.getSearchQuery());
                    Activity currentActivity = placesClient.textSearch(activity.getSearchQuery(), PriceLevel.values()[activity.getPriceLevel()], activity.getPopularity(), true);
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
        protected void onPostExecute(ArrayList<Activity> activities) {
            Log.d(TAG, "In onPostExecute");
            super.onPostExecute(activities); // potentially don't need to call

            // open new activity with intent and activities
            Intent intent = new Intent(context, ConfirmQuestActivity.class);
            intent.putExtra(APIKEY_INTENT_MESSAGE, apiKey);
            intent.putExtra(LOCATION_STRING_INTENT_MESSAGE, questLocation);
            intent.putExtra(PROXIMITY_METERS_INTENT_MESSAGE, Math.round(proximitySlider.getValue() * METERS_IN_ONE_MILE));
            intent.putExtra(PHOTO_REFERENCE_INTENT_MESSAGE, questLocationPhotoReference);
            intent.putParcelableArrayListExtra(CONFIRM_ACTIVITIES_INTENT_MESSAGE, activities);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            // stop loading icon
            progressIndicatorGroup.setVisibility(View.GONE);
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

    // LOCATION methods

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
        missingLocationTextView.setText("");
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