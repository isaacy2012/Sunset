package com.innerCat.sunset.factories;

import android.content.Context;

import androidx.room.Room;

import com.innerCat.sunset.room.TaskDatabase;

public class TaskDatabaseFactory {
    public static TaskDatabase getTaskDatabase( Context context ) {
        return Room.databaseBuilder(context.getApplicationContext(),
                TaskDatabase.class, "tasks")
                .fallbackToDestructiveMigration()
                .build();
    }
}
