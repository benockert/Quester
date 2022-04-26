package com.benockert.numadsp22_quester_final_project.utils;

import java.util.Random;
import android.util.Log;

import com.benockert.numadsp22_quester_final_project.types.Activity;
import com.google.maps.GeoApiContext;
import com.google.maps.ImageResult;
import com.google.maps.PlacesApi;
import com.google.maps.TextSearchRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.PriceLevel;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GooglePlacesClient {
    private final String TAG = "LOG_QUESTER_GOOGLE_API_CLIENT";

    private final GeoApiContext context;
    private LatLng locationLatLng;
    private String locationString;
    private final int radius;
    private final Random rand;

    public GooglePlacesClient(GeoApiContext context, int radius, String location) {
        this.context = context;
        this.radius = radius;
        this.locationString = location;
        rand = new Random();
    }

    public GooglePlacesClient(GeoApiContext context, int radius, LatLng location) {
        this.context = context;
        this.radius = radius;
        this.locationLatLng = location;
        rand = new Random();
    }

    public Activity textSearch(String query, PriceLevel priceLevel) {
        TextSearchRequest request = new TextSearchRequest(context);

        // if location was provided manually by user, add location to query parameter
        if (locationString != null) {
            request.query(query + " in " + locationString);
        }
        // if user's current location is provided, set it as a parameter
        if (locationLatLng != null) {
            request.location(locationLatLng);
        }
        request.radius(radius);
        request.minPrice(priceLevel);
        request.maxPrice(priceLevel);

        try {
            PlacesSearchResponse response  = request.await();
            PlacesSearchResult[] results = response.results;
            Log.d(TAG, "Number of Place results returned: " + results.length);

            // filter places to make sure they are popular and are operational
            List<PlacesSearchResult> filteredPlaces = Arrays.stream(results).filter(a -> a.rating > 3.8 && !a.permanentlyClosed && a.businessStatus.equals("OPERATIONAL")).collect(Collectors.toList());
            int filteredPlacesLength = filteredPlaces.size();
            int randomSelection = rand.nextInt(filteredPlacesLength);

            PlacesSearchResult p = filteredPlaces.get(randomSelection);
            Activity a = new Activity(p.formattedAddress, p.name, null, p.placeId, p.geometry.location.lat, p.geometry.location.lng, priceLevel.ordinal(), query);
            Log.d(TAG, "Randomly selected activity: " + a.toString());
            return a;

        } catch (ApiException | IOException | InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in text search request");
        }

        // parse response
        // 3.8 rating and above, random selection
        return null;
    }

    public PlaceDetails getPlaceDetails(String placeId) {
        try {
            PlaceDetails response = PlacesApi.placeDetails(this.context, placeId).await();
            Log.d(TAG, "Photo reference from Place details: " + response.photos[0].photoReference);
            return response;
        } catch (ApiException | IOException | InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in place details request");
        }
        return null;
    }

    public byte[] getPlacePhoto(String photoReference) {
        try {
            ImageResult response = PlacesApi.photo(this.context, photoReference).await();
            byte[] photoData = response.imageData;
            Log.d(TAG, "Place photo content type " + response.contentType);
            return photoData;
        } catch (ApiException | IOException | InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in place photo request");
        }
        return null;
    }
}
