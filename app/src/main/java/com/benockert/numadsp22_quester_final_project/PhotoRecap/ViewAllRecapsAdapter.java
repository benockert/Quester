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
     * @param parent   ViewGroup a special view that can contain other views
     * @param viewType Integer representing the type of view
     * @return ViewAllRecapsHolder 	A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewAllRecapsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recap_card, parent, false);
        return new ViewAllRecapsHolder(view);
    }

    /**
     * Called by RecyclerView to display data at a specified position
     *
     * @param holder   ViewAllRecapsHolder
     * @param position Integer
     */
    @Override
    public void onBindViewHolder(@NonNull ViewAllRecapsHolder holder, int position) {
        //getting the current card
        RecapCard currentCard = rCardList.get(position);
        //setting the values of the current card
        holder.recapName.setText(currentCard.getRecapName());
        holder.recapDate.setText(currentCard.getRecapDate());
    }

    /**
     * @return Integer size of the past recap card list
     */
    @Override
    public int getItemCount() {
        return this.rCardList.size();
    }
}
