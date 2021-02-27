package com.innerCat.sunset.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.innerCat.sunset.R;
import com.innerCat.sunset.activities.MainActivity;
import com.innerCat.sunset.room.TaskDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementation of App Widget functionality.
 */
public class HomeWidgetProvider extends AppWidgetProvider {

    static TaskDatabase taskDatabase;

    static void updateAppWidget( Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId ) {
        // Create an Intent to launch MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Get the layout for the App Widget and attach an on-click listener
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.home_widget);
        setRemoteAdapter(context, views);
        views.setOnClickPendingIntent(R.id.homeWidgetLinearLayout, pendingIntent);

        if (taskDatabase == null) {
            initDatabase(context);
        }

        //ROOM Threads
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            final int num = taskDatabase.taskDao().getNumberOfAllUncompletedTasks();
            handler.post(() -> {
                // Instruct the widget manager to update the widget
                views.setTextViewText(R.id.appwidget_num, String.valueOf(num));
                appWidgetManager.updateAppWidget(appWidgetId, views);
            });
        });
    }


    @Override
    public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds ) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.home_widget);
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            views.setPendingIntentTemplate(R.id.widgetListView, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widgetListView);

            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled( Context context ) {
        // Enter relevant functionality for when the first widget is created
        initDatabase(context);
    }

    public static void initDatabase(Context context) {
        //initialise the database
        taskDatabase = Room.databaseBuilder(context,
                TaskDatabase.class, "tasks")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Override
    public void onDisabled( Context context ) {
        // Enter relevant functionality for when the last widget is disabled
        taskDatabase = null;
    }

    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widgetListView,
                new Intent(context, WidgetService.class));
    }
}