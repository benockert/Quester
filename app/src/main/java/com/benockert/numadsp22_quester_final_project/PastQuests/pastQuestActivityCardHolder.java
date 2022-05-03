package com.benockert.numadsp22_quester_final_project.PastQuests;

import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.R;

public class pastQuestActivityCardHolder extends RecyclerView.ViewHolder {
    public TextView price;
    public TextView locationName;
    public TextView address;
    public ImageView maps;

    public pastQuestActivityCardHolder(@NonNull View itemView) {
        super(itemView);
        price = itemView.findViewById(R.id.activityPrice);
        locationName = itemView.findViewById(R.id.activityName);
        address = itemView.findViewById(R.id.locationAddress);
        maps = itemView.findViewById(R.id.mapsImg);
    }
}
