package com.benockert.numadsp22_quester_final_project.createQuest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.benockert.numadsp22_quester_final_project.R;
import com.benockert.numadsp22_quester_final_project.utils.GooglePlacesClient;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.google.maps.GeoApiContext;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.PriceLevel;

import java.util.Locale;

public class CreateQuestActivityCopy extends AppCompatActivity {
    private String TAG = "LOG_QUESTER_CREATE_QUEST_ACTIVITY";
    private int LOCATION_PERMISSION_ID = 1;

    private int METERS_IN_ONE_MILE = 1609;

    final PriceLevel[] priceLevels = {PriceLevel.FREE, PriceLevel.INEXPENSIVE, PriceLevel.MODERATE, PriceLevel.EXPENSIVE, PriceLevel.VERY_EXPENSIVE};
    final String[] priceLevelStrings = {"", "$", "$$", "$$$", "$$$"};

    private LocationManager locationManager;
    private String apiKey;
    private GeoApiContext apiContext;

    private Context context;
    private Location mLocation;

    public TextView searchQueryInput;
    public Button submitSearchQueryButton;
    public TextView responseTextView;

    public TextView startLocationTextView;
    public Button useCurrentLocationButton;
    public Slider proximitySlider;
    public Slider priceLevelSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quest);

        context = getApplicationContext();

        searchQueryInput = findViewById(R.id.placeSearchQuery);
        submitSearchQueryButton = findViewById(R.id.submitSearchButton);
        responseTextView = findViewById(R.id.searchResponseView);
        startLocationTextView = findViewById(R.id.questLocationTextView);
        useCurrentLocationButton = findViewById(R.id.useCurrentLocationButton);
        proximitySlider = findViewById(R.id.proximitySlider);
        priceLevelSlider = findViewById(R.id.priceLevelSlider);

        proximitySlider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.US, "%.2f", value) + " mi";
            }
        });

        priceLevelSlider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                int priceLevelValue = Math.round(value);
                return priceLevelStrings[priceLevelValue];
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

    public void sendSearch(View v) {
        GooglePlacesClient client = new GooglePlacesClient(apiContext);

        String query = searchQueryInput.getText().toString();
        LatLng location = new LatLng(mLocation.getLatitude(), mLocation.getLongitude()); // todo user location
        int radius = Math.round(proximitySlider.getValue() * METERS_IN_ONE_MILE); //
        // todo check if this is working
        PriceLevel priceLevel = priceLevels[Math.round(priceLevelSlider.getValue())]; //todo add onChangeListener to sliders

        PlacesSearchResult[] places = client.textSearch(query, location, radius, priceLevel);
        if (places != null) {
            // TODO do shit here
            responseTextView.setText(places[0].name);
        }
    }

    public void useCurrentLocation(View v) {
        boolean locationPermission = checkLocationPermissions();
        if (locationPermission) {
            Log.v(TAG, "Permission already granted!");
            getLocation();
        } else {
            Log.v(TAG, "Requesting permission...");
            requestLocationPermissions();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        apiContext.shutdown(); // todo NetworkOnMainThreadException
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        locationManager = (LocationManager)getSystemService(context.LOCATION_SERVICE);
        Criteria criteria = setLocationCriteria();
        locationManager.requestSingleUpdate(criteria, locationListener, null);
    }

    private boolean checkLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
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
            mLocation = location;
            Log.d(TAG, "Location Changed to: " + location.toString());
            String coordString = location.getLatitude() + ", " + location.getLongitude();
            startLocationTextView.setHint(coordString);
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