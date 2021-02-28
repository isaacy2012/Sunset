package com.innerCat.sunset.widgets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.room.Room;

import com.innerCat.sunset.R;
import com.innerCat.sunset.Task;
import com.innerCat.sunset.room.TaskDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;


public class DataProvider implements RemoteViewsService.RemoteViewsFactory {

    List<SpannableStringBuilder> tasks = new ArrayList<>();
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
        List<Task> incompleteTasks = taskDatabase.taskDao().getAllUncompletedTasks();
        //formatting the tasks
        Collections.reverse(incompleteTasks);
        for (int i = 0; i < incompleteTasks.size(); i++) {
            if (DAYS.between(incompleteTasks.get(i).getDate(), LocalDate.now()) != 0) {
                Task task = incompleteTasks.get(i);
                //noinspection SuspiciousListRemoveInLoop since we are adding it back at index 0
                incompleteTasks.remove(i);
                incompleteTasks.add(0, task);
            }
        }

        for (Task task : incompleteTasks) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();

            SpannableString name= new SpannableString(task.getName());
            name.setSpan(new ForegroundColorSpan(Color.BLACK), 0, name.length(), 0);
            if ((int)DAYS.between(task.getDate(), LocalDate.now()) != 0) {
                SpannableString bullet= new SpannableString("• ");
                bullet.setSpan(new ForegroundColorSpan(Color.parseColor("#d15b5b")), 0, bullet.length(), 0);
                ssb.append(bullet);
            }

            ssb.append(name);

            tasks.add(ssb);
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