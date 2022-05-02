package com.benockert.numadsp22_quester_final_project.activeQuest;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.benockert.numadsp22_quester_final_project.R;
import com.benockert.numadsp22_quester_final_project.activeQuest.previewStopCard.PreviewCardAdapter;
import com.benockert.numadsp22_quester_final_project.myQuests.MyQuestsActivity;
import com.benockert.numadsp22_quester_final_project.types.Activity;
import com.benockert.numadsp22_quester_final_project.types.Quest;
import com.benockert.numadsp22_quester_final_project.utils.GooglePlacesClient;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.GeoApiContext;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ActiveQuest extends AppCompatActivity {
    private static final String TAG = "ACTIVE_QUEST_ACTIVITY";

    private FirebaseAuth mAuth;
    DatabaseReference dr;
    private String apiKey;
    private GeoApiContext apiContext;

    private String currentUser;
    private String currentQuestId;
    private Quest currentQuest;
    private ArrayList<Activity> activityArrayList;
    private Activity currentActivity;
    private Button buttonTakePicture;
    private Button buttonExitButton;
    Button cancelQuest;
    Button saveAndExit;
    Button finishQuest;
    private Button buttonNextStop;
    private RatingBar ratingBar;

    private RecyclerView recyclerView;
    private PreviewCardAdapter previewCardAdapter;
    private LinearLayoutManager recyclerLayoutManager;

    private TextView textJoinCode;
    private TextView textCurrentStopName;
    private TextView textUserSearchTerm;
    private TextView textCurrentStopAddress;
    private TextView textCurrentPriceLevel;
    private TextView textStopCount;
    private ImageView imageStopImage;
    private ShapeableImageView drawableDirections;
    SnapHelper snapHelper;

    String mCurrentPhotoPath;

    ActivityResultLauncher<Intent> activityResultLauncher;

    private boolean onLastStop = false;

    int SELECT_PICTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_quest);

        mAuth = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser().getDisplayName();

        activityArrayList = new ArrayList<Activity>();
        textJoinCode = findViewById(R.id.textJoinCode);
        textCurrentStopName = findViewById(R.id.textCurrentStopName);
        textUserSearchTerm = findViewById(R.id.textUserSearchTerm);
        textCurrentStopAddress = findViewById(R.id.textCurrentStopAddress);
        textCurrentPriceLevel = findViewById(R.id.textPriceLevel);
        textStopCount = findViewById(R.id.textStopCount);
        imageStopImage = findViewById(R.id.imageStopImage);
        ratingBar = findViewById(R.id.ratingBar);

        createRecyclerView();

        drawableDirections = findViewById(R.id.drawableDirections);
        drawableDirections.setOnClickListener(this::openDirections);

        buttonNextStop = findViewById(R.id.buttonNextStop);
        buttonNextStop.setOnClickListener(this::clickNextStop);

        buttonExitButton = findViewById(R.id.buttonExitButton);
        buttonExitButton.setOnClickListener(this::openExitMenu);

        buttonTakePicture = findViewById(R.id.buttonTakePhoto);
        buttonTakePicture.setOnClickListener(this::takePicture);
        Context cont = this;
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Log.d(TAG, "Got to activity result for take picture");
                        InputStream inputStream = null;
                        try {
                            inputStream = cont.getContentResolver().openInputStream(Uri.parse(String.valueOf(result.getData())));
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                            Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

                            MediaStore.Images.Media.insertImage(
                                    getContentResolver(),
                                    bmp,
                                    null,
                                    null
                            );
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                });

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

        // retrieve active quest
        currentQuestId = this.getIntent().getExtras().get("joinCode").toString();
        getActiveQuest();
        previewCardAdapter.notifyDataSetChanged();

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
                activityArrayList = new ArrayList<Activity>(currentQuest.activities);
                currentActivity = activityArrayList.get(currentQuest.getCurrentActivity());
                previewCardAdapter = new PreviewCardAdapter(activityArrayList, currentQuest.getCurrentActivity());
                if (currentQuest.currentActivity + 1 == activityArrayList.size()) {
                    onLastStop = true;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        previewCardAdapter.notifyDataSetChanged();
                    }
                });
                previewCardAdapter = new PreviewCardAdapter(activityArrayList, currentQuest.getCurrentActivity());
                recyclerView.setAdapter(previewCardAdapter);
                Log.d(TAG + "POST NOTIFY", activityArrayList.toString());
                Log.d(TAG + " AADDDDDDDDDDDD", String.valueOf(activityArrayList.size()));
                Log.d(TAG, String.valueOf(previewCardAdapter.getItemCount()));
                populateCurrentActivityFields();
            }
        });
    }

    private void createRecyclerView() {
        Log.v(TAG, "Creating recycler view for ActiveQuest Activity");
        recyclerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = findViewById(R.id.previewCardRecycler);
        recyclerView.setHasFixedSize(true);
        int currentPosition=0;
        if (currentQuest != null){
            currentPosition = currentQuest.getCurrentActivity();
        }
        previewCardAdapter = new PreviewCardAdapter(activityArrayList, currentPosition);
        recyclerView.setAdapter(previewCardAdapter);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        Log.d(TAG + "IN RECYCLER", activityArrayList.toString());
        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    private void populateCurrentActivityFields() {
        textJoinCode.setText(currentQuest.getJoinCode());
        textCurrentStopName.setText(currentActivity.getgName());
        textUserSearchTerm.setText(currentActivity.getuQuery().toUpperCase());
        StringBuilder str_bfr = new StringBuilder();
        for (int i = 0; i < currentActivity.getuPriceLevel(); i++) {
            str_bfr.append("$");
        }
        String setText = str_bfr.toString();
        textCurrentPriceLevel.setText(setText);
        textCurrentStopAddress.setText(currentActivity.getgFormattedAddress());
        ratingBar.setRating(Float.parseFloat(String.format("%.1f", currentActivity.getgRating())));
        Log.d(TAG, String.valueOf(ratingBar.getRating()));
        textStopCount.setText(String.format("%s/%s", currentQuest.getCurrentActivity() + 1, activityArrayList.size()));

        int imgWidth = imageStopImage.getWidth();
        int imgHeight = imageStopImage.getHeight();

        GooglePlacesClient client = new GooglePlacesClient(apiContext);
        byte[] placePhotoBytes = client.getPlacePhoto(currentActivity.getgPhotoReference(), imgWidth, imgHeight);

        Bitmap bmp = BitmapFactory.decodeByteArray(placePhotoBytes, 0, placePhotoBytes.length);
        imageStopImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, imgWidth, imgHeight, false));
        previewCardAdapter.setCurrentPosition(currentQuest.getCurrentActivity());
        previewCardAdapter.notifyDataSetChanged();
        Log.d(TAG, String.valueOf(previewCardAdapter.getItemCount()));


        if (currentQuest != null){
            Log.d(TAG + " SNAP HELPER", String.valueOf(currentQuest.currentActivity + 1));
            recyclerLayoutManager.scrollToPosition(currentQuest.currentActivity + 1);
        }

        if (onLastStop) {
            buttonNextStop.setText(R.string.quest_finish_button);
        }
    }


    public void clickNextStop(View view) {
        if (!onLastStop) {
            dr.child("quests").child(currentQuestId).child("currentActivity").setValue(currentQuest.currentActivity + 1).addOnCompleteListener(task -> {
                currentQuest.currentActivity += 1;
                currentActivity = activityArrayList.get(currentQuest.currentActivity);
                if (currentQuest.currentActivity + 1 == activityArrayList.size()) {
                    onLastStop = true;
                }
                populateCurrentActivityFields();
            });
        } else {
            finishQuest();
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
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
//
//                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                File f = new File("com.benockert.numadsp22_quester_final_project.fileprovider");
//                Uri contentUri = Uri.fromFile(f);
//                mediaScanIntent.setData(contentUri);
//               // this.sendBroadcast(mediaScanIntent);

                File imageFile = createImageFile();
                Log.i(TAG, imageFile.getAbsolutePath());

                Uri imageURI = FileProvider.getUriForFile(this,
                        "com.benockert.numadsp22_quester_final_project.fileprovider", imageFile);
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

    private void openExitMenu(View view) {
        Context context = view.getContext();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.active_quest_exit_menu);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        cancelQuest = dialog.findViewById(R.id.buttonCancelQuest);
        saveAndExit = dialog.findViewById(R.id.buttonSaveAndExit);
        finishQuest = dialog.findViewById(R.id.buttonFinishQuest);

        cancelQuest.setOnClickListener(view1 -> {
            cancelQuest();
            sendMessage("Quest cancelled!", view);
        });
        saveAndExit.setOnClickListener(view1 -> {
            saveAndExitToMenu();
            sendMessage("Quest saved!", view);
        });
        finishQuest.setOnClickListener(view1 -> {
            finishQuest();
            sendMessage("Quest completed!", view);
        });
//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        int dialogWidth = (int)(displayMetrics.widthPixels * 0.4);
//        int dialogHeight = (int)(displayMetrics.heightPixels * 0.6);
//        dialog.getWindow().setLayout(dialogWidth, dialogHeight);

        dialog.show();
    }


    public void cancelQuest() {
        dr.child("quests").child(currentQuestId).child("users").child(currentUser).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(this, MyQuestsActivity.class).putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });
    }

    public void finishQuest() {
        dr.child("quests").child(currentQuestId).child("completed").setValue(true);
        dr.child("quests").child(currentQuestId).child("active").setValue(false);
        Intent intent = new Intent(this, MyQuestsActivity.class).putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    public void saveAndExitToMenu() {
        Intent intent = new Intent(this, MyQuestsActivity.class).putExtra("currentUser", currentUser);
        startActivity(intent);
    }

    public void sendMessage(String message, View view) {
        Toast.makeText(view.getContext(), message, Toast.LENGTH_LONG).show();
    }

}