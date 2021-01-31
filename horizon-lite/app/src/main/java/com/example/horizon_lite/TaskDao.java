package com.example.horizon_lite;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


//The Data Access Object is the abstraction layer through which the database is manipulated
@Dao
public interface TaskDao {

    /**
     * Inserts an Task
     * In the case of a conflict, it simply replacesthe existing task
     * @param task the Task to insert
     * @return the rowID (primary key) of the Task
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insert(Task task);

    /**
     * Updates an task
     * @param task
     */
    @Update
    public void update( Task task );

    /**
     * Removes an Task by id
     * @param id the id of the Task to remove
     */
    @Query("DELETE FROM tasks WHERE id = :id")
    public void removeById( int id );

    /**
     * Removes an Task by name
     * @param name the name of the Task to remove
     */
    @Query("DELETE FROM tasks WHERE name = :name")
    public int removeByName( String name );


    /**
     * Get a single task from the id
     * @param id the id (primary key) of the task
     * @return the task
     */
    @Query("SELECT * FROM tasks WHERE id = :id")
    public Task getTask( int id );

    /**
     * Returns all tasks as a List
     * @return all the tasks in the database as a List
     */
    @Query("SELECT * FROM tasks")
    public List<Task> getAllTasks();

    /**
     * Returns all Entries that match the name
     * @param name the name of the Task
     * @return
     */
    @Query("SELECT * FROM tasks WHERE name = :name")
    public List<Task> getTasks(String name);

}
