package com.innerCat.sunset.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.innerCat.sunset.R;
import com.innerCat.sunset.Task;
import com.innerCat.sunset.factories.TextWatcherFactory;
import com.innerCat.sunset.room.Converters;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    ArrayList<Toast> toasts = new ArrayList<>();
    int buildNumberPressed = 0;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);

        //Set up the capitalization switch
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

    /**
     * When the go back button is pressed
     * @param view
     */
    public void onBuildNumberPressed( View view) {
        buildNumberPressed = buildNumberPressed+1;
        //cancel old toast
        if (toasts.isEmpty() == false) {
            toasts.get(toasts.size()-1).cancel();
        }
        if (buildNumberPressed > 4 && buildNumberPressed < 10) {
            Toast toast;
            if (buildNumberPressed < 7) {
                toast = Toast.makeText(this, "Careful...", Toast.LENGTH_SHORT);
            } else {
                toast = Toast.makeText(this, 10-buildNumberPressed + "...", Toast.LENGTH_SHORT);
            }
            toast.show();
            toasts.add(toast);
        } else if (buildNumberPressed == 10) {
            // Use the Builder class for convenient dialog construction
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded);

            //get the UI elements
            View editTextView = LayoutInflater.from(this).inflate(R.layout.text_input, null);
            EditText input = editTextView.findViewById(R.id.editName);

            //set the input type to numbers only
            input.setInputType(InputType.TYPE_CLASS_NUMBER);

            input.requestFocus();
            //prepare the context for the dialog
            Context context = this;

            builder.setMessage("Set streak")
                    .setView(editTextView)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //get the name of the Task to add
                            try {
                                int streak = Integer.parseInt(String.valueOf(input.getText()));
                                //ROOM Threads
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt(context.getString(R.string.streak), streak);
                                editor.putString(context.getString(R.string.streak_task_date), Converters.dateToTimestamp(LocalDate.now().minusDays(streak+1)));
                                editor.apply();
                            } catch (NumberFormatException e) {
                                Toast.makeText(context, "Error, please enter a valid number.", Toast.LENGTH_SHORT).show();
                            } finally {
                                resetBuildNumberPressed();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            resetBuildNumberPressed();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.setOnCancelListener(dialog1 -> {
                // dialog dismisses
                resetBuildNumberPressed();
            });
            dialog.getWindow().setDimAmount(0.0f);
            dialog.show();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE  );
            Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            okButton.setEnabled(false);
            input.addTextChangedListener(TextWatcherFactory.getNonEmptyTextWatcher(input, okButton, null));
        }
    }

    /**
     * Resets the build number pressed
     */
    private void resetBuildNumberPressed() {
        buildNumberPressed = 0;
        toasts.clear();
    }

    /**
     * When the hardware/software back button is pressed
     */
    @Override
    public void onBackPressed() {
        finish();
    }

}