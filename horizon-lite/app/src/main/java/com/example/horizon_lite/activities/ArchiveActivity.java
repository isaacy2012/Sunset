package com.example.horizon_lite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.horizon_lite.R;
import com.example.horizon_lite.Task;
import com.example.horizon_lite.recyclerViews.ArchiveTasksAdapter;
import com.example.horizon_lite.room.TaskDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ArchiveActivity extends AppCompatActivity {

    //private fields for the Dao and the Database
    public static TaskDatabase taskDatabase;
    RecyclerView rvTasks;
    ArchiveTasksAdapter adapter;
    ArrayList<String> names = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        // Lookup the recyclerview in activity layout
        rvTasks = (RecyclerView) findViewById(R.id.rvTasks);

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



}