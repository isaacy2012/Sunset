package com.innerCat.sunrise;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;

import androidx.room.Room;

import com.innerCat.sunrise.activities.MainActivity;
import com.innerCat.sunrise.room.TaskDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementation of App Widget functionality.
 */
public class HomeWidget extends AppWidgetProvider {

    public static TaskDatabase taskDatabase;

    static void updateAppWidget( Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId ) {
        // Create an Intent to launch MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Get the layout for the App Widget and attach an on-click listener
        // to the button
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.home_widget);
        views.setOnClickPendingIntent(R.id.homeWidgetLinearLayout, pendingIntent);
        //initialise the database
        taskDatabase = Room.databaseBuilder(context.getApplicationContext(),
                TaskDatabase.class, "tasks")
                .fallbackToDestructiveMigration()
                .build();

        //ROOM Threads
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            //NB: This is the new thread in which the database stuff happens
            final List<Task> tasks = taskDatabase.taskDao().getAllUncompletedTasks();
            final int numUncompleted = tasks.size();
            handler.post(() -> {
                CharSequence widgetNum = String.valueOf(numUncompleted);
                // Construct the RemoteViews object
                views.setTextViewText(R.id.appwidget_num, widgetNum);
                //string array for the text views
                String[] stringArray = new String[]{"","",""};
                for (int i = 0; i < tasks.size(); i++) {
                    stringArray[i] = tasks.get(i).getName();
                }

                //set the text of the textViews
                views.setTextViewText(R.id.appwidget_task0, stringArray[0]);
                views.setTextViewText(R.id.appwidget_task1, stringArray[1]);
                views.setTextViewText(R.id.appwidget_task2, stringArray[2]);

                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            });
        });
    }

    @Override
    public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds ) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled( Context context ) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled( Context context ) {
        // Enter relevant functionality for when the last widget is disabled
    }
}