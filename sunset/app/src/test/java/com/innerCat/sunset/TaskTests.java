package com.innerCat.sunset;

import org.junit.Test;
import org.testng.annotations.AfterTest;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertNotEquals;

public class TaskTests {

    @Test
    public void check_date_base_constructor() {
        Task task = new Task("TestTask");
        assertEquals(LocalDate.now(), task.getDate());
        assertEquals(0, task.getRepeatTimes());
        assertFalse(task.wasLate());
        assertFalse(task.runningLate());
        assertFalse(task.isComplete());
        assertNull(task.getCompleteDate());
    }

    @Test
    public void check_date_extended_constructor() {
        Task task = new Task("Extended", LocalDate.now().plusDays(3));
        assertEquals(LocalDate.now().plusDays(3), task.getDate());
    }

    @Test
    public void should_be_late() {
        Task task = new Task("Late", LocalDate.now().minusDays(1));
        assertFalse(task.isComplete());
        assertFalse(task.wasLate());
        assertTrue(task.runningLate());
        task.toggleComplete();
        assertTrue(task.isComplete());
        assertTrue(task.wasLate());
    }

    @Test
    public void check_toggleComplete() {
        Task task = new Task("Toggle Complete");
        assertFalse(task.isComplete());
        task.toggleComplete();
        assertTrue(task.isComplete());
        task.toggleComplete();
        assertFalse(task.isComplete());
    }


    @Test
    public void not_late() {
        Task task = new Task("On Time", LocalDate.now());
        assertFalse(task.isComplete());
        assertFalse(task.wasLate());
        assertFalse(task.runningLate());
        task.toggleComplete();
        assertTrue(task.isComplete());
        assertFalse(task.wasLate());
    }

    @Test
    public void test_repeat_times() {
        Task task = new Task("Repeat");
        assertEquals(0, task.getRepeatTimes());
        int repeatTime = 3;
        task.setRepeatTimes(repeatTime);
        assertEquals(repeatTime, task.getRepeatTimes());
    }

    @Test
    public void test_equals_true() {
        Task a = new Task("A");
        Task b = new Task("A");
        int id = 3;
        a.setId(id);
        b.setId(id);
        assertEquals(a, b);
    }

    @AfterTest
    public void test_equals_false_name() {
        Task a = new Task("A");
        Task b = new Task("B");
        int id = 3;
        a.setId(id);
        b.setId(id);
        assertNotEquals(a, b);
    }

    @Test
    public void test_equals_false_id() {
        Task a = new Task("A");
        Task b = new Task("A");
        a.setId(3);
        b.setId(2);
        assertNotEquals(a, b);
    }
}
