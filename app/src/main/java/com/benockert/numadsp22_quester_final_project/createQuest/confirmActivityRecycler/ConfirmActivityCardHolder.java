package com.benockert.numadsp22_quester_final_project.createQuest.confirmActivityRecycler;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.benockert.numadsp22_quester_final_project.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.Slider;

import org.w3c.dom.Text;

public class ConfirmActivityCardHolder extends RecyclerView.ViewHolder {
    private final String TAG = "QUESTER_CONFIRM_ACTIVITY_HOLDER";

    public TextView activityNameTextView;
    public TextView activityUserQueryTextView;
    public ImageView activityPlaceImageView;
    public MaterialButton regenerateActivityButton;
    public MaterialCardView disabledCardOverlay;
    public MaterialCardView confirmActivityCardView;

    public ConfirmActivityCardHolder(View itemView, final RegenerateButtonClickListener regenerateButtonClickListener) {
        super(itemView);
        activityNameTextView = itemView.findViewById(R.id.placeNameTextView);
        activityUserQueryTextView = itemView.findViewById(R.id.userQueryTextView);
        activityPlaceImageView = itemView.findViewById(R.id.placeImageView);
        regenerateActivityButton = itemView.findViewById(R.id.reSpinActivityButton);
        //disabledCardOverlay = itemView.findViewById(R.id.regenerateLoadingCardOverlay);
        confirmActivityCardView = itemView.findViewById(R.id.activity_baseCardView);

        regenerateActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (regenerateButtonClickListener != null) {
                    int position = getLayoutPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        //Log.d(TAG, "In onClick, setting overlay to VISIBLE");
                        //disabledCardOverlay.setVisibility(View.VISIBLE);
                        Log.v(TAG, "In onClick, searching for new activity");
                        regenerateButtonClickListener.onClick(v, position);
                        //Log.v(TAG, "In onClick, setting overlay to INVISIBLE");
                        //disabledCardOverlay.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }
}
