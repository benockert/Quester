package com.benockert.numadsp22_quester_final_project.activeQuest.previewStopCard;

import android.util.Log;
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

    public PreviewCardAdapter(ArrayList<Activity> activityList) {
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public PreviewCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.active_quest_preview_activity_card, parent, false);
        return new PreviewCardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewCardHolder holder, int position) {
        Activity currentCard = activityList.get(position);
        holder.textPreviewStopName.setText(currentCard.getgName());
        Log.d(TAG + " SETTING ACTIVITY", currentCard.getgName());
        holder.textPreviewUserSearchTerm.setText(currentCard.getuQuery().toUpperCase());
        holder.textPreviewStopCount.setText(String.format("%s/%s", position+1, getItemCount()));
    }

    @Override
    public int getItemCount() {
            return activityList.size();
    }

}
