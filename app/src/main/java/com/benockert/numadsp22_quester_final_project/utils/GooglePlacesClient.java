package com.benockert.numadsp22_quester_final_project.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GooglePlacesClient {
    private final String TAG = "LOG_QUESTER_GOOGLE_PLACES_CLIENT";
    private final String PLACES_TEXT_SEARCH_API_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?";

    private final String apiKey;

    public GooglePlacesClient(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * @param params a map of URL params
     * @return a JSONObject of the response results
     * @note params must include a 'query' parameter
     */
    public JSONArray textSearch(Map<String, String> params) throws IOException {
        params.put("key", apiKey);

        String encodedURL = params.keySet().stream()
                .map(key -> key + "=" + encodeString(params.get(key)))
                .collect(Collectors.joining("&", PLACES_TEXT_SEARCH_API_URL, ""));

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(encodedURL)
                .method("GET", null)
                .build();

        return sendRequest(client, request);
    }

    private JSONArray sendRequest(OkHttpClient client, Request request) {
        CompletableFuture<String> future = new CompletableFuture<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failure in search request to " + request.url());
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Unexpected code " + response + " in request to " + request.url());
                }
                future.complete(response.body().string());
            }
        });

        while (!future.isDone()) {
            Log.d(TAG, "Waiting for response...");
        }

        JSONArray resultsJsonArray = new JSONArray();
        try {
            resultsJsonArray = new JSONObject(future.get()).getJSONArray("results");
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.e(TAG, "ExecutiionException while getting response future");
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "InterruptedException while getting response future");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSONException while parsing response data");
        }

        return resultsJsonArray;
    }

    private String encodeString(String value) {
        String encodedValue = value;
        try {
            encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d(TAG, "Error ending URL string param");
        }
        return encodedValue;
    }
}
