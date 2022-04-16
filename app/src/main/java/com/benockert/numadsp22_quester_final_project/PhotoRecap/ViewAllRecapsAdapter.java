package com.benockert.numadsp22_quester_final_project.PhotoRecap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.R;

import java.util.ArrayList;

public class ViewAllRecapsAdapter extends RecyclerView.Adapter<ViewAllRecapsHolder> {
    private final ArrayList<RecapCard> rCardList;

    public ViewAllRecapsAdapter(ArrayList<RecapCard> rCardList) {
        this.rCardList = rCardList;
    }

    /**
     * creates a new recycler view holder initializing private fields to be used by RecyclerView
     *
     * @param parent   ViewGroup
     * @param viewType Integer
     * @return pastQuestHolder
     */
    @NonNull
    @Override
    public ViewAllRecapsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_view_all_recaps, parent, false);
        return new ViewAllRecapsHolder(view);
    }

    /**
     * Called by RecyclerView to display data at a specified position
     *
     * @param holder   pastQuestHolder
     * @param position Integer
     */
    @Override
    public void onBindViewHolder(@NonNull ViewAllRecapsHolder holder, int position) {
        //getting the current card
        RecapCard currentCard = rCardList.get(position);
        String tempName = "Recap Name: " + currentCard.getRecapName();
        String tempDate ="Date Generated: " + currentCard.getRecapDate();

        holder.recapName.setText(tempName);
        holder.recapDate.setText(tempDate);
    }

    /**
     * @return Integer size of the past recap card list
     */
    @Override
    public int getItemCount() {
        return this.rCardList.size();
    }
}
