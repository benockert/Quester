package com.benockert.numadsp22_quester_final_project.createQuest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benockert.numadsp22_quester_final_project.R;
import com.benockert.numadsp22_quester_final_project.activeQuest.ActiveQuest;
import com.benockert.numadsp22_quester_final_project.createQuest.confirmActivityRecycler.ConfirmActivityCardAdapter;
import com.benockert.numadsp22_quester_final_project.createQuest.confirmActivityRecycler.ConfirmActivityCardHolder;
import com.benockert.numadsp22_quester_final_project.createQuest.confirmActivityRecycler.RegenerateButtonClickListener;
import com.benockert.numadsp22_quester_final_project.myQuests.MyQuestsActivity;
import com.benockert.numadsp22_quester_final_project.types.Activity;
import com.benockert.numadsp22_quester_final_project.types.Quest;
import com.benockert.numadsp22_quester_final_project.utils.GooglePlacesClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.GeoApiContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConfirmQuestActivity extends AppCompatActivity {
    private String TAG = "LOG_QUESTER_CONFIRM_QUEST_ACTIVITY";

    private FirebaseAuth mAuth;
    private DatabaseReference dr;
    private String currentUser;

    private String apiKey;
    private GeoApiContext apiContext;
    private GooglePlacesClient client;

    private ArrayList<Activity> activities;
    private RecyclerView recyclerView;
    private ConfirmActivityCardAdapter confirmActivityCardAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;

    private String questLocation;
    private String locationPhotoReference;
    private int questUserProximity; // for use in re-searching for an activity

    private ImageView questLocationImageView;
    private TextView questLocationTextView;
    private TextView numberOfActivitiesTextView;
    private MaterialButton startQuestNowButton;
    private MaterialButton startQuestLaterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_quest);

        apiKey = getIntent().getStringExtra(CreateQuestActivity.APIKEY_INTENT_MESSAGE);
        apiContext = new GeoApiContext.Builder().apiKey(apiKey).build();

        questLocation = getIntent().getStringExtra(CreateQuestActivity.LOCATION_STRING_INTENT_MESSAGE);
        locationPhotoReference = getIntent().getStringExtra(CreateQuestActivity.PHOTO_REFERENCE_INTENT_MESSAGE);
        questUserProximity = getIntent().getIntExtra(CreateQuestActivity.PROXIMITY_METERS_INTENT_MESSAGE, CreateQuestActivity.METERS_IN_ONE_MILE); // default to 1 mile
        activities = getIntent().getParcelableArrayListExtra(CreateQuestActivity.CONFIRM_ACTIVITIES_INTENT_MESSAGE);

        questLocationImageView = findViewById(R.id.questLocationImageView);
        questLocationTextView = findViewById(R.id.questLocationTextView);
        numberOfActivitiesTextView = findViewById(R.id.questNumberOfActivitesTextView);

        startQuestLaterButton = findViewById(R.id.startQuestLaterButton);
        startQuestLaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Clicked start later button");
                startQuest(false);
            }
        });

        startQuestNowButton = findViewById(R.id.startQuestNowButton);
        startQuestNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Clicked start now button");
                startQuest(true);
            }
        });

        createRecyclerView();
        populateOverviewCard();

        mAuth = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser().getDisplayName();
    }

    private void createRecyclerView() {
        Log.v(TAG, "Creating recycler view for ConfirmQuestActivity");
        recyclerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView = findViewById(R.id.confirmActivityCardRecyclerView);
        recyclerView.setHasFixedSize(true);
        confirmActivityCardAdapter = new ConfirmActivityCardAdapter(activities, apiContext);

        // listener for regenerating specific activities
        RegenerateButtonClickListener regenerateButtonClickListener = new RegenerateButtonClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.v(TAG, "onClick listener in createRecycleView");
                Activity newActivity = activities.get(position).onRegenerateActivityClick(view, position, new GooglePlacesClient(apiContext, questUserProximity, questLocation));
                activities.set(position, newActivity);
                confirmActivityCardAdapter.notifyItemChanged(position);
            }
        };
        confirmActivityCardAdapter.setOnRegenerateButtonClickListener(regenerateButtonClickListener); // before adding the adapter

        recyclerView.setAdapter(confirmActivityCardAdapter);
        recyclerView.setLayoutManager(recyclerLayoutManager);

        // for handling moving and deleting cards
        new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                confirmActivityCardAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Activity deletedActivity = activities.get(position);
                activities.remove(viewHolder.getAdapterPosition());
                confirmActivityCardAdapter.notifyItemRemoved(position);

                Snackbar.make(recyclerView, "Removed " + deletedActivity.gName + " from your quest", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activities.add(position, deletedActivity);
                        confirmActivityCardAdapter.notifyItemInserted(position);
                    }
                }).show();
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
            int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    if (viewHolder instanceof ConfirmActivityCardHolder) {
                        ConfirmActivityCardHolder myViewHolder=
                                (ConfirmActivityCardHolder) viewHolder;
                        confirmActivityCardAdapter.onRowSelected(myViewHolder);
                    }
                }

                super.onSelectedChanged(viewHolder, actionState);
            }
            @Override
            public void clearView(RecyclerView recyclerView,
                    RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);

                if (viewHolder instanceof ConfirmActivityCardHolder) {
                    ConfirmActivityCardHolder myViewHolder=
                            (ConfirmActivityCardHolder) viewHolder;
                    confirmActivityCardAdapter.onRowClear(myViewHolder);
                }

            }
        }).attachToRecyclerView(recyclerView);
    }

    private void populateOverviewCard() {
        int numActivities = activities.size();
        numberOfActivitiesTextView.setText(numActivities + " activities");

        questLocationTextView.setText(questLocation);

        if (locationPhotoReference != null) {
            // quest location image dimensions
            int imgWidth = questLocationImageView.getMaxWidth();
            int imgHeight = questLocationImageView.getMaxHeight();

            GooglePlacesClient client = new GooglePlacesClient(apiContext);
            byte[] placePhotoBytes = client.getPlacePhoto(locationPhotoReference, imgWidth, imgHeight);
            if (placePhotoBytes != null) {
                Log.v(TAG, "Place Photo bytes length: " + placePhotoBytes.length);
                Bitmap bmp = BitmapFactory.decodeByteArray(placePhotoBytes, 0, placePhotoBytes.length);
                questLocationImageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, imgWidth, imgHeight, false));
            }
        }
    }

    private void startQuest(boolean now) {
        // generate code
        String rand = UUID.randomUUID().toString();
        String joinCode = "q"+ rand.substring(rand.length() - 5);

        String datetime = LocalDateTime.now().toString().replace(":", "|");
        String[] datetimeStrings = datetime.split("\\.");
        datetime = datetimeStrings[0];

        // write to Firebase
        CreatingQuest creatingQuest = new CreatingQuest(joinCode, datetime, questLocation, locationPhotoReference, questUserProximity, activities);
        creatingQuest.run();

        Intent intent;
        if (now) {
            intent = new Intent(this, ActiveQuest.class);
            intent.putExtra("joinCode", joinCode);
        } else {
            intent = new Intent(this, MyQuestsActivity.class);
        }
        finish();
        startActivity(intent);
    }

    private class CreatingQuest implements Runnable {
        String joinCode;
        String locationString;
        String locationPhotoReference;
        int proximityInMeters;
        List<Activity> activities;
        String qDatetime;

        CreatingQuest(String joinCode, String qDatetime, String locationString, String locationPhotoReference, int proximityInMeters, List<Activity> activities) {
            this.qDatetime = qDatetime;
            this.joinCode = joinCode;
            this.locationString = locationString;
            this.locationPhotoReference = locationPhotoReference;
            this.proximityInMeters = proximityInMeters;
            this.activities = activities;
        }

        @Override
        public void run() { writeQuestToDb(); }

        private void writeQuestToDb() {
            Log.d(TAG, "Writing Quest to the Database");

            List<String> listOfUsers = new ArrayList<>();

            Quest newQuest = new Quest(true, false, locationString, qDatetime, proximityInMeters, locationPhotoReference, activities, null, 0);

            dr.child("quests").child(joinCode).setValue(newQuest).addOnSuccessListener(task -> {
                Log.d(TAG, String.format("Successfully created quest: %s", joinCode));
            });
            dr.child("quests").child(joinCode).child("users").child(currentUser).setValue(true).addOnFailureListener(task ->
                    dr.child("quests").child(joinCode).removeValue());
        }
    }

}