package com.benockert.numadsp22_quester_final_project.PhotoRecap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.benockert.numadsp22_quester_final_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Scanner;

public class ViewRecap extends AppCompatActivity {
    String questRecapName;
    FirebaseStorage storage;
    String username;
    DatabaseReference dr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recap);
        questRecapName = this.getIntent().getExtras().getString("recapName");
        storage = FirebaseStorage.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();

        username = Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();
        Toolbar toolbar = findViewById(R.id.viewRecapToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_share_24);

        TextView title = findViewById(R.id.toolbar_title);
        title.setText(questRecapName);

        toolbar.setNavigationOnClickListener(view -> {
            Log.i("success", "success");
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            // shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
            shareIntent.setType("image/jpg");
            startActivity(Intent.createChooser(shareIntent, null));
        });

        retrieveRecap();
    }

    private void retrieveRecap() {
        //        retrieves and displays recap that was generated or saved

        // ImageView in your Activity
        ImageView imageView = findViewById(R.id.viewRecapImg);
        // Reference to an image file in Cloud Storage
        StorageReference storageRef = storage.getReference();
        String imgRecapName = questRecapName.replaceAll("\\|", "_");
        String path = username + "/" + imgRecapName + ".jpg";
        StorageReference pathReference = storageRef.child(path);

        final long ONE_MEGABYTE = 1024 * 1024;
        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {

            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView.setImageBitmap(bitmap);

        }).addOnFailureListener(exception -> {
            // Handle any errors
        });
    }

    public void delete(View v) {

      //  DatabaseReference df = dr.re

        dr.child("users").child(username).child("recaps").child(questRecapName).removeValue();

        Log.i("dtae", String.valueOf(LocalDate.now()));

        Intent i = new Intent(this, ViewAllRecaps.class);
        startActivity(i);
    }

    /**
     * converts the String to a parsable json string
     *
     * @param s String of information gotten from the get request
     * @return String of json info to parse through
     */
    private String convertStingToJson(String s) {
        Scanner sc = new Scanner(s).useDelimiter("\\A");
        return sc.hasNext() ? sc.next().replace(", ", ",\n") : "";
    }
}