package com.benockert.numadsp22_quester_final_project.Templates;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.benockert.numadsp22_quester_final_project.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.util.Objects;

public class ChooseTemplate extends AppCompatActivity {
    String chosenTemplate;
    String recapName;
    DatabaseReference dr;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_template);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        dr = FirebaseDatabase.getInstance().getReference();
        recapName = this.getIntent().getStringExtra("recapName");

        ImageView templates = findViewById(R.id.templateImage);

        templates.setVisibility(View.INVISIBLE);

        Spinner spinner = findViewById(R.id.temp_dropdown);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.templates_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        TextView nothingSelected = findViewById(R.id.noTemplateSelecLbl);
        //nothingSelected.setVisibility(View.INVISIBLE);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                //nothingSelected.setVisibility(View.INVISIBLE);
                Object item = adapterView.getItemAtPosition(pos);
                chosenTemplate = item.toString();
                switch (chosenTemplate) {
                    case "Select Template":
                        nothingSelected.setVisibility(View.VISIBLE);
                        break;
                    case "Triangle 1":
                        templates.setImageResource(R.drawable.triangle_template);
                        templates.setVisibility(View.VISIBLE);
                        nothingSelected.setVisibility(View.INVISIBLE);
                        break;
                    case "Triangle 2":
                        templates.setImageResource(R.drawable.triangle_template2);
                        templates.setVisibility(View.VISIBLE);
                        nothingSelected.setVisibility(View.INVISIBLE);
                        break;
                    case "Triangle 3":
                        templates.setImageResource(R.drawable.triangle_template3);
                        templates.setVisibility(View.VISIBLE);
                        nothingSelected.setVisibility(View.INVISIBLE);
                        break;
                    case "Triangle 4":
                        templates.setImageResource(R.drawable.triangle_template4);
                        templates.setVisibility(View.VISIBLE);
                        nothingSelected.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        templates.setVisibility(View.INVISIBLE);
                        nothingSelected.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i("nothingSelected", "nothingSelected");
            }
        });
    }

    public void choosePhotos(View v) {
        if (!chosenTemplate.equals("Select Template")){
            Intent i = new Intent(this, ChoosePhotos.class);
            i.putExtra("recapName", recapName);
            i.putExtra("chosenTemplateName", chosenTemplate.replaceAll(" ", ""));
            startActivity(i);
        } else {
            Snackbar.make(v, "Select A Template", BaseTransientBottomBar.LENGTH_LONG).show();
        }
    }
}