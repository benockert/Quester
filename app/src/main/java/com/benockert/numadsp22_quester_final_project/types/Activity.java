package com.benockert.numadsp22_quester_final_project.types;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Activity implements Parcelable {
    public String gFormattedAddress;
    public String gName;
    public String gPhotoReference;
    public String gPlaceId;
    public double gPlaceLat;
    public double gPlaceLng;
    public int uPriceLevel;
    public String uQuery;
    public double gRating;

    public Activity() {
    }

    public Activity(String gFormattedAddress, String gName, String gPhotoReference, String gPlaceId, double gPlaceLat, double gPlaceLng, int uPriceLevel, String uQuery, double gRating) {
        this.gFormattedAddress = gFormattedAddress;
        this.gName = gName;
        this.gPhotoReference = gPhotoReference;
        this.gPlaceId = gPlaceId;
        this.gPlaceLat = gPlaceLat;
        this.gPlaceLng = gPlaceLng;
        this.uPriceLevel = uPriceLevel;
        this.uQuery = uQuery;
        this.gRating = gRating;
    }

    public Activity(Parcel in) {
        gFormattedAddress = in.readString();
        gName = in.readString();
        gPhotoReference = in.readString();
        gPlaceId = in.readString();
        gPlaceLat = in.readDouble();
        gPlaceLng = in.readDouble();
        uPriceLevel = in.readInt();
        uQuery = in.readString();
        gRating = in.readDouble();
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
        parcel.writeString(uQuery);
        parcel.writeDouble(gRating);
    }

    public static Activity getActivityFromJSON(String data) {
        try {
            JSONObject activityObj = new JSONObject(data);
            Log.i("activities", activityObj.toString());
            String gFormattedAddress = activityObj.getString("gFormattedAddress").replaceAll("_", " ");
            ;
            String gName = activityObj.getString("gName").replaceAll("_", " ");
            String gPhotoReference = activityObj.getString("gPhotoReference");
            String gPlaceId = activityObj.getString("gPlaceId");
            double gPlaceLat = Float.parseFloat(activityObj.getString("gPlaceLat"));
            double gPlaceLng = Float.parseFloat(activityObj.getString("gPlaceLng"));
            int gPriceLevel = activityObj.getInt("gPriceLevel");
            double gRating = activityObj.getDouble("gRating");
            String uQuery = activityObj.getString("uQuery").replaceAll("_", " ");

            return new Activity(gFormattedAddress,
                    gName,
                    gPhotoReference,
                    gPlaceId,
                    gPlaceLat,
                    gPlaceLng,
                    gPriceLevel,
                    uQuery,
                    gRating);
        } catch (JSONException e) {
            Log.e("ACTIVITY_PARSER", "JSONException");
            e.printStackTrace();
            return null;
        }
    }
}