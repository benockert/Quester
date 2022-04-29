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
    public float gRating;

    public Activity() {
    }

    public Activity() {

    }

    public Activity(String gName, int gPriceLevel,
                    String gPhotoReference, String gFormattedAddress) {
        this.gName = gName;
        this.gPriceLevel = gPriceLevel;
        this.gPhotoReference = gPhotoReference;
        this.gFormattedAddress = gFormattedAddress;
    }

    public Activity(String gFormattedAddress, String gName, String gPhotoReference, String gPlaceId, double gPlaceLat, double gPlaceLng, int uPriceLevel, String uQuery, float gRating) {
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

   /**
     * getter for the address of the location
     *
     * @return String representing the address of the location
     */
    public String getgFormattedAddress() {
        return gFormattedAddress;
    }

    /**
     * getter for the name of the activity location
     *
     * @return String representing the name of the activities location
     */
    public String getgName() {
        return gName;
    }

    /**
     * getter for the String referencing the photo of the activity location
     *
     * @return String representing a reference to a photo of the activity location
     */
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

    /**
     * getter for the price range of the activity
     *
     * @return String representing the number of $signs the price range is
     */
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
            int gPriceLevel = activityObj.getInt("gPriceLevel");
            float gRating = Float.parseFloat(activityObj.getString("gRating"));

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

