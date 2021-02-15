package com.example.horizon_lite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.horizon_lite.R;
import com.example.horizon_lite.Task;
import com.example.horizon_lite.recyclerViews.ArchiveTasksAdapter;
import com.example.horizon_lite.room.TaskDatabase;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ArchiveActivity extends AppCompatActivity {

    //private fields for the Dao and the Database
    static TaskDatabase taskDatabase;
    RecyclerView rvTasks;
    ArchiveTasksAdapter adapter;
    ArrayList<String> names = new ArrayList<String>();
    ArrayList<Task> deleteTasks = new ArrayList<Task>();
    ExtendedFloatingActionButton deleteFAB;
    boolean deleteMode = false;
    boolean selectAllMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        // Lookup the recyclerview in activity layout
        rvTasks = (RecyclerView) findViewById(R.id.rvTasks);

        //get the FAB
        deleteFAB = findViewById(R.id.deleteFAB);

        //initialise the database
        taskDatabase = Room.databaseBuilder(getApplicationContext(),
                TaskDatabase.class, "tasks")
                .fallbackToDestructiveMigration()
                .build();

        //ROOM Threads
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            //NB: This is the new thread in which the database stuff happens
            List<Task> tasks = taskDatabase.taskDao().getAllCompletedTasks();
            Collections.reverse(tasks);
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
     * When the delete button is pressed
     * @param view
     */
    public void onDeleteButton( View view) {
        deleteTasks = new ArrayList<Task>();
        //toggle deleteMode
        deleteMode = !deleteMode;
        checkDelete();
        adapter.toggleDelete(this);
        checkFAB();
    }

    public void onDeleteFAB(View view) {
        if (deleteTasks.size() == 0) {
            selectAllMode = true;
            adapter.selectAll(this);
        } else {
            for (Task task : deleteTasks) {
                //ROOM Threads
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());
                ArrayList<Task> tasks = new ArrayList<Task>();
                executor.execute(() -> {
                    //Background work here
                    taskDatabase.taskDao().removeById(task.getId());
                    handler.post(() -> {
                        adapter.notifyItemRemoved(adapter.getTasks().indexOf(task));
                        adapter.getTasks().remove(task);
                    });
                });
            }
            deleteMode = false;
            selectAllMode = false;
            deleteFAB.setVisibility(View.INVISIBLE);
        }
        checkFAB();
    }

    /**
     * Check the status of the extended FAB
     */
    public void checkFAB() {
        if (deleteTasks.size() != 0) {
            if (deleteFAB.getText().equals("DELETE") == false) {
                deleteFAB.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_delete_24));
                deleteFAB.setText("DELETE");
            }
        }
    }

    public void checkDelete() {
        if (deleteMode == true) {
            deleteFAB.setVisibility(View.VISIBLE);
            deleteFAB.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_check_24));
            deleteFAB.setText("SELECT ALL");
        } else {
            deleteFAB.setVisibility(View.INVISIBLE);
            selectAllMode = false;
        }
    }

    public void addDeleteTask(Task task) {
        deleteTasks.add(task);
        checkFAB();
    }

    public void removeDeleteTask(Task task) {
        deleteTasks.remove(task);
        checkFAB();
    }

    /**
     * Add a name to the List of names for adding back to the main tasks
     * @param name the name to add
     */
    public void addName(String name) {
        names.add(name);
    }

    @Override
    public void onBackPressed() {
        String[] nameArray = new String[names.size()];
        //ensure names is converted to array of Strings
        nameArray = names.toArray(nameArray);
        Intent intent = new Intent();
        //pass back the names to the MainActivity
        intent.putExtra("names", nameArray);
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

    public TaskDatabase getTaskDatabase() {
        return this.taskDatabase;
    }

    public RecyclerView getRecyclerView() {
        return this.rvTasks;
    }




}