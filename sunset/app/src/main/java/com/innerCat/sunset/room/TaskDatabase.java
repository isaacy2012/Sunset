package com.innerCat.sunset.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.innerCat.sunset.Task;

@Database(entities = { Task.class }, version = 2)
@TypeConverters({ Converters.class })
public abstract class TaskDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}


