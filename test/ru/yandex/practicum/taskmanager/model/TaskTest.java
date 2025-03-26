package ru.yandex.practicum.taskmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.service.TaskDeserializer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {

    private Task task1;

    @BeforeEach
    void setUp() {
        task1 = new Task("Test Task 1", "Test Task 1 Description");
        task1 = task1.copy(1);
    }

    @Test
    void equals_shouldWorkCorrectlyForTaskWithId() {
        Task task2new = new Task("Test Task 2", "Test Task Description 2");
        final Task task2 = task2new.copy(1);
        final Task task3 = task2new.copy(2);

        assertAll("Equals should work correctly for Tasks with id",
                () -> assertEquals(task1, task1, "Same Tasks should be equals"),
                () -> assertEquals(task1, task2, "Tasks with same IDs should be equals"),
                () -> assertNotEquals(task1, task3, "Tasks with different IDs and same fields should not be equals"),
                () -> assertNotEquals(null, task1, "A Task should not be considered equal to null"),
                () -> assertNotEquals(new Object(), task1, "A Task should not be considered equal to an object of a different type")
        );
    }

    @Test
    void constructor_shouldReturnProperlyInitializedTaskObject() {
        final Task task2 = new Task("Test Task 2", "Test Task 2 Description");

        assertAll("Task object should be correctly initialized",
                () -> assertNotNull(task2, "Task object should not be null after initialization"),
                () -> assertEquals("Test Task 2", task2.getName(), "Task name should match the provided value"),
                () -> assertEquals("Test Task 2 Description", task2.getDescription(), "Task description should match the provided value"),
                () -> assertEquals(Status.NEW, task2.getStatus(), "Task status should be NEW after initialization"),
                () -> assertEquals(0, task2.getId(), "Task ID should be 0 for a newly created object")
        );
    }

    @Test
    void copy_ShouldReturnSameFieldsOfTaskObject() {
        task1.setStatus(Status.IN_PROGRESS);
        final Task copiedTask = task1.copy();

        assertAll("Copy of Task object should equal the original Task object",
                () -> assertEquals(task1.getId(), copiedTask.getId(), "Copied Task ID should match the original subtask ID"),
                () -> assertEquals(task1.getName(), copiedTask.getName(), "Copied Task name should match the original subtask name"),
                () -> assertEquals(task1.getDescription(), copiedTask.getDescription(), "Copied Task description should match the original subtask description"),
                () -> assertEquals(task1.getStatus(), copiedTask.getStatus(), "Copied Task status should match the original subtask status")
        );
    }

    @Test
    void copyWithId_ShouldReturnSameFieldsOfTaskObject() {
        task1.setStatus(Status.IN_PROGRESS);
        final Task copiedTask2 = task1.copy(11);

        assertAll("Copy of Task object with new ID should equal the original Task object",
                () -> assertEquals(11, copiedTask2.getId(), "Copied with new id Task ID should be 11"),
                () -> assertNotEquals(task1.getId(), copiedTask2.getId(), "Copied with new id Task ID should not match the original subtask ID"),
                () -> assertEquals(task1.getName(), copiedTask2.getName(), "Copied Task name should match the original subtask name"),
                () -> assertEquals(task1.getDescription(), copiedTask2.getDescription(), "Copied Task description should match the original subtask description"),
                () -> assertEquals(task1.getStatus(), copiedTask2.getStatus(), "Copied Task status should match the original subtask status")
        );
    }

    @Test
    void settersAndGettersShouldWork() {
        task1.setName(task1.getName() + " Updated");
        task1.setDescription(task1.getDescription() + " Updated");
        task1.setStatus(Status.DONE);

        assertAll("Getters should work correctly for modified Task",
                () -> assertEquals(1, task1.getId(), "Task ID should remain unchanged and match the expected value"),
                () -> assertEquals("Test Task 1 Updated", task1.getName(), "Task name should be updated to the new value"),
                () -> assertEquals("Test Task 1 Description Updated", task1.getDescription(), "Task description should be updated to the new value"),
                () -> assertEquals(Status.DONE, task1.getStatus(), "Task status should be updated to DONE")
        );
    }

    @Test
    void hashCode_shouldWorkCorrectly() {
        Task task2new = new Task("Task 2", "Description 2");
        final Task task2 = task2new.copy(1);
        Task task3new = new Task("Task 3", "Description 3");
        final Task task3 = task3new.copy(2);

        assertAll("Hashcode should correctly work for Task",
                () -> assertEquals(task1.hashCode(), task2.hashCode(), "Tasks with same id should return same hash code value"),
                () -> assertNotEquals(task1.hashCode(), task3.hashCode(), "Tasks with different id should return different hash code values")
        );
    }

    @Test
    void toString_ShouldWorkCorrectly() {
        String expected = "Task{id=1, name='Test Task 1', description='Test Task 1 Description', status='IN_PROGRESS'}";

        task1.setStatus(Status.IN_PROGRESS);

        assertEquals(expected, task1.toString(), "toString() method should return the correct string representation of the Task object");
    }

    @Test
    void getType_ShouldReturnTaskType() {
        assertEquals(Type.TASK, task1.getType(), "Task type should be TASK.");
    }

    @Test
    void serializeCsv_ShouldSerializeTaskObjectCorrectly() {
        String expectedCsv = "1,TASK,Test Task 1,NEW,Test Task 1 Description,";

        assertEquals(expectedCsv, task1.serializeCsv(), "Serialized CSV does not match expected format of Task object.");
    }

    @Test
    void testDeserializeCsv() {
        String csvLine = "1,TASK,Test Task 1,NEW,Test Task 1 Description,";

        Task task = TaskDeserializer.deserialize(csvLine);

        assertAll("Deserialized Task fields",
                () -> assertEquals(1, task.getId(), "ID does not match."),
                () -> assertEquals("Test Task 1", task.getName(), "Name does not match."),
                () -> assertEquals("Test Task 1 Description", task.getDescription(), "Description does not match."),
                () -> assertEquals(Status.NEW, task.getStatus(), "Status does not match.")
        );
    }

    @Test
    void testDeserializeCsvWithInvalidFormat() {
        String invalidCsvLine = "1,TASK,Task 1";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> TaskDeserializer.deserialize(invalidCsvLine)
        );

        assertTrue(exception.getMessage().contains("Invalid CSV format"), "Exception message should indicate invalid CSV format.");
    }

    @Test
    void testSerializeCsvWithSpecialCharacters() {
        String expectedCsv = "1,TASK,\"Task, 1\",NEW,\"Task 1, Description, with, commas\",";

        Task task = Task.createForDeserialization(1, "Task, 1", "Task 1, Description, with, commas", Status.NEW);

        assertEquals(expectedCsv, task.serializeCsv(), "Serialized CSV with special characters does not match expected format.");
    }

    @Test
    void testDeserializeCsvWithSpecialCharacters() {
        String csvLine = "1,TASK,\"Task, 1\",NEW,\"Task 1 Description, with, commas\",";

        Task task = TaskDeserializer.deserialize(csvLine);

        assertAll("Deserialized Task fields with special characters",
                () -> assertEquals(1, task.getId(), "ID does not match."),
                () -> assertEquals("Task, 1", task.getName(), "Name does not match."),
                () -> assertEquals("Task 1 Description, with, commas", task.getDescription(), "Description does not match."),
                () -> assertEquals(Status.NEW, task.getStatus(), "Status does not match.")
        );
    }

}