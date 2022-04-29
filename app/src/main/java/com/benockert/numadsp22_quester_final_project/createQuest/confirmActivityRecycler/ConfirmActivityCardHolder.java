package com.benockert.numadsp22_quester_final_project.createQuest.confirmActivityRecycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.benockert.numadsp22_quester_final_project.R;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.Slider;

import org.w3c.dom.Text;

public class ConfirmActivityCardHolder extends RecyclerView.ViewHolder {
    public TextView activityNameTextView;
    public TextView activityUserQueryTextView;
    public ImageView activityPlaceImageView;

    public ConfirmActivityCardHolder(View itemView) {
        super(itemView);
        activityNameTextView = itemView.findViewById(R.id.placeNameTextView);
        activityUserQueryTextView = itemView.findViewById(R.id.userQueryTextView);
        activityPlaceImageView = itemView.findViewById(R.id.placeImageView);
    }
}
