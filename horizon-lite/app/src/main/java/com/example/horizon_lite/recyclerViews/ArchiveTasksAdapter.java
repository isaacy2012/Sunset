package com.example.horizon_lite.recyclerViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.horizon_lite.R;
import com.example.horizon_lite.Task;
import com.example.horizon_lite.activities.ArchiveActivity;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ArchiveTasksAdapter extends
        RecyclerView.Adapter<ArchiveTasksAdapter.ViewHolder> {

    private List<Task> archivedTasks;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public ImageButton imageButton;
        public Task task;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder( View itemView, Context context) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.nameView);
            imageButton = (ImageButton) itemView.findViewById(R.id.replayButton);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentPosition = archivedTasks.indexOf(task);
                    archivedTasks.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    ((ArchiveActivity)context).addName(task.getName());
                }
            });
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
        View taskView = inflater.inflate(R.layout.archive_list_item, parent, false);

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
        TextView textView = holder.nameTextView;
        textView.setText(holder.task.getName());

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return archivedTasks.size();
    }


}
