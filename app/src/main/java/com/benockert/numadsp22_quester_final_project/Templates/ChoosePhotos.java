package com.benockert.numadsp22_quester_final_project.Templates;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.benockert.numadsp22_quester_final_project.PhotoRecap.ViewRecap;
import com.benockert.numadsp22_quester_final_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        ColorDrawable drawable = (ColorDrawable) findViewById(R.id.finalImageView).getBackground();
        backgroundColor = drawable.getColor();

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
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        img1.launch(i);
    }

    public void selectImg2(View v) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        img2.launch(i);
    }

    public void selectImg3(View v) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
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
        //TODO: allow user to change border of boxes
        //potential way to change the box border
        //                StateListDrawable d = (StateListDrawable) ContextCompat.getDrawable(v.getContext(), R.drawable.box);
////                assert d != null;
////                d.set
////                d.setStroke(2, color);
//
//        StateListDrawable gradientDrawable = (StateListDrawable) ContextCompat.getDrawable(v.getContext(), R.drawable.box);
//        assert gradientDrawable != null;
//        DrawableContainer.DrawableContainerState drawableContainerState = (DrawableContainer.DrawableContainerState) gradientDrawable.getConstantState();
//        Drawable[] children = drawableContainerState.getChildren();
//        GradientDrawable selectedItem = (GradientDrawable) children[0];
////                GradientDrawable selectedDrawable = (GradientDrawable) selectedItem.getDrawable(0);
//        selectedItem.setStroke(2, color);
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

        //TODO:save image URI to db
        dr.child("users").child(userName).child("recaps")
                .child(recapName).child("img1Uri").setValue(img1Uri.toString());
        dr.child("users").child(userName).child("recaps")
                .child(recapName).child("img2Uri").setValue(img2Uri.toString());
        dr.child("users").child(userName).child("recaps")
                .child(recapName).child("img3Uri").setValue(img3Uri.toString());
        dr.child("users").child(userName).child("recaps")
                .child(recapName).child("backgroundColor").setValue(backgroundColor);
    }

    public void finish(View v) {
        saveToFirebase();
        Intent i = new Intent(this, ViewRecap.class);
        i.putExtra("recapName", recapName);
        startActivity(i);
    }

    public void back(View v) {
        finish();
    }
}

