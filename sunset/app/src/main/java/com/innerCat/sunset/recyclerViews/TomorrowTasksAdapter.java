package com.innerCat.sunset.recyclerViews;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.innerCat.sunset.R;
import com.innerCat.sunset.Task;
import com.innerCat.sunset.activities.MainActivity;
import com.innerCat.sunset.factories.TextWatcherFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class TomorrowTasksAdapter extends
        RecyclerView.Adapter<TomorrowTasksAdapter.ViewHolder> {

    private List<Task> tasks;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public CheckBox checkBox;
        public Task task;
        public Context context;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder( View itemView, Context context ) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.nameView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            checkBox.setOnClickListener(v -> {
                task.toggleComplete();
                int currentPosition = tasks.indexOf(task);
                if (task.isComplete() == true) {
                    if (task.wasLate() == false) {
                        ((MainActivity) context).todayComplete();
                    }

                    //UI moving
                    boolean found = false;
                    while (found == false) {
                        for (int i = currentPosition; i < tasks.size() - 1; i++) {
                            if (tasks.get(i + 1).isComplete() == false) {
                                Collections.swap(tasks, i, i + 1);
                            } else {
                                found = true;
                                break;
                            }
                        }
                        if (found == false) {
                            //reached end
                            found = true;
                        }
                    }
                    int newPosition = tasks.indexOf(task);
                    notifyItemMoved(currentPosition, newPosition);
                } else {
                    Task task = tasks.get(currentPosition);
                    //UI moving
                    boolean found = false;
                    while (found == false) {
                        for (int i = currentPosition; i > 0; i--) {
                            if (tasks.get(i - 1).isComplete() == true) {
                                Collections.swap(tasks, i, i - 1);
                            } else {
                                found = true;
                                break;
                            }
                        }
                        if (found == false) {
                            //reached end
                            found = true;
                        }
                    }
                    int newPosition = tasks.indexOf(task);
                    notifyItemMoved(currentPosition, newPosition);

                }

                //ROOM Threads
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());
                executor.execute(() -> {
                    //Background work here
                    MainActivity.taskDatabase.taskDao().update(task);
                    handler.post(() -> {
                        ((MainActivity) context).updateStreak();
                    });
                });
            });

            //store the context
            this.context = context;
            // Attach a click listener to the entire row view
            itemView.setOnClickListener(this);
        }

        // Handles the row being being clicked
        @Override
        public void onClick( View view ) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Task task = tasks.get(position);
                // Use the Builder class for convenient dialog construction
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_Rounded);

                //get the UI elements
                FloatingActionButton fab = ((MainActivity) context).findViewById(R.id.floatingActionButton);
                fab.setVisibility(View.INVISIBLE);
                View editTextView = LayoutInflater.from(context).inflate(R.layout.text_input, null);
                EditText input = editTextView.findViewById(R.id.editName);

                //Set the capitalisation from sharedPreferences
                if (context.getSharedPreferences("preferences", Context.MODE_PRIVATE).getBoolean("capitalization", true) == true) {
                    input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                } else {
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                }

                input.requestFocus();

                builder.setMessage("Name")
                        .setView(editTextView)
                        .setPositiveButton("Ok", ( dialog, id ) -> {
                            //get the name of the Task to edit
                            String newName = input.getText().toString();
                            //edit the task
                            task.setName(newName);
                            ((MainActivity) context).updateTomorrowTask(task, position);
                        })
                        .setNegativeButton("Cancel", ( dialog, id ) -> {
                            // User cancelled the dialog
                            fab.setVisibility(View.VISIBLE);
                        })
                        .setNeutralButton("Delete", ( dialog, id ) -> {
                            tasks.remove(task);
                            ((MainActivity) context).deleteTaskFromTomorrow(task, position);
                            fab.setVisibility(View.VISIBLE);
                        });
                AlertDialog dialog = builder.create();
                dialog.setOnCancelListener(dialog1 -> {
                    // dialog dismisses
                    fab.setVisibility(View.VISIBLE);
                });
                dialog.getWindow().setDimAmount(0.0f);
                dialog.show();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setEnabled(false);
                input.addTextChangedListener(TextWatcherFactory.getNonEmptyTextWatcher(input, okButton, null));
            }
        }
    }


    /**
     * Pass in the tasks array into the Adapter
     *
     * @param tasks
     */
    public TomorrowTasksAdapter( List<Task> tasks ) {
        this.tasks = tasks;
    }


    /**
     * Add a task
     *
     * @param position the position of the new Task in the List
     * @param task     the Task to add
     */
    public void addTask( int position, Task task ) {
        tasks.add(position, task);
    }

    /**
     * Remove all the checked items in the taskAdapter
     */
    public void removeAllChecked() {
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.isComplete() == true) {
                tasks.remove(i);
                notifyItemRemoved(i);
                i = i - 1;
            }
        }
    }

    /**
     * @return the tasks of this TaskAdapter
     */
    public List<Task> getTasks() {
        return this.tasks;
    }

    /**
     * Removes all tasks
     */
    public void removeAllTasks() {
        this.tasks.clear();
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public TomorrowTasksAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View taskView = inflater.inflate(R.layout.list_item_tomorrow, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(taskView, context);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder( TomorrowTasksAdapter.ViewHolder holder, int position ) {
        // Get the data model based on position
        holder.task = tasks.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.nameTextView;
        textView.setText(holder.task.getName());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return tasks.size();
    }


}
