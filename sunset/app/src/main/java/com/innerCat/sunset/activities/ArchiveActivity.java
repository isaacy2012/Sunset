package com.innerCat.sunset.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.innerCat.sunset.R;
import com.innerCat.sunset.Task;
import com.innerCat.sunset.recyclerViews.ArchiveTasksAdapter;
import com.innerCat.sunset.room.Converters;
import com.innerCat.sunset.room.TaskDatabase;
import com.innerCat.sunset.widgets.HomeWidgetProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ArchiveActivity extends AppCompatActivity {

    //private fields for the Dao and the Database
    static TaskDatabase taskDatabase;
    RecyclerView rvTasks;
    ArchiveTasksAdapter adapter;
    ArrayList<Integer> idsToReplay = new ArrayList<>();
    ArrayList<Task> deleteTasks = new ArrayList<Task>();
    ExtendedFloatingActionButton deleteFAB;
    ImageButton deleteButton;
    boolean deleteMode = false;
    boolean selectAllMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        // Lookup the recyclerview in activity layout
        rvTasks = (RecyclerView) findViewById(R.id.rvTasks);
        rvTasks.setNestedScrollingEnabled(false);

        //get the FAB
        deleteFAB = findViewById(R.id.deleteFAB);
        //get the deleteButton
        deleteButton = findViewById(R.id.deleteButton);

        //initialise the database
        taskDatabase = Room.databaseBuilder(getApplicationContext(),
                TaskDatabase.class, "tasks")
                //.fallbackToDestructiveMigration()
                .addMigrations(TaskDatabase.MIGRATION_2_3)
                .build();

        //ROOM Threads
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            //NB: This is the new thread in which the database stuff happens
            List<Task> tasks = taskDatabase.taskDao().getAllCompletedTasks();
            handler.post(() -> {
                // Create adapter passing in the sample user data
                adapter = new ArchiveTasksAdapter(tasks);
                // Attach the adapter to the recyclerview to populate items
                rvTasks.setAdapter(adapter);
                // Set layout manager to position the items
                rvTasks.setLayoutManager(new LinearLayoutManager(this));
                // That's all!
            });
        });
    }

    /**
     * When the go back button is pressed
     * @param view
     */
    public void onGoBackToMainButton( View view ) {
        onBackPressed();
    }

    /**
     * When the settings button is pressed
     * @param view
     */
    public void onSettingsButton( View view ) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * When the delete button is pressed
     * @param view
     */
    public void onDeleteButton( View view ) {
        if (adapter.getTasks().isEmpty() == true) {
            return;
        }
        deleteTasks = new ArrayList<Task>();
        //toggle deleteMode
        deleteMode = !deleteMode;
        checkDelete();
        checkFAB(false);
    }

    /**
     * When the deleteFAB is pressed
     * @param view
     */
    public void onDeleteFAB(View view) {
        if (deleteTasks.size() == 0) { //if there are no items in the deleteTasks list then the deleteFAB acts as a 'select all' button
            selectAllMode = true;
            adapter.selectAll(this);
        } else { //otherwise, delete all the items in the deleteTasks list
            // Use the Builder class for convenient dialog construction
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded);
            LayoutInflater inflater = LayoutInflater.from(this);

            builder.setMessage("Are you sure you wish to delete " + deleteTasks.size() + " " + (deleteTasks.size() > 1 ? "tasks" : "task") + "?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteMode = false;
                            selectAllMode = false;
                            rvTasks.setPadding(0, 0, 0, Converters.fromDpToPixels(16, getResources()));
                            deleteFAB.setVisibility(View.INVISIBLE);
                            checkDelete();

                            //ROOM Threads
                            ExecutorService executor = Executors.newSingleThreadExecutor();
                            Handler handler = new Handler(Looper.getMainLooper());
                            executor.execute(() -> {
                                //Background work here
                                for (Task task : deleteTasks) {
                                    taskDatabase.taskDao().removeById(task.getId());
                                }
                                handler.post(() -> {
                                    for (Task task : deleteTasks) {
                                        adapter.notifyItemRemoved(adapter.getTasks().indexOf(task));
                                        adapter.getTasks().remove(task);
                                    }
                                });
                            });
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // cancelled
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.getWindow().setDimAmount(0.0f);
            dialog.show();
        }
        checkFAB(false);
    }

    /**
     * Check the status of the extended FAB
     */
    public void checkFAB(boolean back) {
        if (deleteTasks.size() != 0) {
            if (deleteFAB.getText().equals("DELETE") == false) {
                deleteFAB.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_delete_24));
                deleteFAB.setText("DELETE");
            }
        } else {
            if (back == true) {
                if (deleteFAB.getText().equals("SELECT ALL") == false) {
                    deleteFAB.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_delete_24));
                    deleteFAB.setText("SELECT ALL");
                }
            }
        }
    }

    /**
     * Check the status of the UI items with respect to the deleteMode
     */
    public void checkDelete() {
        adapter.checkDelete(deleteMode);
        if (deleteMode == true) {
            rvTasks.setPadding(0, 0, 0, Converters.fromDpToPixels(68, getResources()));
            deleteFAB.setVisibility(View.VISIBLE);
            deleteFAB.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_check_24));
            deleteFAB.setText("SELECT ALL");
            deleteButton.setImageResource(R.drawable.ic_baseline_close_24);
        } else {
            rvTasks.setPadding(0, 0, 0, Converters.fromDpToPixels(16, getResources()));
            deleteFAB.setVisibility(View.INVISIBLE);
            selectAllMode = false;
            deleteButton.setImageResource(R.drawable.ic_baseline_delete_24);
        }
    }

    /**
     * Add a task to the deletion list
     * @param task the task to add
     */
    public void addDeleteTask(Task task) {
        deleteTasks.add(task);
        checkFAB(true);
    }

    /**
     * Remove a task to the deletion list
     * @param task the task to remove
     */
    public void removeDeleteTask(Task task) {
        deleteTasks.remove(task);
        checkFAB(true);
    }


    /**
     * Repeat a task to the database
     * @param task the task to repeat
     */
    public void repeatTask( Task task ) {
        //ROOM Threads
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            taskDatabase.taskDao().removeById(task.getId());
            Task newTask = new Task(task.getName());
            newTask.setRepeatTimes(task.getRepeatTimes()+1);
            long id = taskDatabase.taskDao().insert(newTask);
            newTask.setId((int) id);
            idsToReplay.add((int)id);
            handler.post(() -> {
                HomeWidgetProvider.broadcastUpdate(this);
            });
        });
    }

    @Override
    /**
     * When the hardware/software back button is pressed
     */
    public void onBackPressed() {
        Intent intent = new Intent();
        //pass back the ids to the MainActivity
        intent.putExtra("ids", idsToReplay);
        setResult(RESULT_OK, intent);
        finish();
    }

    //setters and getters

    public boolean getDeleteMode() {
        return this.deleteMode;
    }

    public boolean getSelectAllMode() {
        return this.selectAllMode;
    }

}