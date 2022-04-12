package com.benockert.numadsp22_quester_final_project.myQuests;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.R;
import com.benockert.numadsp22_quester_final_project.utils.GooglePlacesClient;
import com.google.maps.GeoApiContext;

import java.util.ArrayList;

public class RecyclerViewAdaptor extends RecyclerView.Adapter<RecyclerViewHolder>{
    private final ArrayList<QuestCard> linkList;
    private LinkClickListener listener;

    GeoApiContext apiContext;

    private Context context;

    //Constructor
    public RecyclerViewAdaptor(ArrayList<QuestCard> linkList, GeoApiContext apiContext) {
        this.linkList = linkList;
        this.apiContext = apiContext;
    }

    public void setOnItemClickListener(LinkClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        
        View view = LayoutInflater.from(this.context).inflate(R.layout.quest_card, parent, false);
        return new RecyclerViewHolder(view, this.listener);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        QuestCard currentQuest = this.linkList.get(position);

//        GooglePlacesClient client = new GooglePlacesClient(apiContext);
//        byte[] placePhotoBytes = client.getPlacePhoto("CnRvAAAAwMpdHeWlXl-lH0vp7lez4znKPIWSWvgvZFISdKx45AwJVP1Qp37YOrH7sqHMJ8C-vBDC546decipPHchJhHZL94RcTUfPa1jWzo-rSHaTlbNtjh-N68RkcToUCuY9v2HNpo5mziqkir37WU8FJEqVBIQ4k938TI3e7bf8xq-uwDZcxoUbO_ZJzPxremiQurAYzCTwRhE_V0");
//
//        Bitmap bmp = BitmapFactory.decodeByteArray(placePhotoBytes, 0, placePhotoBytes.length);
//        ImageView image = holder.coverPhoto;
//        image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(), image.getHeight(), false));

        holder.location.setText(currentQuest.getLocation());
        holder.numParticipants.setText(String.format(this.context.getString(R.string.num_participants), currentQuest.getNumUsers()));
        holder.numActivities.setText(String.format(this.context.getString(R.string.num_activities), currentQuest.getNumActivities()));
    }

    @Override
    public int getItemCount() {
        return this.linkList.size();
    }
}
