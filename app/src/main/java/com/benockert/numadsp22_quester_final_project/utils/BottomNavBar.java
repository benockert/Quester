package com.benockert.numadsp22_quester_final_project.utils;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.benockert.numadsp22_quester_final_project.MainActivity;
import com.benockert.numadsp22_quester_final_project.PhotoRecap.ViewAllRecaps;
import com.benockert.numadsp22_quester_final_project.R;

public class BottomNavBar extends AppCompatActivity {

    public void onClick(MenuItem item) {
        Context context = this.getBaseContext();
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i;
                if (item.getItemId() == R.drawable.ic_home) {
                    i = new Intent(context, MainActivity.class);
                    startActivity(i);
                } else if (item.getItemId() == R.drawable.ic_recaps) {
                    i = new Intent(context, ViewAllRecaps.class);
                    startActivity(i);
                } else if (item.getItemId() == R.drawable.ic_settings) {
//                    i = new Intent(context, MainActivity.class);
//                    startActivity(i);
                } else {
                    return false;
                }
                return true;
            }
        });


    }
}
