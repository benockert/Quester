package com.benockert.numadsp22_quester_final_project.Templates;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.benockert.numadsp22_quester_final_project.PhotoRecap.ViewRecap;
import com.benockert.numadsp22_quester_final_project.R;

public class ChoosePhotos extends AppCompatActivity {
    String recapName;
    String template;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recapName = this.getIntent().getStringExtra("recapName");
        template = this.getIntent().getStringExtra("chosenTemplateName");

        switch (template) {
            case "Triangle 1":
                setContentView(R.layout.triangle1_choose);
                break;
            case "Triangle 2":
                setContentView(R.layout.triangle2_choose);
                break;
            case "Triangle 3":
                setContentView(R.layout.triangle3_choose);
                break;
            case "Triangle 4":
                setContentView(R.layout.triangle4_choose);
                break;
        }

//        // create an instance of the
//        // intent of the type image
//        Intent i = new Intent();
//        i.setType("image/*");
//        i.setAction(Intent.ACTION_GET_CONTENT);
//
//        // pass the constant to compare it
//        // with the returned requestCode
//        startActivityForResult(Intent.createChooser(i, "Select Picture"), 5);
    }

    public void SelectImg1(View v) {

        registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        Uri uri = data.getData();
                        ImageView imageView = findViewById(R.id.imageView);
                        imageView.setImageURI(uri);
                    }
                });

        // create an instance of the
        // intent of the type image
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
    }

    public void SelectImg2(View v) {

    }

    public void SelectImg3(View v) {

    }

//        // Create a storage reference from our app
//        StorageReference storageRef = storage.getReference();
//
//// Create a reference to "mountains.jpg"
//        StorageReference mountainsRef = storageRef.child("mountains.jpg");
//
//// Create a reference to 'images/mountains.jpg'
//        StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");
//
//// While the file names are the same, the references point to different files
//        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
//        mountainsRef.getPath().equals(mountainImagesRef.getPath());


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
//            if (data == null) {
//                //Display an error
//                return;
//            }
//            InputStream inputStream = context.getContentResolver().openInputStream(data.getData());
//            //Now you can do whatever you want with your input stream, save it as file, upload to a server, decode a bitmap...
//        }
//
//        if (template.equals("grid 1") || template.equals("grid 2") || template.equals("triangle")) {
//
//        }
//    }

    public ImageView retrieveImage() {
        return null;
    }

    public void saveToFirebase() {

    }

    public void finish(View v) {
        //TODO: save recap and image in database
        saveToFirebase();

        Intent i = new Intent(this, ViewRecap.class);
        i.putExtra("recapName", recapName);
        startActivity(i);
    }
}

