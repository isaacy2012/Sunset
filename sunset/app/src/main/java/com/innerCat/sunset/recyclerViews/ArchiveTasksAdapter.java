package com.innerCat.sunset.recyclerViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.innerCat.sunset.R;
import com.innerCat.sunset.Task;
import com.innerCat.sunset.activities.ArchiveActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ArchiveTasksAdapter extends
        RecyclerView.Adapter<ArchiveTasksAdapter.ViewHolder> {

    private List<Task> archivedTasks;
    private Set<ViewHolder> mBoundViewHolders = new HashSet<>();

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public CheckBox deleteCheckBox;
        public TextView replayTextView;
        public ImageButton replayButton;
        public Task task;
        public Context context;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder( View itemView, Context context) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            this.context = context;


            nameTextView = (TextView) itemView.findViewById(R.id.nameView);
            replayButton = (ImageButton) itemView.findViewById(R.id.replayButton);
            deleteCheckBox = (CheckBox) itemView.findViewById(R.id.deleteCheckBox);
            replayTextView = (TextView) itemView.findViewById(R.id.replayTextView );
            deleteCheckBox.setOnClickListener(v -> {
                if (deleteCheckBox.isChecked() == true) {
                    ((ArchiveActivity)context).addDeleteTask(task);
                } else {
                    ((ArchiveActivity)context).removeDeleteTask(task);
                }
            });
            replayButton.setOnClickListener(v -> {
                int currentPosition = archivedTasks.indexOf(task);
                archivedTasks.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                ((ArchiveActivity)context).repeatTask(task);
            });
        }

        /**
         * Update the state of the checkboxes in the recyclerview wrt the modes in ArchiveActivity
         */
        public void updateState() {
            if (((ArchiveActivity)context).getDeleteMode() == true) {
                deleteCheckBox.setVisibility(View.VISIBLE);
                if (((ArchiveActivity)context).getSelectAllMode() == true) {
                    deleteCheckBox.setChecked(true);
                    ((ArchiveActivity)context).addDeleteTask(task);
                }
            } else {
                deleteCheckBox.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Enables deletion of all the tasks
     */
    public void checkDelete(boolean deleteMode) {
        for (ViewHolder viewHolder : mBoundViewHolders) {
            if (deleteMode == true) {
                viewHolder.deleteCheckBox.setVisibility(View.VISIBLE);
                viewHolder.deleteCheckBox.setChecked(false);
            } else {
                viewHolder.deleteCheckBox.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Select all the items in the RecyclerView
     * @param context the context (ArchiveActivity instance)
     */
    public void selectAll(Context context) {
        for (ViewHolder viewHolder : mBoundViewHolders) {
            viewHolder.deleteCheckBox.setChecked(true);
        }
        for (Task task : archivedTasks) {
            ((ArchiveActivity)context).addDeleteTask(task);
        }
    }



    /**
     * Pass in the tasks array into the Adapter
     * @param tasks
     */
    public ArchiveTasksAdapter( List<Task> tasks) {
        this.archivedTasks = tasks;
    }

    /**
     * Add a task
     * @param position the position of the new Task in the List
     * @param task the Task to add
     */
    public void addTask(int position, Task task) {
        archivedTasks.add(position, task);
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ArchiveTasksAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View taskView = inflater.inflate(R.layout.list_item_archive, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(taskView, context);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder( ArchiveTasksAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        holder.task = archivedTasks.get(position);

        // Set item views based on your views and data model
        TextView nameTextView = holder.nameTextView;
        TextView replayTextView = holder.replayTextView;
        int repeatTimes = holder.task.getRepeatTimes();
        if (repeatTimes != 0) {
            String str = "Replayed " + (repeatTimes > 1 ? repeatTimes +  " times" : "once") + " before";
            replayTextView.setText(str);
            replayTextView.setVisibility(View.VISIBLE);
        } else {
            replayTextView.setVisibility(View.GONE);
        }
        nameTextView.setText(holder.task.getName());
        mBoundViewHolders.add(holder);
        holder.updateState();


    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return archivedTasks.size();
    }

    public List<Task> getTasks() {
        return this.archivedTasks;
    }


}
