package com.benockert.numadsp22_quester_final_project.myQuests;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder{
    public TextView location;
    public ImageView coverPhoto;
    public TextView numParticipants;
    public TextView numActivities;
    public TextView date;

    public RecyclerViewHolder(View itemView, final LinkClickListener listener) {
        super(itemView);

        this.location = itemView.findViewById(R.id.location);
        this.coverPhoto = itemView.findViewById(R.id.location_image);
        this.numParticipants = itemView.findViewById(R.id.num_participants);
        this.numActivities = itemView.findViewById(R.id.num_activities);
        this.date = itemView.findViewById(R.id.date);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    int position = getLayoutPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position, view);
                    }
                }
            }
        });
    }
}
