package com.benockert.numadsp22_quester_final_project.PhotoRecap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
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

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class ViewRecap extends AppCompatActivity {
    String questRecapName;
    Toolbar toolbar;
    String username;
    DatabaseReference dr;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recap);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();
        questRecapName = this.getIntent().getExtras().getString("recapName");
        username = Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();

        title = findViewById(R.id.toolbar_title);
        title.setText(questRecapName);

        // Reference to an image file in Cloud Storage
        String recapName = questRecapName.replaceAll("\\|", "_");
        Log.i("name", recapName);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference pathReference = storageReference.child(username + "/" + recapName + ".jpg");

        // ImageView in your Activity
        ImageView imageView = findViewById(R.id.finalImageView);
        //grab recap from storage and display it
        final long ONE_MEGABYTE = 1024 * 1024;
        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView.setImageBitmap(bm);
            setToolbar(bm);
        });

        //TODO: share image/fetch image with URI
    }

    private void setToolbar(Bitmap bm) {
        toolbar = findViewById(R.id.viewRecapToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_share_24);

        toolbar.setNavigationOnClickListener(view -> {
            Log.i("success", "success");
            Intent shareIntent = new Intent();

            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("image/*");

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, b);
            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bm, null, null);
            Uri uri = Uri.parse(path);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(shareIntent, null));
        });
    }

    public void delete(View v) {
        dr.child("users").child(username).child("recaps").child(questRecapName).removeValue();
        Intent i = new Intent(this, ViewAllRecaps.class);
        startActivity(i);
    }
}