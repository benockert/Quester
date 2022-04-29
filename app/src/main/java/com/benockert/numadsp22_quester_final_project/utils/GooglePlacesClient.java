package com.benockert.numadsp22_quester_final_project.utils;

import android.util.Log;

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

public class GooglePlacesClient {
    private final String TAG = "LOG_QUESTER_GOOGLE_API_CLIENT";
    private final GeoApiContext context;

    public GooglePlacesClient(GeoApiContext context) {
        this.context = context;
    }

    public PlacesSearchResult[] textSearch(String query,
                                           LatLng location,
                                           int radius,
                                           PriceLevel priceLevel) {
        TextSearchRequest request = new TextSearchRequest(context);
        request.query(query);
        request.location(location);
        request.radius(radius);
        request.minPrice(priceLevel);
        request.maxPrice(priceLevel);

        try {
            PlacesSearchResponse response  = request.await();
            PlacesSearchResult[] results = response.results;
            Log.d(TAG, "Number of Place results returned: " + results.length);
            return results;
        } catch (ApiException | IOException | InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in text search request");
        }
        return null;
    }



    public byte[] getPlacePhoto(String photoReference, int maxWidth, int maxHeight) {
        try {
            ImageResult response = PlacesApi.photo(this.context, photoReference).maxWidth(maxWidth).maxHeight(maxHeight).await();
            byte[] photoData = response.imageData;
            Log.d(TAG, "Place photo content type " + response.contentType);
            return photoData;
        } catch (ApiException | IOException | InterruptedException |IllegalArgumentException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in place photo request");
        }
        return null;
    }
}
