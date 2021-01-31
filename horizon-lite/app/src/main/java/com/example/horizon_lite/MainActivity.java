package com.example.horizon_lite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {

    //private fields for the Dao and the Database
    public static TaskDatabase taskDatabase;
    RecyclerView rvTasks;
    List<Task> tasks;
    TasksAdapter adapter;

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
            tasks = taskDatabase.taskDao().getAllTasks();
            Collections.reverse(tasks);
            handler.post(() -> {
                // Create adapter passing in the sample user data
                adapter = new TasksAdapter(tasks);
                // Attach the adapter to the recyclerview to populate items
                rvTasks.setAdapter(adapter);
                // Set layout manager to position the items
                rvTasks.setLayoutManager(new LinearLayoutManager(this));
                // That's all!
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
     * When the command button is clicked, either add or remove an Task
     * @param view
     */
    public void commandButton( View view ) {
        EditText editText = findViewById(R.id.editText);
        String command = editText.getText().toString();
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
                    tasks.add(0, task);
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


    /**
     * Get the name of a particular id from the database
     * @deprecated
     * @param view
     */
    public void getNameFromID( View view ) {
        EditText getIDEdit = findViewById(R.id.getIDEdit);
        String getIDEditText = getIDEdit.getText().toString();
        if (getIDEditText.equals("") == true) {
            return;
        }
        //clear the textEdit
        getIDEdit.setText("");

        //get the id of the Task to query
        int id = Integer.parseInt(getIDEditText);

        //ROOM Threads
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            Task task = taskDatabase.taskDao().getTask(id);
            handler.post(() -> {
                //UI Thread work here
                if (task != null) {
                    //append to the CLI the name
                    append("GET: " + task.getName());
                } else {
                    //append to the CLI the error message
                    append("GET: task was null");
                }
            });
        });

    }


}