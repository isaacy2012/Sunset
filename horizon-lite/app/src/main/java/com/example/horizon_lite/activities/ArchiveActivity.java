package com.example.horizon_lite.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.horizon_lite.R;
import com.example.horizon_lite.Task;
import com.example.horizon_lite.recyclerViews.ArchiveTasksAdapter;
import com.example.horizon_lite.recyclerViews.TasksAdapter;
import com.example.horizon_lite.room.Converters;
import com.example.horizon_lite.room.TaskDatabase;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.horizon_lite.activities.MainActivity.taskDatabase;
import static java.time.temporal.ChronoUnit.DAYS;


public class ArchiveActivity extends AppCompatActivity {

    //private fields for the Dao and the Database
    public static TaskDatabase taskDatabase;
    RecyclerView rvTasks;
    ArchiveTasksAdapter adapter;


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
                //overscroll mode
                //rvTasks.setOverScrollMode(View.OVER_SCROLL_NEVER);
            });
        });
    }



}