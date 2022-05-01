package com.benockert.numadsp22_quester_final_project.activeQuest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ActiveQuest extends AppCompatActivity {
    private static final String TAG = "ACTIVE_QUEST_ACTIVITY";

    private FirebaseAuth mAuth;
    DatabaseReference dr;
    private String apiKey;
    private GeoApiContext apiContext;

    private String currentUser;
    private String currentQuestId;
    private Quest currentQuest;
    private List<Activity> activities;
    private Activity currentActivity;
    private Button buttonTakePicture;
    private RatingBar ratingBar;


    private TextView textCurrentStopName;
    private TextView textUserSearchTerm;
    private TextView textCurrentStopAddress;
    private TextView textCurrentPriceLevel;
    private TextView textStopCount;
    private ImageView imageStopImage;
    private ShapeableImageView drawableDirections;

    private Activity previewActivity;
    private int previewActivityIndex;
    private TextView textPreviewStopName;
    private TextView textPreviewUserSearchTerm;
    private TextView textPreviewStopCount;
    private CardView cardPreviewStop;

    ActivityResultLauncher<Intent> activityResultLauncher;
    private String mCurrentPhotoPath;

    private boolean onLastStop;

    int SELECT_PICTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_quest);

        mAuth = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser().getDisplayName();

        textCurrentStopName = findViewById(R.id.textCurrentStopName);
        textUserSearchTerm = findViewById(R.id.textUserSearchTerm);
        textCurrentStopAddress = findViewById(R.id.textCurrentStopAddress);
        textCurrentPriceLevel = findViewById(R.id.textPriceLevel);
        textStopCount = findViewById(R.id.textStopCount);
        imageStopImage = findViewById(R.id.imageStopImage);
        ratingBar = findViewById(R.id.ratingBar);

        textPreviewStopCount = findViewById(R.id.textNextStopCount);
        textPreviewStopName = findViewById(R.id.textNextStopName);
        textPreviewUserSearchTerm = findViewById(R.id.textPreviewUserSearchTerm);
        cardPreviewStop = findViewById(R.id.cardNextStop);
        drawableDirections = findViewById(R.id.drawableDirections);
        drawableDirections.setOnClickListener(this::openDirections);

        buttonTakePicture = findViewById(R.id.buttonTakePhoto);
        buttonTakePicture.setOnClickListener(this::takePicture);
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Log.d(TAG, "Got to activity result for take picture");

                }

            }
        });

        // retrieve active quest
        currentQuestId = this.getIntent().getExtras().get("joinCode").toString();
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
                currentQuest = Quest.getQuestFromJSON(currentQuestId, result);
                if (currentQuest == null) {
                    return;
                }
                activities = currentQuest.activities;
                currentActivity = activities.get(currentQuest.getCurrentActivity());
                previewActivityIndex = currentQuest.getCurrentActivity() + 1;
                if (previewActivityIndex == activities.size()) {
                    onLastStop = true;
                } else if (activities.size() > previewActivityIndex) {
                    previewActivity = activities.get(previewActivityIndex);
                }
                setActivityFields();
            }
        });
    }

    private void setActivityFields() {

        textCurrentStopName.setText(currentActivity.getgName());
        String setText = "\""+currentActivity.getuQuery()+"\"";
        textUserSearchTerm.setText(setText);
        StringBuilder str_bfr = new StringBuilder();
        for(int i=0;i<currentActivity.getuPriceLevel();i++) { str_bfr.append("$"); }
        setText = str_bfr.toString();
        textCurrentPriceLevel.setText(setText);
        textCurrentStopAddress.setText(currentActivity.getgFormattedAddress());
        ratingBar.setRating(Float.parseFloat(String.format("%.1f", currentActivity.getgRating())));
        Log.d(TAG, String.valueOf(ratingBar.getRating()));
        textStopCount.setText(String.format("%s/%s", currentQuest.getCurrentActivity() + 1, activities.size()));
        if (!onLastStop) {
            textPreviewStopCount.setText(String.format("%s/%s", previewActivityIndex + 1, activities.size()));
            textPreviewStopName.setText(previewActivity.getgName());
            setText = "\""+previewActivity.getuQuery()+"\"";
            textPreviewUserSearchTerm.setText(setText);
        }
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
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }
        else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
             try {
                    File imageFile = createImageFile();
                    Log.i(TAG, imageFile.getAbsolutePath());

                    Uri imageURI = FileProvider.getUriForFile(this, "com.benockert.numadsp22_quester_final_project.fileprovider", imageFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                    activityResultLauncher.launch(intent);

                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Log.i(TAG, timeStamp);
        String imageFileName = "JPEG_" + currentQuest.joinCode + "_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName
                , ".jpg"
                , storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;

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
}