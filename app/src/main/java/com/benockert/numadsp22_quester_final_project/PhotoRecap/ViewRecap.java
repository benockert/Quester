package com.benockert.numadsp22_quester_final_project.PhotoRecap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.benockert.numadsp22_quester_final_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

public class ViewRecap extends AppCompatActivity {
    String questRecapName;
    Toolbar toolbar;
    String username;
    DatabaseReference dr;
    TextView title;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recap);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();
        questRecapName = this.getIntent().getExtras().getString("recapName");
        username = Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();
        userId = this.getIntent().getExtras().getInt("userId");

        //sets the title of the recap
        title = findViewById(R.id.toolbar_title);
        title.setText(questRecapName);

        // Reference to an image file in Cloud Storage
        String recapName = questRecapName.replaceAll("\\|", "_");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String path = userId + "/" + recapName + ".JPEG";
        StorageReference pathReference = storageReference.child(path);

        // ImageView in your Activity
        ImageView imageView = findViewById(R.id.finalImageView);
        //grab recap from storage and display it
        final long ONE_MEGABYTE = 1024 * 1024;
        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView.setImageBitmap(bm);
            setToolbar(bm);
        });
    }

    /**
     * sets the on click dunction of the share button located in the toolbar
     *
     * @param bm BitMap representing the image to share
     */
    private void setToolbar(Bitmap bm) {
        toolbar = findViewById(R.id.viewRecapToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_share_24);

        toolbar.setNavigationOnClickListener(view -> {
            try {
                File cachePath = new File(this.getCacheDir(), "images");
                // overwrites this image every time
                FileOutputStream stream = new FileOutputStream(new File(cachePath, "image.JPEG"));
                bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                stream.close();
            } catch (Exception e) {
                Log.i("error", e.getMessage());
            }
            File imagePath = new File(this.getCacheDir(), "images");
            File newFile = new File(imagePath, "image.JPEG");
            Uri contentUri =
                    FileProvider.getUriForFile(this,
                            "com.benockert.numadsp22_quester_final_project" +
                                    ".PhotoRecap.ViewRecap", newFile);

            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                // temp permission for receiving app to read this file
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                startActivity(Intent.createChooser(shareIntent, "Save Or Share Image"));
            }
        });
    }

    /**
     * on click allow user to delete their recap and re-directs them to the view all recaps page
     *
     * @param v View current view
     */
    public void delete(View v) {
        dr.child("users").child(username).child("recaps").child(questRecapName).removeValue();
        Intent i = new Intent(this, ViewAllRecaps.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
    }

    /**
     * on click method to navigate the user to the view all recaps page
     *
     * @param v View current view
     */
    public void toViewAllRecaps(View v) {
        Intent i = new Intent(this, ViewAllRecaps.class);
        startActivity(i);
    }
}