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
    private RecapCardListener rc_listener;

    public ViewAllRecapsAdapter(ArrayList<RecapCard> rCardList) {
        this.rCardList = rCardList;
    }

    /**
     * sets the recap card listener of the current adapter
     *
     * @param rc_listener the given link listener the adapter will soon have
     */
    public void setRc_listener(RecapCardListener rc_listener){
        this.rc_listener = rc_listener;
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
        return new ViewAllRecapsHolder(view, rc_listener);
    }

    /**
     * Called by RecyclerView to display data at a specified position
     *
     * @param holder   ViewAllRecapsHolder
     * @param pos Integer representing the position of the current recap card
     */
    @Override
    public void onBindViewHolder(@NonNull ViewAllRecapsHolder holder, int pos) {
        //getting the current card
        RecapCard currentCard = rCardList.get(pos);
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
