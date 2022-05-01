package com.benockert.numadsp22_quester_final_project.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.util.Log;

import com.benockert.numadsp22_quester_final_project.types.Activity;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.ImageResult;
import com.google.maps.PlacesApi;
import com.google.maps.TextSearchRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressType;
import com.google.maps.model.GeocodingResult;
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

    private final double MINIMUM_ALLOWED_RATING = 3.8;

    private final GeoApiContext context;
    private LatLng locationLatLng;
    private String locationString;
    private int radius;
    private Random rand;

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

    // for just the photos method
    public GooglePlacesClient(GeoApiContext context) {
        this.context = context;
    }

    public Activity textSearch(String query, PriceLevel priceLevel, double popularity, boolean addPriceConstraints) {
        Log.d(TAG, "In Place TextSearch, searching for: " + query + " | Price: " + priceLevel + " | Popularity: " + popularity);
        TextSearchRequest request = new TextSearchRequest(context);

        // if location was provided manually by user, add location to query parameter
        if (locationString != null) {
            request.query(query + " in " + locationString);
        }
        // if user's current location is provided, set it as a parameter
        if (locationLatLng != null) {
            request.query(query);
            request.location(locationLatLng);
        }
        request.radius(radius);

        // for non food/drink activities, prices are irrelevant and including them sometimes results in 0 places found
        if (addPriceConstraints) {
            request.minPrice(priceLevel);
            request.maxPrice(priceLevel);
        }

        try {
            PlacesSearchResponse response  = request.await();
            PlacesSearchResult[] results = response.results;
            Log.d(TAG, "Number of Place results returned: " + results.length);

            if (results.length > 0) {
                boolean isRestaurant = Arrays.stream(results[0].types).anyMatch(x -> x.equals("restaurant"));
                Log.d(TAG, "Places are restaurants? " + isRestaurant);

                // filter places to make sure they are popular and are operational
                List<PlacesSearchResult> filteredPlaces = Arrays.stream(results).filter(a -> a.rating > MINIMUM_ALLOWED_RATING && (!isRestaurant || a.userRatingsTotal < getRatingsThreshold((int)popularity)) && !a.permanentlyClosed && a.businessStatus.equals("OPERATIONAL")).collect(Collectors.toList());
                int filteredPlacesLength = filteredPlaces.size();
                Log.d(TAG, "Places left after filtering: " + filteredPlacesLength);
                int randomSelection = rand.nextInt(filteredPlacesLength);

                PlacesSearchResult p = filteredPlaces.get(randomSelection);
                String pPhotoReference = getPlacePhotoReference(p.placeId);
                Activity a = new Activity(p.formattedAddress.replace(",", ""), p.name, pPhotoReference, p.placeId, p.geometry.location.lat, p.geometry.location.lng, priceLevel.ordinal(), popularity, query, p.rating);
                Log.d(TAG, "Randomly selected activity: " + a.gName + " | Ratings: " + p.userRatingsTotal + " | Stars: " + p.rating);
                return a;
            } else {
                // rerun without price constraints
                return textSearch(query, priceLevel, popularity, false);
            }
        } catch (ApiException | IOException | InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in text search request");
        }
        return null;
    }

    public Map<String, String> locationTextSearch() {
        Log.d(TAG, "In locationTextSearch, searching for: " + locationString);
        TextSearchRequest request = new TextSearchRequest(context);

        // if location was provided manually by user, add location to query parameter
        if (locationString != null) {
            request.query(locationString);
        }

        try {
            PlacesSearchResponse response  = request.await();
            PlacesSearchResult[] results = response.results;
            Log.d(TAG, "Number of Place results returned (should be 1): " + results.length);

            Map<String, String> info = new HashMap<>();
            if (results.length > 0) {
                PlacesSearchResult p = results[0];
                String gName = p.name;
                info.put("placeName", gName);
                String gPhotoReference = getPlacePhotoReference(p.placeId);
                info.put("photoReference", gPhotoReference);
            } else {
                // use the user's original query
                info.put("placeName", locationString);
            }
            return info;
        } catch (ApiException | IOException | InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in location text search request");
        }
        return null;
    }

    private String getPlacePhotoReference(String placeId) {
        try {
            PlaceDetails response = PlacesApi.placeDetails(this.context, placeId).await();
            return response.photos[0].photoReference;
        } catch (ApiException | IOException | InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in place details request");
        }
        return null;
    }

    public byte[] getPlacePhoto(String photoReference, int maxWidth, int maxHeight) {
        Log.v(TAG, "Getting place photo --- " + photoReference);
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

    // for getting location names and photos from coordinates
    public Map<String, String> getReverseGeoLocationInfo(LatLng location) {
        Map<String, String> result = new HashMap<>();
        try {
            Log.d(TAG, "In getReverseGeoLocationInfo");
            GeocodingResult[] response = GeocodingApi.reverseGeocode(this.context, new LatLng(location.lat, location.lng)).await();
            List<GeocodingResult> filteredPlaces = Arrays.stream(response).filter(r -> Arrays.stream(r.types).allMatch(addressType -> addressType.toString().equals(AddressType.NEIGHBORHOOD.toString()) || addressType.toString().equals(AddressType.LOCALITY.toString()) || addressType.toString().equals(AddressType.POLITICAL.toString()))).collect(Collectors.toList());
            if (filteredPlaces.size() > 0) {
                Log.d(TAG, "Filtered place addresses has results");
                String placeId = filteredPlaces.get(0).placeId;
                String photoReference = getPlacePhotoReference(placeId);
                result.put("photoReference", photoReference);

                String placeString = Arrays.stream(filteredPlaces.get(0).addressComponents).findFirst().get().longName;
                result.put("placeName", placeString);
            }
            return result;
        } catch (ApiException | IOException | InterruptedException |IllegalArgumentException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in reverse geocode request");
        }
        return null;
    }

    private int getRatingsThreshold(int popularity) {
        switch (popularity) {
            case 0:
                return 300;
            case 1:
                return 600;
            case 2:
                return 1000;
            default:
            case 3:
                return 1000000; // reasonable upper limit of google reviews
        }
    }
}
