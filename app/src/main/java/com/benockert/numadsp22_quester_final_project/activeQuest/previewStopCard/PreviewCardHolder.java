package com.benockert.numadsp22_quester_final_project.activeQuest.previewStopCard;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.R;

public class PreviewCardHolder extends RecyclerView.ViewHolder {
    public TextView textPreviewStopName;
    public TextView textPreviewUserSearchTerm;
    public TextView textPreviewStopCount;

    public PreviewCardHolder(@NonNull View itemView) {
        super(itemView);
        textPreviewStopName = itemView.findViewById(R.id.textNextStopName);
        textPreviewUserSearchTerm = itemView.findViewById(R.id.textPreviewUserSearchTerm);
        textPreviewStopCount = itemView.findViewById(R.id.textNextStopCount);
    }
}
