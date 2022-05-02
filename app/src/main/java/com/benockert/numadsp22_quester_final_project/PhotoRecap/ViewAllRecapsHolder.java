package com.benockert.numadsp22_quester_final_project.PhotoRecap;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.R;

public class ViewAllRecapsHolder extends RecyclerView.ViewHolder {
    public TextView recapName;
    public TextView recapDate;

    public ViewAllRecapsHolder(@NonNull View itemView, final RecapCardListener lstnr) {
        super(itemView);
        recapName = itemView.findViewById(R.id.recapName);
        recapDate = itemView.findViewById(R.id.recapDate);

        itemView.setOnClickListener(v -> {
            if(lstnr != null){
                int p = getLayoutPosition();
                if (p != RecyclerView.NO_POSITION) {
                    lstnr.onCardClick(p);
                }
            }
        });
    }
}
