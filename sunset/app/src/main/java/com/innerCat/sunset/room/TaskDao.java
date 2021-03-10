package com.innerCat.sunset.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.innerCat.sunset.Task;

import java.time.LocalDate;
import java.util.List;


//The Data Access Object is the abstraction layer through which the database is manipulated
@Dao
public interface TaskDao {

    /**
     * Inserts an Task
     * In the case of a conflict, it simply replaces the existing Task
     *
     * @param task the Task to insert
     * @return the rowID (primary key) of the Task
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insert( Task task );

    /**
     * Updates an task
     *
     * @param task
     */
    @Update
    public void update( Task task );

    /**
     * Removes an Task by id
     *
     * @param id the id of the Task to remove
     */
    @Query("DELETE FROM tasks WHERE id = :id")
    public void removeById( int id );

    /**
     * Removes an Task by name
     *
     * @param name the name of the Task to remove
     */
    @Query("DELETE FROM tasks WHERE name = :name")
    public int removeByName( String name );

    /**
     * Get a single Date from the id
     *
     * @param id the id (primary key) of the task
     * @return the task
     */
    @Query("SELECT date FROM tasks WHERE id = :id")
    public LocalDate getDate( int id );

    /**
     * Get a single Task from the id
     *
     * @param id the id (primary key) of the task
     * @return the task
     */
    @Query("SELECT * FROM tasks WHERE id = :id")
    public Task getTask( int id );

    /**
     * Returns all Tasks as a List
     *
     * @return all the Tasks in the database as a List
     */
    @Query("SELECT * FROM tasks")
    public List<Task> getAllTasks();

    /**
     * Returns all of todays tasks as a List
     *
     * @return all the tasks in the database as a List
     */
    @Query("SELECT * FROM tasks WHERE date(date) = date(:today)")
    public List<Task> getDayTasks( String today );

    /**
     * Returns all uncompleted Tasks as a List
     *
     * @return all the uncompleted Tasks in the database as a List
     */
    @Query("SELECT * FROM tasks WHERE (completeDate IS NULL AND julianday(date) <= julianday(:today)) ORDER BY id DESC")
    public List<Task> getAllUncompletedTasksBeforeAndToday( String today );

    /**
     * Returns all uncompleted Tasks as a List
     *
     * @return all the uncompleted Tasks in the database as a List
     */
    @Query("SELECT COUNT(*) FROM tasks WHERE (completeDate IS NULL AND julianday(date) <= julianday(:today))")
    public int getNumberOfAllUncompletedTasksBeforeAndToday( String today );

    /**
     * Returns all completed Tasks as a List
     *
     * @return all the completed Tasks in the database as a List
     */
    @Query("SELECT * FROM tasks WHERE (completeDate IS NOT NULL AND julianday(date) <= julianday(:today)) ORDER BY id DESC")
    public List<Task> getAllCompletedTasksBeforeAndToday( String today );

    /**
     * Returns all completed Tasks as a List
     *
     * @return all the completed Tasks in the database as a List
     */
    @Query("SELECT * FROM tasks WHERE (completeDate IS NOT NULL) ORDER BY id DESC")
    public List<Task> getAllCompletedTasks();

    /**
     * Returns all completed Tasks as a List
     *
     * @return all the completed Tasks in the database as a List
     */
    @Query("SELECT * FROM tasks WHERE julianday(date) = julianday(:date) ORDER BY id DESC")
    public List<Task> getAllTasksOnDate( String date );

    /**
     * Returns all Entries that match the name
     *
     * @param name the name of the Task
     * @return
     */
    @Query("SELECT * FROM tasks WHERE name = :name")
    public List<Task> getTasks( String name );

    /**
     * Returns the last failed Task
     *
     * @return
     */
    @Query("SELECT * FROM tasks WHERE (completeDate IS NULL AND julianday(date) < julianday(:today)) OR (julianday(completeDate) > julianday(date) AND julianday(date) < julianday('now')) ORDER BY date DESC LIMIT 1")
    public Task getLastStreakTask( String today );

    /**
     * Returns the first ever Task
     *
     * @return
     */
    @Query("SELECT * FROM tasks ORDER BY ROWID ASC LIMIT 1")
    public Task getFirstEverTask();

}
