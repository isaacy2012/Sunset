package com.innerCat.sunset;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.innerCat.sunset.room.Converters;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

//table name is 'entries'
@Entity(tableName = "tasks")
public class Task implements Comparable<Task> {

    //set the primary key to auto generate and increment
    @PrimaryKey(autoGenerate = true)
    //placeholder id
    private int id = 0;
    private String name;
    private LocalDate date;

    @Nullable
    private LocalDate completeDate;

    @ColumnInfo(defaultValue = "0")
    private int repeatTimes = 0;


    /**
     * Constructor
     * @param name the name of the task
     */
    public Task( String name ) {
        this.name = name;
        this.date = LocalDate.now();
    }

    /**
     * Constructor with specified date
     * @param name the name of the task
     * @param date the specified date
     */
    @Ignore
    public Task( String name, LocalDate date ) {
        this.name = name;
        this.date = date;
    }


    public void setId( int id ) {
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate( LocalDate date ) {
        this.date = date;
    }

    public String getDateString() {
        return Converters.dateToTimestamp(this.date);
    }

    public void toggleComplete() {
        if (this.completeDate == null) {
            this.completeDate = LocalDate.now();
        } else {
            this.completeDate = null;
        }
    }

    public boolean getComplete() {
        return (this.completeDate != null);
    }

    @Nullable
    public LocalDate getCompleteDate() {
        return this.completeDate;
    }

    public void setCompleteDate( @Nullable LocalDate completeDate) {
        this.completeDate = completeDate;
    }

    public boolean runningLate() {
        return DAYS.between(getDate(), LocalDate.now()) != 0;
    }


    public boolean wasLate() {
        if (this.completeDate == null) {
            return false;
        }
        return (DAYS.between(this.date, this.completeDate) != 0);
    }

    public boolean completedToday() {
        if (this.completeDate == null) {
            return false;
        }
        return (DAYS.between(LocalDate.now(), this.completeDate) == 0);
    }

    public void setRepeatTimes(int repeatTimes) {
        this.repeatTimes = repeatTimes;
    }

    public int getRepeatTimes() {
        return this.repeatTimes;
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
        if (c.getId() == this.getId() && c.getName().equals(this.getName())) {
            return true;
        } else {
            return false;
        }

    }
}

