package com.benockert.numadsp22_quester_final_project.activeQuest.previewStopCard;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.R;
import com.benockert.numadsp22_quester_final_project.types.Activity;

import java.util.ArrayList;

public class PreviewCardAdapter extends RecyclerView.Adapter<PreviewCardHolder> {
    private static final String TAG = "PREVIEW_ACTIVITY_CARD";
    private ArrayList<Activity> activityList;
    private int currentPosition;

    public PreviewCardAdapter(ArrayList<Activity> activityList, int currentStop) {
        this.activityList = activityList;
        this.currentPosition = currentStop;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    @NonNull
    @Override
    public PreviewCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.active_quest_preview_activity_card, parent, false);
        int deviceWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        view.getLayoutParams().width = deviceWidth / 10 * 9;
        return new PreviewCardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewCardHolder holder, int position) {
        Activity currentCard = activityList.get(position);
        if (position == this.getItemCount() - 1) {
            holder.textPreviewMessage.setText(R.string.last_stop);
        } else if (position > currentPosition) {
            holder.textPreviewMessage.setText(R.string.up_next);
        } else if (position < currentPosition) {
            holder.textPreviewMessage.setText(R.string.previous_stop);
        } else if (position == currentPosition) {
            holder.textPreviewMessage.setText(R.string.current_stop);
        }

        holder.textPreviewStopName.setText(currentCard.getgName());
        holder.textPreviewUserSearchTerm.setText(currentCard.getuQuery().toUpperCase());
        holder.textPreviewStopCount.setText(String.format("%s/%s", position + 1, getItemCount()));
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

}
