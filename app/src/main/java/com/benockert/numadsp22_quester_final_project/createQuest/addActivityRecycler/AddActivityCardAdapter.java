package com.benockert.numadsp22_quester_final_project.createQuest.addActivityRecycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.slider.Slider;
import com.google.maps.model.PriceLevel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddActivityCardAdapter extends RecyclerView.Adapter<AddActivityCardHolder> {
    private static final String TAG = "CREATE_QUEST_ACTIVITY_CARD";
    private ArrayList<AddActivityCard> activityCardList;
    private final List<String> popularityLevels = Arrays.asList("Hole In The Wall", "Hidden Gem", "Everyday Spot", "Talk Of The Town");
    private final List<String> priceLevelStrings = Arrays.asList("", "$", "$$", "$$$", "$$$$");

    private Context context;

    public AddActivityCardAdapter(ArrayList<AddActivityCard> activityCardList, Context context) {
        this.activityCardList = activityCardList;
        this.context = context;
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
        holder.priceLevelSlider.setValue((float) currentCard.priceLevel);
        holder.priceLevelTextView.setText(priceLevelStrings.get(currentCard.priceLevel));
        holder.popularitySlider.setValue((float) currentCard.popularity);
        holder.popularityTextView.setText(popularityLevels.get(currentCard.popularity));

        // handle price level slider changes
        holder.priceLevelSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                int priceLevelInt = Math.round(value);
                holder.priceLevelTextView.setText(priceLevelStrings.get(priceLevelInt));
                activityCardList.get(holder.getAdapterPosition()).setPriceLevel(priceLevelInt);
            }
        });

        // handle popularity slider changes
        holder.popularitySlider.addOnChangeListener(new Slider.OnChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                int popularityInt = Math.round(value);
                holder.popularityTextView.setText(popularityLevels.get(popularityInt));
                activityCardList.get(holder.getAdapterPosition()).setPopularity(popularityInt);
            }
        });

        // handle search query changes
        holder.userTextQueryView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                activityCardList.get(holder.getAdapterPosition()).setSearchQuery(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // handle card collapse
        if (currentCard.isCollapsed) {
            holder.collapsableGroup.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return activityCardList.size();
    }


}
