package com.benockert.numadsp22_quester_final_project.Templates;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.benockert.numadsp22_quester_final_project.PhotoRecap.ViewRecap;
import com.benockert.numadsp22_quester_final_project.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recapName = this.getIntent().getStringExtra("recapName");
        template = this.getIntent().getStringExtra("chosenTemplateName");

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
                        ImageView imageView = findViewById(R.id.imageView1);
                        imageView.setImageURI(img1Uri);
                        findViewById(R.id.selectImg1).setVisibility(View.INVISIBLE);
                        Log.i("im1Uri", img1Uri.toString());
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
                        ImageView imageView = findViewById(R.id.imageView2);
                        imageView.setImageURI(img2Uri);
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
                        ImageView imageView = findViewById(R.id.imageView3);
                        imageView.setImageURI(img3Uri);
                        findViewById(R.id.selectImg3).setVisibility(View.INVISIBLE);
                    }
                });
    }

    public void selectImg1(View v) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        i.setType("image/*");
        img1.launch(i);
    }

    public void selectImg2(View v) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        i.setType("image/*");
        img2.launch(i);
    }

    public void selectImg3(View v) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        i.setType("image/*");
        img3.launch(i);
    }

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

    public void saveToFirebase() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();
        assert userName != null;
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
        String dateGenerated = String.valueOf(LocalDate.now());

        dr.child("users").child(userName).child("recaps")
                .child(recapName).child("template").setValue(template.replaceAll(" ", ""));

        dr.child("users").child(userName).child("recaps")
                .child(recapName).child("dateGenerated").setValue(dateGenerated);

        //TODO:save image.jpg to db
    }

    private void grabAndSaveScreenshot() {
        String username = Objects.requireNonNull(FirebaseAuth.getInstance()
                .getCurrentUser()).getDisplayName();
        String name = recapName.replaceAll("//|", "_");

        View view = findViewById(R.id.viewRecapLayout);
        //create screenshot of recap
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        // Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        //reference to where the screenshot will be saves
        StorageReference recapRef = storageRef.child(username + "/" + name + ".JPEG");

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

    public void finish(View v) {
        saveToFirebase();
        grabAndSaveScreenshot();
        Intent i = new Intent(this, ViewRecap.class);
        i.putExtra("recapName", recapName);
        startActivity(i);
    }

    public void back(View v) {
        finish();
    }
}

