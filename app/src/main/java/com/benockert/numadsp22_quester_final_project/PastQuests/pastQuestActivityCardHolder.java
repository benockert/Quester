package com.benockert.numadsp22_quester_final_project.PastQuests;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.R;

public class pastQuestActivityCardHolder extends RecyclerView.ViewHolder {
    public ImageView locationImage;
    public TextView participants;
    public TextView questName;

    public pastQuestActivityCardHolder(@NonNull View itemView) {
        super(itemView);
        locationImage = itemView.findViewById(R.id.locationImage);
        participants = itemView.findViewById(R.id.participants);
        questName = itemView.findViewById(R.id.pastQuest_questName);
    }
}
