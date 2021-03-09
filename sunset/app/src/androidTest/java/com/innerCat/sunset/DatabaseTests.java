package com.innerCat.sunset;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.innerCat.sunset.room.TaskDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class DatabaseTests {
    private TaskDatabase taskDatabase;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        System.out.println("hi");
        taskDatabase = Room.inMemoryDatabaseBuilder(context, TaskDatabase.class).build();
    }

    @After
    public void closeDb() throws IOException {
        taskDatabase.close();
    }

    @Test
    public void check_retrieve() throws Exception {
        Task task = new Task("A");
        int id = (int) taskDatabase.taskDao().insert(task);
        Task retrievedTask = taskDatabase.taskDao().getTask(id);
        assertEquals(retrievedTask, task);
    }
}
