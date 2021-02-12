package com.example.horizon_lite.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.horizon_lite.room.Converters;
import com.example.horizon_lite.R;
import com.example.horizon_lite.Task;
import com.example.horizon_lite.room.TaskDatabase;
import com.example.horizon_lite.recyclerViews.TasksAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.time.temporal.ChronoUnit.DAYS;


public class MainActivity extends AppCompatActivity {

    //private fields for the Dao and the Database
    public static TaskDatabase taskDatabase;
    RecyclerView rvTasks;
    TasksAdapter adapter;

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate( SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE tasks "
                    + " ADD COLUMN late INTEGER");
        }
    };

    public void goToArchive(View view) {
        Intent intent = new Intent(this, ArchiveActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //find the textView
        TextView textView = findViewById(R.id.textView);
        //make the textView scrollable
        textView.setMovementMethod(new ScrollingMovementMethod());

        //initialise the database
        taskDatabase = Room.databaseBuilder(getApplicationContext(),
                TaskDatabase.class, "tasks")
                .fallbackToDestructiveMigration()
                .build();

        // Lookup the recyclerview in activity layout
        rvTasks = (RecyclerView) findViewById(R.id.rvTasks);

        //ROOM Threads
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            //NB: This is the new thread in which the database stuff happens
            List<Task> tasks = taskDatabase.taskDao().getAllUncompletedTasks();
            Collections.reverse(tasks);
            handler.post(() -> {
                // Create adapter passing in the sample user data
                adapter = new TasksAdapter(tasks);
                // Attach the adapter to the recyclerview to populate items
                rvTasks.setAdapter(adapter);
                // Set layout manager to position the items
                rvTasks.setLayoutManager(new LinearLayoutManager(this));
                // That's all!
                //overscroll mode
                //rvTasks.setOverScrollMode(View.OVER_SCROLL_NEVER);
            });
        });
        updateStreak();
    }

    /**
     * Update the streak
     */
    public void updateStreak() {
        //ROOM Threads
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            //NB: This is the new thread in which the database stuff happens
            int streak = 0;
            Task lastStreakTask = taskDatabase.taskDao().getLastStreakTask(Converters.dateToTimestamp(LocalDate.now()));
            if (lastStreakTask != null) { //if there was a previous failed task
                //the streak is the difference between the day after that day and today
                streak = (int)DAYS.between(lastStreakTask.getDate(), LocalDate.now())-1;
            } else { //if there were no previous failed tasks
                Task firstEverTask = taskDatabase.taskDao().getFirstEverTask();
                if (firstEverTask == null) {
                    streak = 0; //if there are no tasks at all ever, then the streak is 0
                } else { //day difference between the first ever (completed) task and today is the streak
                    streak = (int)DAYS.between(firstEverTask.getDate(), LocalDate.now());
                }
            }

            //if today is all completed, add a streak
            List<Task> todayTasks = taskDatabase.taskDao().getTodayTasks(Converters.dateToTimestamp(LocalDate.now()));
            //List<Task> todayTasks = taskDatabase.taskDao().getTodayTasks();
            boolean ok = true;
            if (todayTasks.size() > 0) { //if there are tasks today
                for (Task task : todayTasks) {
                    if (task.getComplete() == false) {
                        ok = false;
                    }
                }
                if (ok == true) { //increase the streak by 1 if all the tasks are done
                    streak = streak+1;
                }
            } else { //if there are no tasks today
                if (streak != 0) { //only increase the streak by 1 if the streak isn't 0
                    streak = streak+1;
                }
            }
            //------------------
            final String finalStreak = String.valueOf(streak);

            handler.post(() -> {
                TextView streakCounter = findViewById(R.id.streakCounter);
                streakCounter.setText(finalStreak);
            });
        });

    }


    /**
     * Append to the CLI textView
     * @param text the text to append
     */
    public void append(String text) {
        TextView textView = findViewById(R.id.textView);
        textView.append("\n" + text);
    }


    /**
     * Shows all entries in the database
     * @param view
     */
    public void showAllButton( View view ) {
        //ROOM Threads
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            //NB: This is the new thread in which the database stuff happens
            List<Task> tasks = taskDatabase.taskDao().getAllTasks();
            handler.post(() -> {
                //UI Thread work here
                //NB: This is what happens after the database stuff
                if (tasks != null) {
                    append("====ENTRIES====");
                    for (Task task : tasks) {
                        append(task.getId() + " " + task.getComplete() + ": " + task.getName() + " at: " + task.getDateString());
                    }
                    append("---------------");
                } else {
                    append("GET: entries was null");
                }
            });
        });
    }

    /**
     * Add a task to the database
     * @param name the name of the task
     */
    public void addTask(String name) {
        //ROOM Threads
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            Task task = new Task(name);
            long id = taskDatabase.taskDao().insert(task);
            task.setId((int) id);
            handler.post(() -> {
                //UI Thread work here
                append("ADDED: " + task.getName() + " ID: " + id);
                // Add a new task
                adapter.addTask(0, task);
                // Notify the adapter that an item was inserted at position 0
                adapter.notifyItemInserted(0);
                //rvTasks.scheduleLayoutAnimation();
                rvTasks.scrollToPosition(0);
                //rvTasks.scheduleLayoutAnimation();
            });
        });
        updateStreak();
    }

    /**
     * When the command button is clicked, either add or remove an Task
     * @param view
     */
    /**
    public void commandButton( View view ) {
        EditText editText = findViewById(R.id.editText);
        String command = editText.getText().toString();
        if (command.equals("") == true || InnerHealth.nWords(command) == 0) {
            return;
        }
        if (InnerHealth.nWord(command, 0).equals("add") == true) { //add an task
            //checking the command is correctly formed
            if (command.length()<InnerHealth.nWord(command, 0).length()+1) {
                return;
            }
            //get the name of the Task to add
            String name = command.substring(InnerHealth.nWord(command, 0).length()+1);

            //ROOM Threads
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here
                Task task = new Task(name);
                long id = taskDatabase.taskDao().insert(task);
                handler.post(() -> {
                    //UI Thread work here
                    append("ADDED: " + task.getName() + " ID: " + id);
                    // Add a new task
                    adapter.addTask(0, task);
// Notify the adapter that an item was inserted at position 0
                    adapter.notifyItemInserted(0);
                    rvTasks.scrollToPosition(0);
                });
            });
        } else if (InnerHealth.nWord(command, 0).equals("removeID") == true) { //remove an task
            //checking that the command is correctly formed
            if (command.length()<InnerHealth.nWord(command, 0).length()+1) {
                return;
            } else if (InnerHealth.nWords(command) > 2) {
                return;
            }
            if (InnerHealth.nWord(command, 1) == null) {
                return;
            }

            //get the id of the Event to remove
            int id;
            try {
                id = Integer.parseInt(InnerHealth.nWord(command, 1));
            } catch (NumberFormatException e) {
                append("ERROR: enter the id number, not the name");
                return;
            }
            //ROOM Threads
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                //Background work here
                Task task = taskDatabase.taskDao().getTask(id);
                taskDatabase.taskDao().removeById(id);
                handler.post(() -> {
                    //UI Thread work here
                    append("REMOVEDID: " + task.getName());

                    //remove the task
                    if (tasks.contains(task)) {
                        append("TRY: " + task.getName());
                        int position = tasks.indexOf(task);
                        tasks.remove(task);
                        //notify that task was removed at position
                        adapter.notifyItemRemoved(position);
                    }
                });
            });
        } else if (InnerHealth.nWord(command, 0).equals("remove") == true) { //remove an task
            //checking that the command is correctly formed
            if (command.length()<InnerHealth.nWord(command, 0).length()+1) {
                return;
            } else if (InnerHealth.nWords(command) > 2) {
                return;
            }
            if (InnerHealth.nWord(command, 1) == null) {
                return;
            }

            //get the id of the Event to remove
            String name = command.substring(InnerHealth.nWord(command, 0).length()+1);
            //ROOM Threads
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                //Background work here
                int numberDeleted = taskDatabase.taskDao().removeByName(name);
                handler.post(() -> {
                    //UI Thread work here
                    if (numberDeleted == 1) {
                        append("REMOVED: " + name);
                    } else if (numberDeleted != 0) {
                        append("REMOVED " + numberDeleted + " ENTRIES CALLED: " + name);
                    } else {
                        append("NO ENTRIES TO REMOVE");
                    }
                });
            });
        } else if (InnerHealth.nWord(command, 0).equals("get") == true) { //remove an task
            String name = InnerHealth.nWord(command, 1);

            //ROOM Threads
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here
                //NB: This is the new thread in which the database stuff happens
                List<Task> entries = taskDatabase.taskDao().getTasks(name);
                handler.post(() -> {
                    //UI Thread work here
                    //NB: This is what happens after the database stuff
                    if (entries != null) {
                        append("====ENTRIES STARTING WITH " + name + "====");
                        for (Task task : entries) {
                            append(task.getId() + ": " + task.getName());
                        }
                        append("---------------");
                    } else {
                        append("GET: entries was null");
                    }
                });
            });

        } else if (InnerHealth.nWord(command, 0).equals("toggle") == true) { //remove an task
            //checking that the command is correctly formed
            if (command.length() < InnerHealth.nWord(command, 0).length() + 1) {
                return;
            } else if (InnerHealth.nWords(command) > 2) {
                return;
            }
            if (InnerHealth.nWord(command, 1) == null) {
                return;
            }

            //get the id of the Event to remove
            int id;
            try {
                id = Integer.parseInt(InnerHealth.nWord(command, 1));
            } catch (NumberFormatException e) {
                append("ERROR: enter the id number, not the name");
                return;
            }
            //ROOM Threads
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                //Background work here
                String name = taskDatabase.taskDao().getTask(id).getName();
                Task editTask = taskDatabase.taskDao().getTask(id);
                editTask.toggleComplete();
                taskDatabase.taskDao().update(editTask);

                handler.post(() -> {
                    //UI Thread work here
                    append("TOGGLED: " + name);
                });
            });
        }
        editText.setText("");
    }
     */

    /** Called when the user taps the FAB button */
    public void fabButton(View view) {

        // Use the Builder class for convenient dialog construction
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded);
        LayoutInflater inflater = LayoutInflater.from(this);
        View editTextView = inflater.inflate(R.layout.text_input, null);
        EditText input = editTextView.findViewById(R.id.editName);
        input.requestFocus();

        builder.setMessage("Name")
                .setView(editTextView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //get the name of the Task to add
                        String name = input.getText().toString();
                        //add the task
                        addTask(name);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setDimAmount(0.0f);
        dialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }


}