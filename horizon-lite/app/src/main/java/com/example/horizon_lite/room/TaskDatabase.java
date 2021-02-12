package com.example.horizon_lite.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.horizon_lite.Task;

@Database(entities = { Task.class}, version = 2)
@TypeConverters({ Converters.class})
public abstract class TaskDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}

