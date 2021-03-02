package com.innerCat.sunset.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.innerCat.sunset.Task;

@Database(entities = { Task.class}, version = 3)
@TypeConverters({ Converters.class})
public abstract class TaskDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE tasks "
                    + " ADD COLUMN repeatTimes INTEGER NOT NULL DEFAULT 0");
            database.execSQL("UPDATE tasks SET repeatTimes = 0");
        }
    };
}


