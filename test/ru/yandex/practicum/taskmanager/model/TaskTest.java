package ru.yandex.practicum.taskmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskTest {

    private Task task1;

    @BeforeEach
    void setUp() {
        task1 = new Task("Test Task 1", "Test Task 1 Description");
        task1 = task1.copy(1);
    }

    @Test
    void equals_shouldWorkCorrectlyWithTaskIds() {
        Task task2 = new Task("Test Task 2", "Test Task Description 2");
        task2 = task2.copy(1);
        Task task3 = task1.copy(2);

        assertEquals(task1, task1, "Same task should be equals");
        assertEquals(task1, task2, "Tasks with same id should be equals");
        assertNotEquals(task1, task3, "Tasks with different id and same fields should not be equals");
        assertNotEquals(null, task1);
        assertNotEquals(new Object(), task1);
    }

    @Test
    void constructor_shouldReturnProperlyInitializedTaskObject() {
        Task task2 = new Task("Test Task 2", "Test Task 2 Description");

        assertNotNull(task2);
        assertEquals("Test Task 2", task2.getName());
        assertEquals("Test Task 2 Description", task2.getDescription());
        assertEquals(Status.NEW, task2.getStatus());
        assertEquals(0, task2.getId());
    }

    @Test
    void copy_ShouldReturnSameFieldsOfTaskObject() {
        task1.setStatus(Status.IN_PROGRESS);

        Task copiedTask = task1.copy();

        assertEquals(task1.getId(), copiedTask.getId());
        assertEquals(task1.getName(), copiedTask.getName());
        assertEquals(task1.getDescription(), copiedTask.getDescription());
        assertEquals(task1.getStatus(), copiedTask.getStatus());
    }

    @Test
    void settersAndGettersShouldWork() {
        task1.setName(task1.getName() + " Updated");
        task1.setDescription(task1.getDescription() + " Updated");
        task1.setStatus(Status.DONE);

        assertEquals(1, task1.getId());
        assertEquals("Test Task 1 Updated", task1.getName());
        assertEquals("Test Task 1 Description Updated", task1.getDescription());
        assertEquals(Status.DONE, task1.getStatus());
    }

    @Test
    void hashCode_shouldWorkCorrectly() {
        Task task2 = new Task("Task 2", "Description 2");
        task2 = task2.copy(1);
        Task task3 = new Task("Task 3", "Description 3");
        task3 = task3.copy(2);

        assertEquals(task1.hashCode(), task2.hashCode(), "Tasks with same id should return same hash code value");
        assertNotEquals(task1.hashCode(), task3.hashCode(), "Tasks with different id should return different hash code values");
    }

    @Test
    void toString_ShouldWorkCorrectly() {
        task1.setStatus(Status.IN_PROGRESS);

        String expected = "Task{id=1, name='Test Task 1', description='Test Task 1 Description', status='IN_PROGRESS'}";
        assertEquals(expected, task1.toString());
    }
}