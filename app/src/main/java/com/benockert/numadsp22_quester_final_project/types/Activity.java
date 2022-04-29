package com.benockert.numadsp22_quester_final_project.types;

import android.os.Parcel;
import android.os.Parcelable;

public class Activity implements Parcelable {
    public String gFormattedAddress;
    public String gName;
    public String gPhotoReference;
    public String gPlaceId;
    public double gPlaceLat;
    public double gPlaceLng;
    public int uPriceLevel;
    public String uQuery;

    public Activity() {
    }

    public Activity(String gFormattedAddress, String gName, String gPhotoReference, String gPlaceId, double gPlaceLat, double gPlaceLng, int uPriceLevel, String uQuery) {
        this.gFormattedAddress = gFormattedAddress;
        this.gName = gName;
        this.gPhotoReference = gPhotoReference;
        this.gPlaceId = gPlaceId;
        this.gPlaceLat = gPlaceLat;
        this.gPlaceLng = gPlaceLng;
        this.uPriceLevel = uPriceLevel;
        this.uQuery = uQuery;
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
    }
}
