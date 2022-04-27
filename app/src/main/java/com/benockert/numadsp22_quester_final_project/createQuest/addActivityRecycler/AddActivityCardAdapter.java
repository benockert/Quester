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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddActivityCardAdapter extends RecyclerView.Adapter<AddActivityCardHolder> {
    private static final String TAG = "CREATE_QUEST_ACTIVITY_CARD";
    private ArrayList<AddActivityCard> activityCardList;
    final List<String> popularityLevels = Arrays.asList("Hole In The Wall", "Hidden Gem", "Everyday Spot", "Talk Of The Town");

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
        holder.popularitySlider.setValue((float) currentCard.popularity);
        holder.popularityTextView.setText(popularityLevels.get(currentCard.popularity));

        // set and style checked button on bind
        MaterialButton checkedButton = (MaterialButton)holder.priceLevelButtonGroup.getChildAt(activityCardList.get(holder.getAdapterPosition()).getPriceLevel() - 1);
        Log.d(TAG, "Button checked on bind " + checkedButton.getText() + " ; card at position => " + activityCardList.get(holder.getAdapterPosition()).getPriceLevel());
        checkedButton.setChecked(true);
        checkedButton.setTextColor(ContextCompat.getColor(context, R.color.white));
        checkedButton.setBackgroundColor(ContextCompat.getColor(context, R.color.green_700));

        holder.priceLevelButtonGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                MaterialButton changedButton = holder.cardView.findViewById(checkedId);
                if (isChecked) {
                    Log.d(TAG, "Button checked " + checkedId);
                    activityCardList.get(holder.getAdapterPosition()).setPriceLevel(changedButton.getText().length());
                    Log.d(TAG, "Activity card price level " + changedButton.getText().length());
                    changedButton.setTextColor(ContextCompat.getColor(context, R.color.white));
                    changedButton.setBackgroundColor(ContextCompat.getColor(context, R.color.green_700));
                } else {
                    changedButton.setTextColor(ContextCompat.getColor(context, R.color.green_700));
                    changedButton.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                }
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

        if (currentCard.isCollapsed) {
            holder.collapsableGroup.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return activityCardList.size();
    }


}
