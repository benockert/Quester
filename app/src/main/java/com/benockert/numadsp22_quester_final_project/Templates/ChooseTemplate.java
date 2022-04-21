package com.benockert.numadsp22_quester_final_project.Templates;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class ChooseTemplate extends AppCompatActivity {
    String chosenTemplate;
    String recapName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_template);

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
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userName = Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();
        assert userName != null;
        String dateGenerated = String.valueOf(LocalDate.now());

        dr.child("users").child(userName).child("recaps")
                .child(recapName).child("template").setValue(chosenTemplate);

        dr.child("users").child(userName).child("recaps")
                .child(recapName).child("dateGenerated").setValue(dateGenerated);

        Intent i = new Intent(this, ChoosePhotos.class);
        i.putExtra("chosenTemplateName", chosenTemplate);
        i.putExtra("recapName", recapName);
        startActivity(i);
    }
}