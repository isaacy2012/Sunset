package com.example.horizon_lite;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

//table name is 'entries'
@Entity(tableName = "tasks")
public class Task implements Comparable<Task> {

    //set the primary key to auto generate and increment
    @PrimaryKey(autoGenerate = true)
    //placeholder id
    private int id = 0;
    private String name;
    private Date date;
    private boolean complete = false;


    //constructor
    public Task( String name ) {
        this.name = name;
        this.date = new Date();
    }


    public void setId( int id ) {
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate( Date date ) {
        this.date = date;
    }

    public String getDateString() {
        return this.date.toString();
    }

    public void setComplete( boolean complete ) {
        this.complete = complete;
    }

    public void toggleComplete() {
        this.complete = (this.complete == false);
    }

    public boolean getComplete() {
        return this.complete;
    }

    @Override
    public int compareTo( Task o ) {
        return (o.getId() - this.getId());
    }

    @Override
    public boolean equals( Object o ) {
        if (!(o instanceof Task)) {
            return false;
        }
        Task c = (Task) o;
        if (c.getId() == this.getId()) {
            return true;
        } else {
            return false;
        }

    }
}
