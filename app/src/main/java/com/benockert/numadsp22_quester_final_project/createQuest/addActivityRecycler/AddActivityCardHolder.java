package com.benockert.numadsp22_quester_final_project.createQuest.addActivityRecycler;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.benockert.numadsp22_quester_final_project.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.Slider;

public class AddActivityCardHolder extends RecyclerView.ViewHolder {
    public TextView userTextQueryView;
    public ImageView dropdownArrowImageView;
    public TextView collapsedPriceLevelTextView;
    public MaterialButtonToggleGroup priceLevelButtonGroup;
    public TextView popularityTextView;
    public Slider popularitySlider;
    public Group collapsableGroup;
    public MaterialCardView cardView;

    public AddActivityCardHolder(View itemView) {
        super(itemView);
        userTextQueryView = itemView.findViewById(R.id.activityQueryText);
        priceLevelButtonGroup = itemView.findViewById(R.id.priceLevelButtonToggleGroup);
        popularityTextView = itemView.findViewById(R.id.popularityLevelTextRepresentation);
        popularitySlider = itemView.findViewById(R.id.popularitySlider);

        // collapsable group
        cardView = itemView.findViewById(R.id.activity_baseCardView);
        dropdownArrowImageView = itemView.findViewById(R.id.dropdownCarrot);
        collapsableGroup = itemView.findViewById(R.id.activityPreferencesGroup);

        // collapse carrot onClick behavior
        dropdownArrowImageView.setOnClickListener(view -> {
            if (collapsableGroup.getVisibility() == View.VISIBLE){
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                collapsableGroup.setVisibility(View.GONE);
                dropdownArrowImageView.setImageResource(android.R.drawable.arrow_down_float);
            }
            else {
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                collapsableGroup.setVisibility(View.VISIBLE);
                dropdownArrowImageView.setImageResource(android.R.drawable.arrow_up_float);
            }
        });

    }
}
