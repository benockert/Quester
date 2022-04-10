package com.benockert.numadsp22_quester_final_project.createQuest.addActivityRecycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.R;

import java.util.ArrayList;

public class AddActivityCardAdapter extends RecyclerView.Adapter<AddActivityCardHolder> {
    private static final String TAG = "CREATE_QUEST_ACTIVITY_CARD";
    private ArrayList<AddActivityCard> activityCardList;

    public AddActivityCardAdapter(ArrayList<AddActivityCard> activityCardList) {
        this.activityCardList = activityCardList;
    }

    @NonNull
    @Override
    public AddActivityCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_quest_activity_card, parent, false);
        return new AddActivityCardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddActivityCardHolder holder, int position) {
        AddActivityCard currentCard = activityCardList.get(position);
        holder.userTextQueryView.setText(currentCard.searchQuery);
        holder.popularitySlider.setValue((float) currentCard.popularity);
        holder.popularityTextView.setText(getPopularityString(currentCard.popularity));
        //holder.priceLevelButtonGroup.setSingleSelection(currentCard.priceLevel - 1);

        if (currentCard.isCollapsed) {
            holder.collapsableGroup.setVisibility(View.GONE);
            holder.collapsedPriceLevelTextView.setVisibility(View.VISIBLE);
            holder.collapsedPriceLevelTextView.setText(currentCard.getPriceLevelString());
        }
    }

    @Override
    public int getItemCount() {
        return activityCardList.size();
    }

    private String getPopularityString(double popularity) {
        if (popularity <= 2.5) {
            return "Hole In The Wall";
        } else if (popularity <= 5.0) {
            return "Hidden Gem";
        } else if (popularity <= 7.5) {
            return "Everyday Spot";
        } else {
            return "Talk Of The Town";
        }
    }
}
