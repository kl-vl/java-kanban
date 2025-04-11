package ru.yandex.practicum.taskmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.service.TaskDeserializer;
import ru.yandex.practicum.taskmanager.service.exception.InvalidManagerTaskException;

import java.time.Duration;
import java.time.LocalDateTime;

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
        task1 = new Task("Test Task 1", "Test Task 1 Description", LocalDateTime.of(2025, 4, 8, 14, 45), Duration.ofMinutes(59));
        task1 = task1.copy(1);
    }

    @Test
    void testEqualsWorkCorrectlyForTaskUniqueId() {
        Task task2new = new Task("Test Task 2", "Test Task Description 2", LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0), Duration.ofMinutes(30));

        final Task task2 = task2new.copy(1);
        final Task task3 = task2new.copy(2);

        assertAll("Equals should work correctly for Tasks with unique ids",
                () -> assertEquals(task1, task1, "Same Tasks should be equals"),
                () -> assertEquals(task1, task2, "Tasks with same IDs should be equals"),
                () -> assertNotEquals(task1, task3, "Tasks with different IDs and same fields should not be equals"),
                () -> assertNotEquals(null, task1, "A Task should not be considered equal to null"),
                () -> assertNotEquals(new Object(), task1, "A Task should not be considered equal to an object of a different type")
        );
    }

    @Test
    void testConstructorsReturnProperlyInitializedTaskObject() {
        final Task task2 = new Task("Test Task 2", "Test Task 2 Description");

        assertAll("Task object should be correctly initialized",
                () -> assertNotNull(task2, "Task object should not be null after initialization"),
                () -> assertEquals("Test Task 2", task2.getName(), "Task name should match the provided value"),
                () -> assertEquals("Test Task 2 Description", task2.getDescription(), "Task description should match the provided value"),
                () -> assertEquals(Status.NEW, task2.getStatus(), "Task status should be NEW after initialization"),
                () -> assertEquals(0, task2.getId(), "Task ID should be 0 for a newly created object")
        );

        final Task task3 = new Task("Test Task 3", "Test Task 3 Description", LocalDateTime.of(2025, 4, 8, 14, 45), Duration.ofMinutes(59));

        assertAll("Task object should be correctly initialized",
                () -> assertNotNull(task3, "Task object should not be null after initialization"),
                () -> assertEquals("Test Task 3", task3.getName(), "Task name should match the provided value"),
                () -> assertEquals("Test Task 3 Description", task3.getDescription(), "Task description should match the provided value"),
                () -> assertEquals(Status.NEW, task3.getStatus(), "Task status should be NEW after initialization"),
                () -> assertEquals(0, task3.getId(), "Task ID should be 0 for a newly created object"),
                () -> assertEquals(LocalDateTime.of(2025, 4, 8, 14, 45, 0), task3.getStartTime()),
                () -> assertEquals(Duration.ofMinutes(59), task3.getDuration())
        );
    }

    @Test
    void testCopyReturnSameFieldsOfTaskObject() {
        final Task copiedTask1 = task1.copy();

        assertAll("Copy of Task object should equal the original Task object",
                () -> assertEquals(task1.getId(), copiedTask1.getId(), "Copied Task ID should match the original subtask ID"),
                () -> assertEquals(task1.getName(), copiedTask1.getName(), "Copied Task name should match the original subtask name"),
                () -> assertEquals(task1.getDescription(), copiedTask1.getDescription(), "Copied Task description should match the original subtask description"),
                () -> assertEquals(task1.getStatus(), copiedTask1.getStatus(), "Copied Task status should match the original subtask status")
        );

    }

    @Test
    void testCopyIdReturnSameFieldsOfTaskObject() {
        final Task copiedTask1 = task1.copy(11);

        assertAll("Copy of Task object with new ID should equal the original Task object",
                () -> assertEquals(11, copiedTask1.getId(), "Copied with new id Task ID should be 11"),
                () -> assertNotEquals(task1.getId(), copiedTask1.getId(), "Copied with new id Task ID should not match the original subtask ID"),
                () -> assertEquals(task1.getName(), copiedTask1.getName(), "Copied Task name should match the original subtask name"),
                () -> assertEquals(task1.getDescription(), copiedTask1.getDescription(), "Copied Task description should match the original subtask description"),
                () -> assertEquals(task1.getStatus(), copiedTask1.getStatus(), "Copied Task status should match the original subtask status")
        );
    }

    @Test
    void testCopyWithReturnSameFieldsOfTaskObject() {
        final Task copiedTask1 = task1.copyWith("Test Task 2", "Test Task 2 Description", Status.IN_PROGRESS, LocalDateTime.of(2025, 4, 8, 14, 57), Duration.ofMinutes(17));

        assertAll("Copy with fields of Task object should have new values except ID",
                () -> assertEquals(task1.getId(), copiedTask1.getId(), "Copied Task ID should match the original subtask ID"),
                () -> assertEquals("Test Task 2", copiedTask1.getName(), "Copied Task name should match new name"),
                () -> assertEquals("Test Task 2 Description", copiedTask1.getDescription(), "Copied Task description should match new description"),
                () -> assertEquals(Status.IN_PROGRESS, copiedTask1.getStatus(), "Copied Task status should match new status"),
                () -> assertEquals(LocalDateTime.of(2025, 4, 8, 14, 57), copiedTask1.getStartTime(), "Copied Task startTime should match new startTime"),
                () -> assertEquals(Duration.ofMinutes(17), copiedTask1.getDuration(), "Copied Task duration should match new duration")
        );
    }

    @Test
    void testSettersAndGettersWorkCorrectly() {
        final LocalDateTime startTime1 = LocalDateTime.of(2025, 4, 8, 14, 57);
        final Duration duration1 = Duration.ofMinutes(17);
        final LocalDateTime endTime = startTime1.plus(duration1);
        task1.setStatus(Status.DONE);
        task1.setStartTime(startTime1);
        task1.setDuration(duration1);

        assertAll("Getters should work correctly for modified Task",
                () -> assertEquals(1, task1.getId(), "Task ID should remain unchanged and match the expected value"),
                () -> assertEquals(Status.DONE, task1.getStatus(), "Task status should be updated to DONE"),
                () -> assertEquals(startTime1, task1.getStartTime(), "Task startTime should be updated to start time"),
                () -> assertEquals(duration1, task1.getDuration(), "Task duration should be updated to duration"),
                () -> assertEquals(endTime, task1.getEndTime(), "Task endTime should be equals to startTime + duration")
        );
    }

    @Test
    void testHashCodeWorkCorrectly() {
        Task task2new = new Task("Task 2", "Description 2", LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0), Duration.ofMinutes(30));
        final Task task2 = task2new.copy(1);
        Task task3new = new Task("Task 3", "Description 3");
        final Task task3 = task3new.copy(2);

        assertAll("Hashcode should correctly work for Task",
                () -> assertEquals(task1.hashCode(), task2.hashCode(), "Tasks with same id should return same hash code value"),
                () -> assertNotEquals(task1.hashCode(), task3.hashCode(), "Tasks with different id should return different hash code values")
        );
    }

    @Test
    void testToStringWorkCorrectly() {
        String expectedToStr = "Task{id=1, name='Test Task 1', description='Test Task 1 Description', status='IN_PROGRESS', startTime='2025-04-08T14:45:00', endTime='2025-04-08T15:44:00', duration=59}";

        final Task task1new = task1.copyWith(null, null, Status.IN_PROGRESS, null, null);

        assertEquals(expectedToStr, task1new.toString(), "toString() method should return the correct string representation of the Task object");
    }

    @Test
    void testGetTypeReturnTaskType() {
        assertEquals(Type.TASK, task1.getType(), "Task type should be TASK.");
    }

    @Test
    void testSerializeCsvTaskObject() {
        String expectedCsvWithoutTime = "2,TASK,Test Task 2,NEW,Test Task 2 Description,,,";
        String expectedCsvWithTime = "1,TASK,Test Task 1,NEW,Test Task 1 Description,,2025-04-08T14:45:00,59";

        Task task2new = new Task("Test Task 2", "Test Task 2 Description");
        final Task task2 = task2new.copy(2);

        assertAll("Serialize in CSV string should correctly work for Task",
                () -> assertEquals(expectedCsvWithoutTime, task2.serializeCsv(), "Serialized CSV does not match expected format of Task object."),
                () -> assertEquals(expectedCsvWithTime, task1.serializeCsv(), "Serialized CSV does not match expected format of Task object.")
        );

    }

    @Test
    void testDeserializeCsv() throws InvalidManagerTaskException {
        String csvLineTWithoutDatetime = "1,TASK,Test Task 1,NEW,Test Task 1 Description,,,";
        String csvLineTWithDatetime = "2,TASK,Test Task 2,DONE,Test Task 2 Description,,2000-01-01T00:00:00,59";
        String csvLineTWithNegativeDuration = "3,TASK,Test Task 3,NEW,Test Task 3 Description,,,-3";

        final Task task1 = TaskDeserializer.deserialize(csvLineTWithoutDatetime);

        assertAll("Deserialized Task fields without startTime",
                () -> assertEquals(1, task1.getId(), "ID does not match."),
                () -> assertEquals("Test Task 1", task1.getName(), "Name does not match."),
                () -> assertEquals("Test Task 1 Description", task1.getDescription(), "Description does not match."),
                () -> assertEquals(Status.NEW, task1.getStatus(), "Status does not match.")
        );

        final Task task2 = TaskDeserializer.deserialize(csvLineTWithDatetime);

        assertAll("Deserialized Task fields",
                () -> assertEquals(2, task2.getId(), "ID does not match."),
                () -> assertEquals("Test Task 2", task2.getName(), "Name does not match."),
                () -> assertEquals("Test Task 2 Description", task2.getDescription(), "Description does not match."),
                () -> assertEquals(Status.DONE, task2.getStatus(), "Status does not match."),
                () -> assertEquals(LocalDateTime.of(2000, 1, 1, 0, 0, 0), task2.getStartTime()),
                () -> assertEquals(Duration.ofMinutes(59), task2.getDuration())
        );

        final String expectedMessage = "Invalid negative sign of value of Task DURATION in CSV file";

        InvalidManagerTaskException exception = assertThrows(
                InvalidManagerTaskException.class,
                () -> TaskDeserializer.deserialize(csvLineTWithNegativeDuration)
        );
        assertTrue(exception.getMessage().contains(expectedMessage), "Exception message should indicate negative Duration format.");
    }

    @Test
    void testDeserializeCsvWithInvalidFormat() {
        String invalidCsvLine = "1,TASK,Task 1";

        InvalidManagerTaskException exception = assertThrows(
                InvalidManagerTaskException.class,
                () -> TaskDeserializer.deserialize(invalidCsvLine)
        );

        assertTrue(exception.getMessage().contains("Invalid CSV format"), "Exception message should indicate invalid CSV format.");
    }

    @Test
    void testSerializeCsvWithSpecialCharacters() {
        final String expectedCsv1 = "1,TASK,\"Task, 1\",NEW,\"Task 1, Description, with, commas\",,,";
        final String expectedCsv2 = "2,TASK,\"Task, 2\",DONE,\"Task 2, Description, with, commas\",,2000-01-01T00:00:00,30";

        final Task task1 = Task.createForDeserialization(1, "Task, 1", "Task 1, Description, with, commas", Status.NEW, null, null);
        final Task task2 = Task.createForDeserialization(2, "Task, 2", "Task 2, Description, with, commas", Status.DONE, LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0), Duration.ofMinutes(30));

        assertAll("Serialized Task fields",
                () -> assertEquals(expectedCsv1, task1.serializeCsv(), "Serialized CSV with special characters does not match expected format."),
                () -> assertEquals(expectedCsv2, task2.serializeCsv(), "Serialized CSV with special characters does not match expected format.")
        );
    }

    @Test
    void testDeserializeCsvWithSpecialCharacters() throws InvalidManagerTaskException {
        String expectedCsvLine = "1,TASK,\"Task, 1\",NEW,\"Task 1 Description, with, commas\",,,";

        Task task = TaskDeserializer.deserialize(expectedCsvLine);

        assertAll("Deserialized Task fields with special characters",
                () -> assertEquals(1, task.getId(), "ID does not match."),
                () -> assertEquals("Task, 1", task.getName(), "Name does not match."),
                () -> assertEquals("Task 1 Description, with, commas", task.getDescription(), "Description does not match."),
                () -> assertEquals(Status.NEW, task.getStatus(), "Status does not match.")
        );
    }

}