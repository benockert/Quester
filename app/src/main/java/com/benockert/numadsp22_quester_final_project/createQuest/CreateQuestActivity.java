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

    private void createRecyclerView() {
        recyclerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView = findViewById(R.id.activityCardRecyclerView);
        recyclerView.setHasFixedSize(true);
        //Collections.sort(cardList, new ReceivedStickerComparator());
        activityCardAdapter = new AddActivityCardAdapter(activityCards);
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

        // collapse all other cards except last
        for (int i=0; i < activityCards.size() - 1; i++) {
            activityCards.get(i).setIsCollapsed(true);
            activityCardAdapter.notifyItemChanged(i);
        }
    }

    public void onGenerateQuestButtonClick(View v) {
        CharSequence locationString = startLocationTextView.getText();
        int radiusInMeters = Math.round(proximitySlider.getValue() * METERS_IN_ONE_MILE);
        if (locationString != "") {
            placesClient = new GooglePlacesClient(apiContext, radiusInMeters, locationString.toString());
        } else if (mLocation != null) {
            placesClient = new GooglePlacesClient(apiContext, radiusInMeters, mLocation);
        }

        // start loading icon


        List<Activity> questActivities = new ArrayList<>();
        for (AddActivityCard activity : activityCards) {
            Activity currentActivity = placesClient.textSearch(activity.getSearchQuery(), PriceLevel.values()[activity.priceLevel]);
            if (currentActivity != null) {
                questActivities.add(currentActivity);
            }
        }

        // stop loading and open new intent with the list of activities
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