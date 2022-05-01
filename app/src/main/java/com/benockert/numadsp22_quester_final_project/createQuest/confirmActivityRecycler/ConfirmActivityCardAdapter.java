package com.benockert.numadsp22_quester_final_project.createQuest.confirmActivityRecycler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.R;
import com.benockert.numadsp22_quester_final_project.types.Activity;
import com.benockert.numadsp22_quester_final_project.utils.GooglePlacesClient;
import com.google.maps.GeoApiContext;

import java.util.ArrayList;
import java.util.Collections;

public class ConfirmActivityCardAdapter extends RecyclerView.Adapter<ConfirmActivityCardHolder> implements ConfirmCardMoveCallback {
    private static final String TAG = "CONFIRM_QUEST_ACTIVITY_CARD_ADAPTER";
    private ArrayList<Activity> confirmActivityList;
    private final GeoApiContext apiContext;
    private RegenerateButtonClickListener regenerateClickListener;

    public ConfirmActivityCardAdapter(ArrayList<Activity> confirmActivityList, GeoApiContext apiContext) {
        this.confirmActivityList = confirmActivityList;
        this.apiContext = apiContext;
    }

    @NonNull
    @Override
    public ConfirmActivityCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.confirm_quest_activity_card, parent, false);
        return new ConfirmActivityCardHolder(view, regenerateClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfirmActivityCardHolder holder, int position) {
        Activity currentActivity = confirmActivityList.get(position);
        holder.activityNameTextView.setText(currentActivity.getgName());
        holder.activityUserQueryTextView.setText(currentActivity.getuQuery().toUpperCase());

        ImageView imageView = holder.activityPlaceImageView;
        int imgWidth = imageView.getMaxWidth();
        int imgHeight = imageView.getMaxHeight();

        GooglePlacesClient client = new GooglePlacesClient(apiContext);
        byte[] placePhotoBytes = client.getPlacePhoto(currentActivity.getgPhotoReference(), imgWidth, imgHeight);
        Log.v(TAG, "Place Photo bytes length: " + placePhotoBytes.length);
        Bitmap bmp = BitmapFactory.decodeByteArray(placePhotoBytes, 0, placePhotoBytes.length);
        imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, imgWidth, imgHeight, false));
    }

    @Override
    public int getItemCount() {
        return confirmActivityList.size();
    }

    public void setOnRegenerateButtonClickListener(RegenerateButtonClickListener regenerateButtonClickListener) {
        this.regenerateClickListener = regenerateButtonClickListener;
    }

    // for drag n drop
    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(confirmActivityList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(confirmActivityList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);

    }

    @Override
    public void onRowSelected(ConfirmActivityCardHolder myViewHolder) {
        myViewHolder.confirmActivityCardView.setStrokeColor(myViewHolder.activityNameTextView.getResources().getColor(R.color.green_200));
    }

    @Override
    public void onRowClear(ConfirmActivityCardHolder myViewHolder) {
        myViewHolder.confirmActivityCardView.setStrokeColor(myViewHolder.activityNameTextView.getResources().getColor(R.color.green_700));
    }
}
