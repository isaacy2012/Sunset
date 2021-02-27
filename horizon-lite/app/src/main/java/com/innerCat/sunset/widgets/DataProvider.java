package com.innerCat.sunset.widgets;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.room.Room;

import com.innerCat.sunset.R;
import com.innerCat.sunset.Task;
import com.innerCat.sunset.room.TaskDatabase;

import java.util.ArrayList;
import java.util.List;


public class DataProvider implements RemoteViewsService.RemoteViewsFactory {

    List<String> tasks = new ArrayList<>();
    Context context = null;
    TaskDatabase taskDatabase;

    public DataProvider(Context context, Intent intent) {
        this.context = context;
        taskDatabase = Room.databaseBuilder(context.getApplicationContext(),
                TaskDatabase.class, "tasks")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        tasks.clear();
        final List<Task> uncompleteTasks = taskDatabase.taskDao().getAllUncompletedTasks();
        for (Task task : uncompleteTasks) {
            tasks.add(task.getName());
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews widgetListView = new RemoteViews(context.getPackageName(),
                R.layout.list_item_widget);
        System.out.println("queried");
        widgetListView.setTextViewText(R.id.listItemWidgetTextView, tasks.get(position));

        // Create an Intent to launch MainActivity
        Intent intent = new Intent();
        intent.putExtra("com.example.android.stackwidget.EXTRA_ITEM", position);
        widgetListView.setOnClickFillInIntent(R.id.listItemWidgetTextView, intent);

        return widgetListView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}