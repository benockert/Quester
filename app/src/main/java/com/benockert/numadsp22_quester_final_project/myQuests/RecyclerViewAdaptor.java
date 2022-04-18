package com.benockert.numadsp22_quester_final_project.myQuests;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
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
        
        CardView view = (CardView) LayoutInflater.from(this.context).inflate(R.layout.quest_card, parent, false);
        return new RecyclerViewHolder(view, this.listener);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        QuestCard currentQuest = this.linkList.get(position);

        ImageView image = holder.coverPhoto;
        int imgWidth = image.getMaxWidth();
        int imgHeight = image.getMaxHeight();

        GooglePlacesClient client = new GooglePlacesClient(apiContext);
        byte[] placePhotoBytes = client.getPlacePhoto(currentQuest.getPhotoReference(), imgWidth, imgHeight);

        Bitmap bmp = BitmapFactory.decodeByteArray(placePhotoBytes, 0, placePhotoBytes.length);
        image.setImageBitmap(Bitmap.createScaledBitmap(bmp, imgWidth, imgHeight, false));

        holder.location.setText(currentQuest.getLocation());
        holder.date.setText(currentQuest.getDate());
        holder.numParticipants.setText(String.format(this.context.getString(R.string.num_participants), currentQuest.getNumUsers()));
        holder.numActivities.setText(String.format(this.context.getString(R.string.num_activities), currentQuest.getNumActivities()));

        if (currentQuest.getQuest().isActive()) {
            holder.view.setCardBackgroundColor(context.getResources().getColor(R.color.green_200));
        }
    }

    @Override
    public int getItemCount() {
        return this.linkList.size();
    }
}
