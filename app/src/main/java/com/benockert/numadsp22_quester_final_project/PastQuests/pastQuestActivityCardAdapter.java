package com.benockert.numadsp22_quester_final_project.PastQuests;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.R;
import com.benockert.numadsp22_quester_final_project.types.Activity;

import java.util.ArrayList;

public class pastQuestActivityCardAdapter extends RecyclerView.Adapter<pastQuestActivityCardHolder> {
    private final ArrayList<Activity> qCardList;

    public pastQuestActivityCardAdapter(ArrayList<Activity> qCardList) {
        this.qCardList = qCardList;
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
    public pastQuestActivityCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_quest_activity_card, parent, false);
        return new pastQuestActivityCardHolder(view);
    }

    /**
     * Called by RecyclerView to display data at a specified position
     *
     * @param holder   pastQuestHolder
     * @param position Integer
     */
    @Override
    public void onBindViewHolder(@NonNull pastQuestActivityCardHolder holder, int position) {
        //getting the current card
        Activity currentCard = qCardList.get(position);
        //setting the name of the activity location
        String tempName = "Name: " + currentCard.getgName();
        holder.locationName.setText(tempName);
        //converting the integer price range to a series of dollar signs to display the price range
        StringBuilder temp = new StringBuilder();
        temp.append("Price Range: ");
        for (int x = 0; x < currentCard.getuPriceLevel(); x++) {
            temp.append("$");
        }
        holder.price.setText(temp);
        //setting the address of the activity location
        holder.address.setText(currentCard.getgFormattedAddress());


        //setting the image of the location
        //if there is no photo available it displays no image found
        String url = "";
//        = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";
//        url += currentCard.getLocationPhotoRef();
//       url+= "&key=currentCard.getImageAPIKey();
        Drawable locationPhoto = Drawable.createFromPath(url);

        if (locationPhoto == null) {
            Context appContext = holder.locationImage.getContext();
            Resources r = appContext.getResources();
            int drawableId = r.getIdentifier("no_image_found_foreground",
                    "drawable", appContext.getPackageName());
            locationPhoto = AppCompatResources.getDrawable(appContext, drawableId);
        }
        holder.locationImage.setImageDrawable(locationPhoto);

        holder.maps.setOnClickListener(view -> PastQuests.openInMaps(currentCard.gFormattedAddress));
    }

    /**
     * @return Integer size of the past quest card list
     */
    @Override
    public int getItemCount() {
        return this.qCardList.size();
    }
}