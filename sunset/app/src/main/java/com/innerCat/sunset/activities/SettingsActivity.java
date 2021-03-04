package com.innerCat.sunset.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.innerCat.sunset.R;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);

        Switch capitalizationSwitch = findViewById(R.id.capitalizationSwitch);
        if (sharedPreferences.getBoolean("capitalization", true) == true) {
            capitalizationSwitch.setChecked(true);
        } else {
            capitalizationSwitch.setChecked(false);
        }

        capitalizationSwitch.setOnCheckedChangeListener(( buttonView, isChecked ) -> {
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            if (isChecked == true) {
                editor.putBoolean("capitalization", true);
            } else {
                editor.putBoolean("capitalization", false);
            }
            editor.apply();
        });

    }

    /**
     * When the go back button is pressed
     * @param view
     */
    public void onGoBackToArchiveButton( View view) {
        onBackPressed();
    }

    @Override
    /**
     * When the hardware/software back button is pressed
     */
    public void onBackPressed() {
        finish();
    }

}