package com.benockert.numadsp22_quester_final_project.createQuest.recycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.slider.Slider;

public class AddActivityCardHolder extends RecyclerView.ViewHolder {
    public TextView userTextQueryView;
    public ImageView dropdownArrowImageView;
    public TextView collapsedPriceLevelTextView;
    public MaterialButtonToggleGroup priceLevelButtonGroup;
    public TextView popularityTextView;
    public Slider popularitySlider;

    public AddActivityCardHolder(View itemView) {
        super(itemView);
        userTextQueryView = itemView.findViewById()
    }
}
