package com.benockert.numadsp22_quester_final_project.createQuest.confirmQuestRecycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.R;
import com.benockert.numadsp22_quester_final_project.types.Activity;

import java.util.ArrayList;

public class ConfirmActivityCardAdapter extends RecyclerView.Adapter<AddActivityCardHolder> {
    private static final String TAG = "CONFIRM_QUEST_ACTIVITY_CARD";
    private ArrayList<Activity> confirmActivityList;

    public ConfirmActivityCardAdapter(ArrayList<Activity> confirmActivityList) {
        this.confirmActivityList = confirmActivityList;
    }

    @NonNull
    @Override
    public AddActivityCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_quest_activity_card, parent, false);
        return new AddActivityCardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddActivityCardHolder holder, int position) {
        Activity currentCard = confirmActivityList.get(position);

    }

    @Override
    public int getItemCount() {
        return confirmActivityList.size();
    }

}
