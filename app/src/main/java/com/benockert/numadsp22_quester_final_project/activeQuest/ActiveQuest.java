package com.benockert.numadsp22_quester_final_project.activeQuest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.benockert.numadsp22_quester_final_project.MainActivity;
import com.benockert.numadsp22_quester_final_project.R;
import com.benockert.numadsp22_quester_final_project.types.Activity;
import com.benockert.numadsp22_quester_final_project.types.Quest;
import com.benockert.numadsp22_quester_final_project.utils.GooglePlacesClient;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.GeoApiContext;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActiveQuest extends AppCompatActivity {
    private static final String TAG = "ACTIVE QUEST";

    private FirebaseAuth mAuth;
    DatabaseReference dr;
    private String apiKey;
    private GeoApiContext apiContext;

    private String currentUser;
    private String currentQuestId;
    private Quest currentQuest;
    private List<Activity> activities;
    private List<Byte[]> activityBytes;
    private Activity currentActivity;
    private Button buttonTakePicture;
    private RatingBar ratingBar;


    private TextView textCurrentStopName;
    private TextView textUserSearchTerm;
    private TextView textCurrentStopAddress;
    private TextView textStopCount;
    private ImageView imageStopImage;
    private ShapeableImageView drawableDirections;

    private Activity previewActivity;
    private int previewActivityIndex;
    private TextView textPreviewStopName;
    private TextView textPreviewStopCount;
    private CardView cardPreviewStop;

    private boolean onLastStop;

    int SELECT_PICTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_quest);

        mAuth = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();

        textCurrentStopName = findViewById(R.id.textCurrentStopName);
        textUserSearchTerm = findViewById(R.id.textUserSearchTerm);
        textCurrentStopAddress = findViewById(R.id.textCurrentStopAddress);
        textStopCount = findViewById(R.id.textStopCount);
        imageStopImage = findViewById(R.id.imageStopImage);
        ratingBar = findViewById(R.id.ratingBar);

        textPreviewStopCount = findViewById(R.id.textNextStopCount);
        textPreviewStopName = findViewById(R.id.textNextStopName);
        cardPreviewStop = findViewById(R.id.cardNextStop);
        drawableDirections = findViewById(R.id.drawableDirections);
        drawableDirections.setOnClickListener(this::openDirections);

        buttonTakePicture = findViewById(R.id.buttonTakePhoto);
        buttonTakePicture.setOnClickListener(this::takePicture);

        // retrieve active quest
        // fill in values
        currentQuestId = "newshi";
        getActiveQuest();

        // get the API key for the Places SDK to use
        try {
            ActivityInfo ai = getPackageManager().getActivityInfo(this.getComponentName(), PackageManager.GET_META_DATA);
            Bundle metaData = ai.metaData;
            apiKey = metaData.get("com.google.android.geo.API_KEY").toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e("MY_QUESTS_ACTIVITY", "API_KEY not found in metadata");
        }

        // create the Google Maps Geo API context
        apiContext = new GeoApiContext.Builder().apiKey(apiKey).build();

        // if no active quest
        // redirect to create quest page

    }

    private void getActiveQuest() {
        dr.child("quests").child(currentQuestId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String result = String.valueOf(task.getResult().getValue());
                Log.d(TAG, String.format("Result: %s", result));
                currentQuest = Quest.getQuestFromJSON(result, currentQuestId);
                if (currentQuest == null) {
                    return;
                }
                activities = currentQuest.activities;
                currentActivity = activities.get(currentQuest.getCurrentActivity());
                previewActivityIndex = currentQuest.getCurrentActivity() + 1;
                if (activities.size() > previewActivityIndex) {
                    previewActivity = activities.get(previewActivityIndex);
                }

                setActivityFields();
            }
        });
    }

    private void setActivityFields() {

        textCurrentStopName.setText(currentActivity.getgName());
        textUserSearchTerm.setText(currentActivity.getuQuery());
        textCurrentStopAddress.setText(currentActivity.getgFormattedAddress());
        ratingBar.setRating(currentActivity.gRating);
        textStopCount.setText(String.format("%s/%s", currentQuest.getCurrentActivity() + 1, activities.size()));
        textPreviewStopCount.setText(String.format("%s/%s", previewActivityIndex + 1, activities.size()));
        textPreviewStopName.setText(previewActivity.getgName());

        int imgWidth = imageStopImage.getWidth();
        int imgHeight = imageStopImage.getHeight();

        GooglePlacesClient client = new GooglePlacesClient(apiContext);
        byte[] placePhotoBytes = client.getPlacePhoto(currentActivity.getgPhotoReference(), imgWidth, imgHeight);

        Bitmap bmp = BitmapFactory.decodeByteArray(placePhotoBytes, 0, placePhotoBytes.length);
        imageStopImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, imgWidth, imgHeight, false));

    }


    public void goToNextStop(View view) {

        dr.child("quests").child(currentQuestId).child("currentActivity").setValue(currentQuest.currentActivity + 1).addOnCompleteListener(task -> {
            currentQuest.currentActivity += 1;
            currentActivity = activities.get(currentQuest.currentActivity);
            previewActivityIndex = currentQuest.currentActivity + 1;
            setActivityFields();
        });

        if (currentQuest.currentActivity + 1 == activities.size()) {
            findViewById(R.id.buttonNextStop).setVisibility(View.INVISIBLE);
        }
    }


    public void openDirections(View view) {
        Uri mapUri = Uri.parse(String.format("google.navigation:q=%s,+%s", currentActivity.gFormattedAddress.replace(" ", "+"), currentQuest.location.replace(" ", "+")));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void takePicture(View view) {
        // Request for camera runtime permission
        if (ContextCompat.checkSelfPermission(ActiveQuest.this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActiveQuest.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        } else {
            Intent intent = new Intent((MediaStore.ACTION_IMAGE_CAPTURE));
            startActivity(intent);
        }
    }


    public void cancelQuest() {
        dr.child("quests").child(currentQuestId).child("users").child(currentUser).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(this, MainActivity.class).putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });

    }

    public void finishQuest() {
        dr.child("quests").child(currentQuestId).child("completed").setValue(true);
        dr.child("quests").child(currentQuestId).child("active").setValue(false);
        Intent intent = new Intent(this, MainActivity.class).putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    public void saveAndExitToMenu() {
        Intent intent = new Intent(this, MainActivity.class).putExtra("currentUser", currentUser);
        startActivity(intent);
    }


    public String generateJoinCode() {
        String rand = UUID.randomUUID().toString();
        rand = rand.substring(rand.length() - 6);
        return rand;
//        AtomicBoolean sendIt = new AtomicBoolean(false);
//        dr.child("quests").child(rand).get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                String result = String.valueOf(task.getResult().getValue());
//                if (result.equals("null")) {
//                    // quest does not already exist
//                    sendIt.set(true);
//                }
//            }
//        });
//        if (sendIt.get()) {
//            return rand;
//        } else {
//            return generateJoinCode();
//        }
    }


}