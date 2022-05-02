package com.benockert.numadsp22_quester_final_project.CreateRecap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.benockert.numadsp22_quester_final_project.PhotoRecap.ViewRecap;
import com.benockert.numadsp22_quester_final_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Objects;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ChoosePhotos extends AppCompatActivity {
    String recapName;
    String template;
    ActivityResultLauncher<Intent> img1;
    ActivityResultLauncher<Intent> img2;
    ActivityResultLauncher<Intent> img3;
    Uri img1Uri;
    Uri img2Uri;
    Uri img3Uri;
    int backgroundColor;
    String userId;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recapName = this.getIntent().getStringExtra("recapName");
        template = this.getIntent().getStringExtra("chosenTemplateName");
        userId = this.getIntent().getExtras().getString("userId");
        findViewById(R.id.generatingRecap).setVisibility(View.INVISIBLE);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /*
         * switch statement to display the template that was chosen in the choose template activity
         * so that users may add photos to it
         */
        switch (template) {
            case "Triangle1":
                setContentView(R.layout.triangle1_choose);
                break;
            case "Triangle2":
                setContentView(R.layout.triangle2_choose);
                break;
            case "Triangle3":
                setContentView(R.layout.triangle3_choose);
                break;
            case "Triangle4":
                setContentView(R.layout.triangle4_choose);
                break;
        }

        img1 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        img1Uri = data.getData();
                        ImageView imgV1 = findViewById(R.id.imageView1);
                        imgV1.setImageURI(img1Uri);
                        findViewById(R.id.selectImg1).setVisibility(View.INVISIBLE);
                    }
                });
        img2 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        img2Uri = data.getData();
                        ImageView imgV2 = findViewById(R.id.imageView2);
                        imgV2.setImageURI(img2Uri);
                        findViewById(R.id.selectImg2).setVisibility(View.INVISIBLE);
                    }
                });
        img3 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        img3Uri = data.getData();
                        ImageView imgV3 = findViewById(R.id.imageView3);
                        imgV3.setImageURI(img3Uri);
                        findViewById(R.id.selectImg3).setVisibility(View.INVISIBLE);
                    }
                });
    }

    /**
     * allows for user to select an image from their gallery to populate their chosen template
     *
     * @param v the current view
     */
    public void selectImg1(View v) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        i.setType("image/*");
        img1.launch(i);
    }

    /**
     * allows for user to select an image from their gallery to populate their chosen template
     *
     * @param v the current view
     */
    public void selectImg2(View v) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        i.setType("image/*");
        img2.launch(i);
    }

    /**
     * allows for user to select an image from their gallery to populate their chosen template
     *
     * @param v the current view
     */
    public void selectImg3(View v) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        i.setType("image/*");
        img3.launch(i);
    }

    /**
     * allows for user to change the background color of their chosen template
     *
     * @param v the current view
     */
    public void changeBackgroundColor(View v) {
        AmbilWarnaDialog x = new AmbilWarnaDialog(v.getContext(), Color.BLACK, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                findViewById(R.id.finalImageView).setBackgroundColor(color);
                backgroundColor = color;
            }
        });
        x.show();
    }

    /**
     * helper so when the user presses finish to view their recap
     * the recap name and date generated is saved to the firebase database
     */
    public void saveToFirebase() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();
        assert userName != null;
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
        String dateGenerated = String.valueOf(LocalDate.now());
        dr.child("users").child(userName).child("recaps")
                .child(recapName).child("dateGenerated").setValue(dateGenerated);
    }

    /**
     * creates a screenshot of the template view the user created
     * saves the screenshot created to firebase storage
     */
    private void createAndSaveScreenshot() {
        String name = recapName.replaceAll("\\|", "_");
        View view = findViewById(R.id.L2);
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        // Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        //reference to where the screenshot will be saves
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
        String userName = mAuth.getCurrentUser().getDisplayName();

        StorageReference recapRef = storageRef.child(this.userId + "/" + name + ".JPEG");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        //uploading image to database
        UploadTask uploadTask = recapRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
            Log.i("success", "image upload success");
        });
    }


    /**
     * on click method called when the user is done selecting photos/changing the background
     * of their chosen template
     *
     * @param v the current view
     */
    public void finish(View v) {
        if (findViewById(R.id.imageView1).getVisibility() == View.VISIBLE
                || findViewById(R.id.imageView2).getVisibility() == View.VISIBLE
                || findViewById(R.id.imageView3).getVisibility() == View.VISIBLE) {
            Snackbar.make(v, "Please Select Three Photos",
                    BaseTransientBottomBar.LENGTH_LONG).show();
        } else {
            findViewById(R.id.generatingRecap).setVisibility(View.VISIBLE);
            saveToFirebase();
            createAndSaveScreenshot();
            Intent i = new Intent(this, ViewRecap.class);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i("recapName1", recapName);
            i.putExtra("recapName", this.recapName);
            i.putExtra("userId", this.userId);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(i);
        }
    }

    /**
     * brings the user back to the select template activity
     *
     * @param v the current view
     */
    public void back(View v) {
        finish();
    }
}

