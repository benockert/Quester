package com.benockert.numadsp22_quester_final_project.types;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import com.benockert.numadsp22_quester_final_project.utils.GooglePlacesClient;
import com.google.maps.model.PriceLevel;

import org.json.JSONException;
import org.json.JSONObject;


public class Activity implements Parcelable {
    private static final String TAG = "ACTIVITY_CLASS";

    public String gFormattedAddress;
    public String gName;
    public String gPhotoReference;
    public String gPlaceId;
    public double gPlaceLat;
    public double gPlaceLng;
    public int uPriceLevel;
    public String uQuery;
    public float gRating;
    public double uPopularity;

    public Activity() {
    }

    public Activity(String gFormattedAddress, String gName, String gPhotoReference, String gPlaceId, double gPlaceLat, double gPlaceLng, int uPriceLevel, double uPopularity, String uQuery, float gRating) {
        this.gFormattedAddress = gFormattedAddress;
        this.gName = gName;
        this.gPhotoReference = gPhotoReference;
        this.gPlaceId = gPlaceId;
        this.gPlaceLat = gPlaceLat;
        this.gPlaceLng = gPlaceLng;
        this.uPriceLevel = uPriceLevel;
        this.uQuery = uQuery;
        this.gRating = gRating;
        this.uPopularity = uPopularity;
    }

    public Activity(Parcel in) {
        gFormattedAddress = in.readString();
        gName = in.readString();
        gPhotoReference = in.readString();
        gPlaceId = in.readString();
        gPlaceLat = in.readDouble();
        gPlaceLng = in.readDouble();
        uPriceLevel = in.readInt();
        uPopularity = in.readDouble();
        uQuery = in.readString();
        gRating = in.readFloat();
    }

    public static final Creator<Activity> CREATOR = new Creator<Activity>() {
        @Override
        public Activity createFromParcel(Parcel in) {
            return new Activity(in);
        }

        @Override
        public Activity[] newArray(int size) {
            return new Activity[size];
        }
    };

    public String getgFormattedAddress() {
        return gFormattedAddress;
    }

    public String getgName() {
        return gName;
    }

    public String getgPhotoReference() {
        return gPhotoReference;
    }

    public String getgPlaceId() {
        return gPlaceId;
    }

    public double getgPlaceLat() {
        return gPlaceLat;
    }

    public double getgPlaceLng() {
        return gPlaceLng;
    }

    public int getuPriceLevel() {
        return uPriceLevel;
    }

    public String getuQuery() {
        return uQuery;
    }

    public double getgRating() {
        return gRating;
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(gFormattedAddress);
        parcel.writeString(gName);
        parcel.writeString(gPhotoReference);
        parcel.writeString(gPlaceId);
        parcel.writeDouble(gPlaceLat);
        parcel.writeDouble(gPlaceLng);
        parcel.writeInt(uPriceLevel);
        parcel.writeDouble(uPopularity);
        parcel.writeString(uQuery);
        parcel.writeFloat(gRating);

    }

    public static Activity getActivityFromJSON(String data) {
        try {
            JSONObject activityObj = new JSONObject(data);
            Log.i("activities", activityObj.toString());
            String gFormattedAddress = activityObj.getString("gFormattedAddress").replaceAll("_", " ");
            String gName = activityObj.getString("gName").replaceAll("_", " ");
            String gPhotoReference = activityObj.getString("gPhotoReference");
            String gPlaceId = activityObj.getString("gPlaceId");
            double gPlaceLat = activityObj.getDouble("gPlaceLat");
            double gPlaceLng = activityObj.getDouble("gPlaceLng");
            int uPriceLevel = activityObj.getInt("uPriceLevel");
            double uPopularity = activityObj.getDouble("uPopularity");
            float gRating = Float.parseFloat(activityObj.getString("gRating"));

            String uQuery = activityObj.getString("uQuery").replaceAll("_", " ");

            return new Activity(gFormattedAddress,
                    gName,
                    gPhotoReference,
                    gPlaceId,
                    gPlaceLat,
                    gPlaceLng,
                    uPriceLevel,
                    uPopularity,
                    uQuery,
                    gRating);
        } catch (JSONException e) {
            Log.e("ACTIVITY_PARSER", "JSONException");
            e.printStackTrace();
            return null;
        }
    }

    public Activity onRegenerateActivityClick(View view, int position, GooglePlacesClient client) {
        Log.v(TAG, "onRegenerateActivityClick in Activity class, regenerating activity at position: " + position);
        Log.v(TAG, "Query: " + uQuery + " | PriceLevel: " + uPriceLevel + " | Popularity: " + uPopularity);
        return client.textSearch(this.uQuery, PriceLevel.values()[this.uPriceLevel], this.uPopularity, true);
    }
}

