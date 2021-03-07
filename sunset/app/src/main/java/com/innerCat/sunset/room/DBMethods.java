package com.innerCat.sunset.room;

import android.content.Context;
import android.content.SharedPreferences;

import com.innerCat.sunset.R;
import com.innerCat.sunset.Task;

import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class DBMethods {

    /**
     * Update and get the streak from SharedPreferences
     * @param context the Context
     * @param taskDatabase the TaskDatabase
     * @return the final streak
     */
    public static int updateAndGetStreak(Context context, TaskDatabase taskDatabase) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int streak = sharedPreferences.getInt(context.getString(R.string.streak), 0);

        //calculate the maximum streak
        int maxStreak = DBMethods.calculateMaxStreak(context, taskDatabase);

        //if the streak hasn't been updated today
        LocalDate lastUpdated = Converters.fromTimestamp(sharedPreferences.getString(context.getString(R.string.last_updated), Converters.dateToTimestamp(LocalDate.ofEpochDay(0))));
        if (DAYS.between(lastUpdated, LocalDate.now()) != 0) {
            streak = DBMethods.updateDayStreak(context, taskDatabase, streak, maxStreak);
        }

        //if all tasks have been completed today
        List<Task> todayTasks = taskDatabase.taskDao().getDayTasks(Converters.dateToTimestamp(LocalDate.now()));
        if (DBMethods.areAllTasksCompletedAtLeastOne(context, todayTasks) == true) {
            maxStreak = maxStreak+1;
            streak = streak+1;
        }

        //if the new streak is greater than the maximum theoretical streak, then set it to the max
        streak = Math.min(streak, maxStreak);

        //Update the highscore
        if (streak > sharedPreferences.getInt(context.getString(R.string.highscore), 0)) {
            editor.putInt(context.getString(R.string.highscore), streak);
            editor.apply();
        }
        return streak;
    }

    /**
     * Calculate the maximum streak possible (assuming no days where the user had no tasks
     * @param context the Context
     * @param taskDatabase the TaskDatabase
     * @return the maximum streak possible
     */
    public static int calculateMaxStreak( Context context, TaskDatabase taskDatabase ) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String lastStreakTaskDateString = sharedPreferences.getString(context.getString(R.string.streak_task_date), "none");
        int maxStreak;

        //checking that lastStreakDateString is in the past
        if (lastStreakTaskDateString.equals("none") == false) {
            LocalDate lastStreakTaskDate = Converters.fromTimestamp(lastStreakTaskDateString);
            if (DAYS.between(lastStreakTaskDate, LocalDate.now()) < 0) {
                editor.putString(context.getString(R.string.streak_task_date), Converters.dateToTimestamp(LocalDate.now().minusDays(1)));
                editor.apply();
            }
        }
        if (lastStreakTaskDateString.equals("none") == false) { //if there was a previous lastStreakTaskDateString then use that
            LocalDate lastStreakTaskDate = Converters.fromTimestamp(lastStreakTaskDateString);
            maxStreak = (int) DAYS.between(lastStreakTaskDate, LocalDate.now()) - 1;
        } else { //otherwise create one
            Task lastStreakTask = taskDatabase.taskDao().getLastStreakTask(Converters.dateToTimestamp(LocalDate.now()));
            if (lastStreakTask != null) { //if there was a previous failed task
                //the streak is the difference between the day after that day and today
                maxStreak = (int) DAYS.between(lastStreakTask.getDate(), LocalDate.now()) - 1;
                if (maxStreak < 0) {maxStreak = 0;}
                editor.putString(context.getString(R.string.streak_task_date), Converters.dateToTimestamp(lastStreakTask.getDate()));
            } else { //if there were no previous failed tasks
                Task firstEverTask = taskDatabase.taskDao().getFirstEverTask();
                //if there is a first task
                if (firstEverTask != null) { //day difference between the first ever (completed) task and today is the streak
                    maxStreak = (int) DAYS.between(firstEverTask.getDate(), LocalDate.now()) - 1;
                    if (maxStreak < 0) {maxStreak = 0;}
                    editor.putString(context.getString(R.string.streak_task_date), Converters.dateToTimestamp(firstEverTask.getDate()));
                } else {  //if there are no tasks at all ever
                    maxStreak = 0; //if there are no tasks at all ever, then the streak is 0
                }
            }
        }
        if (maxStreak < 0) {maxStreak = 0;}
        return maxStreak;
    }

    /**
     * Update the streak from yesterday
     * @param context the Context
     * @param taskDatabase the TaskDatabase
     * @param streak the streak in computation
     * @param maxStreak the maxStreak in computation
     * @return
     */
    public static int updateDayStreak(Context context, TaskDatabase taskDatabase, int streak, int maxStreak) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        List<Task> yesterdayTasks = taskDatabase.taskDao().getDayTasks(Converters.dateToTimestamp(LocalDate.now().minusDays(1)));
        //if yesterday indicated that the streak should be increased
        if (areAllTasksCompletedAtLeastOne(context, yesterdayTasks) == true) {
            streak = streak + 1;
            if (streak > maxStreak) {
                streak = maxStreak;
            }
            editor.putInt(context.getString(R.string.streak), streak);
        } else { //if the streak should not have been increased
            //if any of the tasks yesterday were failed AND there were tasks yesterday
            if (areAllTasksCompletedAtLeastOne(context, yesterdayTasks) == false && yesterdayTasks.size() > 0) {
                //reset streak
                streak = 0;
                editor.putInt(context.getString(R.string.streak), streak);
                editor.putString(context.getString(R.string.streak_task_date), Converters.dateToTimestamp(LocalDate.now().minusDays(1)));
            }
            //otherwise the streak is the same
        }
        //reset yesterdays "today tasks completed"
        if (sharedPreferences.getBoolean(context.getString(R.string.today_at_least_one_completed), false) == true) {
            editor.putBoolean(context.getString(R.string.today_at_least_one_completed), false);
        }

        //update the "last updated" date to today
        editor.putString(context.getString(R.string.last_updated), Converters.dateToTimestamp(LocalDate.now()));
        editor.apply();
        return streak;
    }

    /**
     * Returns whether all of today's tasks were completed AND if there were tasks today
     * @param todayTasks all of today's tasks
     * @return whether there are tasks AND they are all completed
     */
    public static boolean areAllTasksCompletedAtLeastOne(Context context, List<Task> todayTasks) {
        if (todayTasks.size() > 0) { //if there are tasks today
            for (Task task : todayTasks) {
                if (task.getComplete() == false) {
                    return false;
                }
            }
            return true;
        } else {
            if (context.getSharedPreferences("preferences", Context.MODE_PRIVATE).getBoolean(context.getString(R.string.today_at_least_one_completed), false) == true) {
                return true;
            }
        }
        return false;
    }

}
