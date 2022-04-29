package com.benockert.numadsp22_quester_final_project.createQuest.confirmActivityRecycler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.R;
import com.benockert.numadsp22_quester_final_project.types.Activity;
import com.benockert.numadsp22_quester_final_project.utils.GooglePlacesClient;
import com.google.maps.GeoApiContext;

import java.util.ArrayList;

public class ConfirmActivityCardAdapter extends RecyclerView.Adapter<ConfirmActivityCardHolder> {
    private static final String TAG = "CONFIRM_QUEST_ACTIVITY_CARD_ADAPTER";
    private ArrayList<Activity> confirmActivityList;
    private final GeoApiContext apiContext;

    public ConfirmActivityCardAdapter(ArrayList<Activity> confirmActivityList, GeoApiContext apiContext) {
        this.confirmActivityList = confirmActivityList;
        this.apiContext = apiContext;
    }

    @NonNull
    @Override
    public ConfirmActivityCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.confirm_quest_activity_card, parent, false);
        return new ConfirmActivityCardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfirmActivityCardHolder holder, int position) {
        Activity currentCard = confirmActivityList.get(position);
        holder.activityNameTextView.setText(currentCard.getgName());
        holder.activityUserQueryTextView.setText(currentCard.getuQuery().toUpperCase());

        ImageView imageView = holder.activityPlaceImageView;
        int imgWidth = imageView.getMaxWidth();
        int imgHeight = imageView.getMaxHeight();

        GooglePlacesClient client = new GooglePlacesClient(apiContext);
        byte[] placePhotoBytes = client.getPlacePhoto(currentCard.getgPhotoReference(), imgWidth, imgHeight);
        Log.v(TAG, "Place Photo bytes length: " + placePhotoBytes.length);
        Bitmap bmp = BitmapFactory.decodeByteArray(placePhotoBytes, 0, placePhotoBytes.length);
        imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, imgWidth, imgHeight, false));
    }

    @Override
    public int getItemCount() {
        return confirmActivityList.size();
    }

}
