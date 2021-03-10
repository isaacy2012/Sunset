package com.innerCat.sunset;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.innerCat.sunset.room.Converters;
import com.innerCat.sunset.room.DBMethods;
import com.innerCat.sunset.room.TaskDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@MediumTest
@RunWith(AndroidJUnit4.class)
public class DatabaseTests {
    private Context context;
    private TaskDatabase taskDatabase;
    private SharedPreferences sharedPreferences;

    /**
     * Add a task into the database, setting its id in the process
     * @param task the task to add
     */
    public void addTask(Task task) {
        int id = (int) taskDatabase.taskDao().insert(task);
        task.setId(id);
    }

    /**
     * Make and add a task with name and offset quickly
     * @param name the name of the task
     * @param dayOffset the dayOffset (from today)
     */
    public Task makeAndAddTask(String name, int dayOffset, boolean completed) {
        if (dayOffset > 0) {
            Task addTask = new Task(name, LocalDate.now().plusDays(dayOffset));
            if (completed == true) {
                addTask.setCompleteDate(addTask.getDate());
            }
            addTask(addTask);
            return addTask;
        } else if (dayOffset < 0) {
            Task addTask = new Task(name, LocalDate.now().minusDays(-dayOffset));
            if (completed == true) {
                addTask.setCompleteDate(addTask.getDate());
            }
            addTask(addTask);
            return addTask;
        } else {
            Task addTask = new Task(name);
            if (completed == true) {
                addTask.setCompleteDate(addTask.getDate());
            }
            addTask(addTask);
            return addTask;
        }
    }

    @Before
    public void createDb() {
        context = ApplicationProvider.getApplicationContext();
        taskDatabase = Room.inMemoryDatabaseBuilder(context, TaskDatabase.class).build();
        sharedPreferences = getPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * Get the test sharedPreferences
     * @return sharedPreferences
     */
    private SharedPreferences getPreferences() {
        return context.getSharedPreferences("test", Context.MODE_PRIVATE);
    }

    @After
    public void closeDb() throws IOException {
        taskDatabase.close();
    }

    /**
     * Check that insert and retrieve on 'id' returns the same task
     */
    @Test
    public void check_retrieve() {
        Task task = new Task("A");
        int id = (int) taskDatabase.taskDao().insert(task);
        task.setId(id);
        Task retrievedTask = taskDatabase.taskDao().getTask(id);
        assertEquals(retrievedTask, task);
    }

    /**
     * Check that deleting from database works
     */
    @Test
    public void check_delete() {
        Task task = new Task("A");
        int id = (int) taskDatabase.taskDao().insert(task);
        task.setId(id);
        taskDatabase.taskDao().removeById(id);
        Task retrievedTask = taskDatabase.taskDao().getTask(id);
        assertNull(retrievedTask);
    }

    @Test
    public void check_getDateTasks1() {
        Task expected = makeAndAddTask("D", -4, false);
        makeAndAddTask("A", -3, true);
        makeAndAddTask("B", -3, true);
        makeAndAddTask("C", -3, true);
        Task actual = taskDatabase.taskDao().getLastStreakTask(Converters.todayString());
        assertEquals(expected, actual);
    }

    @Test
    public void check_getDateTasks2() {
        makeAndAddTask("A", -4, false);
        makeAndAddTask("B", -3, true);
        makeAndAddTask("C", -3, true);
        Task expected = makeAndAddTask("D", -3, false);
        Task actual = taskDatabase.taskDao().getLastStreakTask(Converters.todayString());
        assertEquals(expected, actual);
    }

    @Test
    public void check_maxStreak() {
        int maxStreak = 10;
        makeAndAddTask("A", -(maxStreak+1), false);
        makeAndAddTask("B", -4, true);
        makeAndAddTask("C", -3, true);
        makeAndAddTask("D", -2, true);
        makeAndAddTask("E", -1, true);
        int actualMaxStreak = DBMethods.calculateMaxStreak(context, taskDatabase);
        assertEquals(maxStreak, actualMaxStreak);
    }



}
