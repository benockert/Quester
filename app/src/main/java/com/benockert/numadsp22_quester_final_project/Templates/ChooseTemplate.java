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
                    case "Horizontal":
                        templates.setImageResource(R.drawable.horizontal_template);
                        templates.setVisibility(View.VISIBLE);
                        nothingSelected.setVisibility(View.INVISIBLE);
                        break;
                    case "Single":
                        templates.setImageResource(R.drawable.single_template);
                        templates.setVisibility(View.VISIBLE);
                        nothingSelected.setVisibility(View.INVISIBLE);
                        break;
                    case "Vertical":
                        templates.setImageResource(R.drawable.vertical_template);
                        templates.setVisibility(View.VISIBLE);
                        nothingSelected.setVisibility(View.INVISIBLE);
                        break;
                    case "Grid 1":
                        templates.setImageResource(R.drawable.grid1_template);
                        templates.setVisibility(View.VISIBLE);
                        nothingSelected.setVisibility(View.INVISIBLE);
                        break;
                    case "Grid 2":
                        templates.setImageResource(R.drawable.grid2_template);
                        templates.setVisibility(View.VISIBLE);
                        nothingSelected.setVisibility(View.INVISIBLE);
                        break;
                    case "Triangle (3 photo limit)":
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
        Intent i = new Intent(this, ChoosePhotos.class);
        i.putExtra("chosenTemplateName", chosenTemplate);
        i.putExtra("recapName", recapName);
        startActivity(i);
    }
}