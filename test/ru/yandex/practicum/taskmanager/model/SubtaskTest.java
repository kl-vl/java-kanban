package ru.yandex.practicum.taskmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.service.exception.InvalidManagerTaskException;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.taskmanager.service.TaskDeserializer.deserialize;

class SubtaskTest {

    private Subtask subtask1;
    private Epic epic1;

    @BeforeEach
    void setUp() {
        subtask1 = new Subtask("Test Subtask 1", "Test Subtask 1 Description", LocalDateTime.of(2025, 4, 8, 14, 45), Duration.ofMinutes(59));
        subtask1 = subtask1.copy(1);
        epic1 = new Epic("Test Epic 1", "Test Epic 1 Description");
        epic1 = epic1.copy(2);
    }

    @Test
    void testEqualsReturnTrueForSubtasksWithSameId() {
        subtask1.setEpic(epic1);
        Subtask subtask2new = new Subtask("Test Subtask 2", "Test Subtask 2 Description", LocalDateTime.of(2025, 4, 8, 15, 47), Duration.ofMinutes(31));
        subtask2new.setStatus(Status.IN_PROGRESS);
        final Task subtask2 = subtask2new.copy(1);
        final Subtask subtask3 = subtask1.copy(2);

        assertAll("Equals should correctly work for Tasks",
                () -> assertEquals(subtask1, subtask2, "Subtasks with the same ID should be considered equal"),
                () -> assertNotEquals(subtask1, subtask3, "Subtasks with different IDs but the same fields should not be considered equal"),
                () -> assertNotEquals(subtask1, subtask3, "Subtasks with different id and same fields should not be equals"),
                () -> assertNotEquals(null, subtask1, "A subtask should not be considered equal to null"),
                () -> assertNotEquals(new Object(), subtask1, "A subtask should not be considered equal to an object of a different type")
        );
    }

    @Test
    void testConstructoReturnProperlyInitializedSubtaskObject() {
        final Subtask subtask2 = new Subtask("Test Subtask 2", "Test Subtask 2 Description");

        assertAll("Subtask should be properly initialized",
                () -> assertNotNull(subtask2, "Subtask object should not be null after initialization"),
                () -> assertEquals("Test Subtask 2", subtask2.getName(), "Subtask name should match the provided value"),
                () -> assertEquals("Test Subtask 2 Description", subtask2.getDescription(), "Subtask description should match the provided value"),
                () -> assertEquals(Status.NEW, subtask2.getStatus(), "Subtask status should be NEW after initialization"),
                () -> assertEquals(0, subtask2.getId(), "Subtask ID should be 0 for a newly created object"),
                () -> assertNull(subtask2.getEpic(), "Subtask should not be associated with an epic after initialization")
        );

        final LocalDateTime startTime = LocalDateTime.of(2025, 4, 8, 15, 47);
        final Duration duration = Duration.ofMinutes(31);
        final Subtask subtask3 = new Subtask("Test Subtask 3", "Test Subtask 3 Description", startTime, duration);

        assertAll("Subtask object should be correctly initialized",
                () -> assertNotNull(subtask3, "Subtask object should not be null after initialization"),
                () -> assertEquals("Test Subtask 3", subtask3.getName(), "Subtask name should match the provided value"),
                () -> assertEquals("Test Subtask 3 Description", subtask3.getDescription(), "Subtask description should match the provided value"),
                () -> assertEquals(Status.NEW, subtask3.getStatus(), "Subtask status should be NEW after initialization"),
                () -> assertEquals(0, subtask3.getId(), "Task ID should be 0 for a newly created object"),
                () -> assertNull(subtask3.getEpic(), "Epic of new Subtask should be null for a newly created object"),
                () -> assertEquals(startTime, subtask3.getStartTime()),
                () -> assertEquals(duration, subtask3.getDuration())
        );
    }

    @Test
    void testCopyReturnSameFieldsOfSubtaskObject() {
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask1.setEpic(epic1);
        final Subtask copiedSubtask1 = subtask1.copy();

        assertAll("Copy of Subtask object should equal the original Subtask object",
                () -> assertEquals(subtask1.getId(), copiedSubtask1.getId(), "Copied Subtask ID should match the original subtask ID"),
                () -> assertEquals(subtask1.getName(), copiedSubtask1.getName(), "Copied Subtask name should match the original subtask name"),
                () -> assertEquals(subtask1.getDescription(), copiedSubtask1.getDescription(), "Copied Subtask description should match the original subtask description"),
                () -> assertEquals(subtask1.getStatus(), copiedSubtask1.getStatus(), "Copied Subtask status should match the original subtask status"),
                () -> assertEquals(subtask1.getEpic(), copiedSubtask1.getEpic(), "Copied Subtask Epic should match the original subtask epic")
        );
    }

    @Test
    void testCopyIdReturnSameFieldsOfSubtaskObject() {
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask1.setEpic(epic1);
        final Subtask copiedSubtask2 = subtask1.copy(33);

        assertAll("Copy of Subtask object with new ID should equal the original Subtask object",
                () -> assertEquals(33, copiedSubtask2.getId(), "Copied with new id subtask ID should be 33"),
                () -> assertNotEquals(subtask1.getId(), copiedSubtask2.getId(), "Copied with new id subtask ID should not match the original subtask ID"),
                () -> assertEquals(subtask1.getName(), copiedSubtask2.getName(), "Copied subtask name should match the original subtask name"),
                () -> assertEquals(subtask1.getDescription(), copiedSubtask2.getDescription(), "Copied subtask description should match the original subtask description"),
                () -> assertEquals(subtask1.getStatus(), copiedSubtask2.getStatus(), "Copied subtask status should match the original subtask status"),
                () -> assertEquals(subtask1.getEpic(), copiedSubtask2.getEpic(), "Copied subtask epic should match the original subtask epic")
        );
    }

    @Test
    void testCopyWithReturnSameFieldsOfSubtaskObject() {
        final Subtask copiedSubtask1 = subtask1.copyWith("Test Subtask 2", "Test Subtask 2 Description", Status.DONE, LocalDateTime.of(2025, 4, 8, 14, 57), Duration.ofMinutes(17), epic1);

        assertAll("Copy with fields of Subtask object should have new values except ID",
                () -> assertEquals(subtask1.getId(), copiedSubtask1.getId(), "Copied Task ID should match the original subtask ID"),
                () -> assertEquals("Test Subtask 2", copiedSubtask1.getName(), "Copied Task name should match new name"),
                () -> assertEquals("Test Subtask 2 Description", copiedSubtask1.getDescription(), "Copied Task description should match new description"),
                () -> assertEquals(Status.DONE, copiedSubtask1.getStatus(), "Copied Task status should match new status"),
                () -> assertEquals(epic1, copiedSubtask1.getEpic(), "Copied Subtask epic should match new epic"),
                () -> assertEquals(LocalDateTime.of(2025, 4, 8, 14, 57), copiedSubtask1.getStartTime(), "Copied Task startTime should match new startTime"),
                () -> assertEquals(Duration.ofMinutes(17), copiedSubtask1.getDuration(), "Copied Task duration should match new duration")
        );
    }

    @Test
    void testAssignEpicToSubtaskWorkCorrectly() {
        epic1 = epic1.copy(2);
        subtask1.setEpic(epic1);

        assertEquals(epic1, subtask1.getEpic(), "Subtask's Epic should be set to epic1");

        Epic epicNew2 = new Epic("New Epic", "Test Epic Description");
        final Epic epic2 = epicNew2.copy(3);
        subtask1.setEpic(epic2);

        assertEquals(epic2, subtask1.getEpic(), "Subtask's Epic should be updated to the new Epic");
    }

    @Test
    void testHashCodedWorkCorrectly() {
        final LocalDateTime startTime = LocalDateTime.of(2025, 4, 8, 15, 47);
        final Duration duration = Duration.ofMinutes(31);
        Subtask subtaskNew2 = new Subtask("Test Subtask 2", "Test Subtask 2 Description", startTime, duration);
        final Subtask subtask2 = subtaskNew2.copy(1);
        Subtask subtaskNew3 = new Subtask("Test Subtask 3", "Test Subtask 3 Description");
        final Subtask subtask3 = subtaskNew3.copy(2);

        assertAll("Hashcode should correctly work for Subtask",
                () -> assertEquals(subtask1.hashCode(), subtask2.hashCode(), "Subtasks with same id should return same hash code value"),
                () -> assertNotEquals(subtask1.hashCode(), subtask3.hashCode(), "Subtasks with different id should return different hash code values")
        );
    }

    @Test
    void testToStringWorkCorrectly() {
        final String expectedWithoutEpic = "Subtask{id=1, name='Test Subtask 1', description='Test Subtask 1 Description', status='NEW', startTime='2025-04-08T14:45:00', endTime='2025-04-08T15:44:00', duration=59, epicId=}";

        assertEquals(expectedWithoutEpic, subtask1.toString(), "The toString() method should return the correct string representation of the Subtask object without Epic");

        final String expectedWithEpic = "Subtask{id=1, name='Test Subtask 1', description='Test Subtask 1 Description', status='NEW', startTime='2025-04-08T14:45:00', endTime='2025-04-08T15:44:00', duration=59, epicId=2}";
        subtask1.setEpic(epic1.copy(2));

        assertEquals(expectedWithEpic, subtask1.toString(), "toString() method should return the correct string representation of the Subtask object with Epic");
    }

    @Test
    void testGetTypeReturnSubtaskType() {
        assertEquals(Type.SUBTASK, subtask1.getType(), "Epic type should be SUBTASK.");
    }

    @Test
    void testSerializeCsvSubtaskObject() {
        final String expectedCsv = "1,SUBTASK,Test Subtask 1,NEW,Test Subtask 1 Description,2,2025-04-08T14:45:00,59";

        subtask1.setEpic(epic1);

        assertEquals(expectedCsv, subtask1.serializeCsv(), "Serialized CSV does not match expected format of Subtask object.");
    }

    @Test
    void testDeserializeCsvSubtask() throws InvalidManagerTaskException {
        String csvLine = "1,SUBTASK,Test Subtask 1,IN_PROGRESS,Test Subtask 1 Description,2,2025-04-08T15:47:00,31";
        final LocalDateTime startTime = LocalDateTime.of(2025, 4, 8, 15, 47);
        final Duration duration = Duration.ofMinutes(31);

        final Subtask subtask = (Subtask) deserialize(csvLine);

        assertAll("Deserialized Subtask fields",
                () -> assertEquals(1, subtask.getId(), "ID does not match."),
                () -> assertEquals("Test Subtask 1", subtask.getName(), "Name does not match."),
                () -> assertEquals("Test Subtask 1 Description", subtask.getDescription(), "Description does not match."),
                () -> assertEquals(Status.IN_PROGRESS, subtask.getStatus(), "Status does not match."),
                () -> assertEquals(startTime, subtask.getStartTime(), "StartTime does not match."),
                () -> assertEquals(duration, subtask.getDuration(), "Duration does not match.")
        );
    }

    @Test
    void testDeserializeCsvWithInvalidFormat() {
        String invalidCsvLine = "1,SUBTASK,Subtask 1";

        InvalidManagerTaskException exception = assertThrows(
                InvalidManagerTaskException.class,
                () -> deserialize(invalidCsvLine)
        );

        assertTrue(exception.getMessage().contains("Invalid CSV format in line:"), "Exception message should indicate invalid CSV format.");
    }

    @Test
    void testSerializeCsvWithSpecialCharacters() {
        final String expectedCsv = "1,SUBTASK,\"Subtask, 1\",IN_PROGRESS,\"Subtask 1, Description, with, commas,\",2,2025-04-08T15:47:00,31";

        final LocalDateTime startTime = LocalDateTime.of(2025, 4, 8, 15, 47);
        final Duration duration = Duration.ofMinutes(31);
        Subtask subtaskNew1 = new Subtask("Subtask, 1", "Subtask 1, Description, with, commas,", startTime, duration);
        subtaskNew1.setStatus(Status.IN_PROGRESS);
        subtaskNew1.setEpic(epic1);
        final Subtask subtask = subtaskNew1.copy(1);

        assertEquals(expectedCsv, subtask.serializeCsv(), "Serialized Subtask CSV with special characters does not match expected format.");
    }

    @Test
    void testDeserializeCsvWithSpecialCharacters() throws InvalidManagerTaskException {
        final String expectedCsvLineWithoutEpic = "1,SUBTASK,\"Subtask, 1\",NEW,\"Subtask 1 Description, with, commas\",,,";

        final Subtask subtask1 = (Subtask) deserialize(expectedCsvLineWithoutEpic);

        assertAll("Deserialized Subtask2 fields with special characters",
                () -> assertEquals(1, subtask1.getId(), "ID does not match."),
                () -> assertEquals("Subtask, 1", subtask1.getName(), "Name does not match."),
                () -> assertEquals("Subtask 1 Description, with, commas", subtask1.getDescription(), "Description does not match."),
                () -> assertEquals(Status.NEW, subtask1.getStatus(), "Status does not match."),
                () -> assertNull(subtask1.getEpic())
        );

        final String expectedCsvLineWithoutTime = "2,SUBTASK,\"Subtask, 2\",NEW,\"Subtask 2 Description, with, commas\",1,,";


        final Subtask subtask2 = (Subtask) deserialize(expectedCsvLineWithoutTime);

        assertAll("Deserialized Subtask2 fields with special characters",
                () -> assertEquals(2, subtask2.getId(), "ID does not match."),
                () -> assertEquals("Subtask, 2", subtask2.getName(), "Name does not match."),
                () -> assertEquals("Subtask 2 Description, with, commas", subtask2.getDescription(), "Description does not match."),
                () -> assertEquals(Status.NEW, subtask2.getStatus(), "Status does not match."),
                () -> assertNull(subtask2.getEpic(), "Epic must be null outside Taskmanager."),
                () -> assertNull(subtask2.getStartTime(), "StartTime is not null"),
                () -> assertEquals(Duration.ZERO, subtask2.getDuration(), "Duration is not Zero")
        );


        final String expectedCsvLineWithTime = "3,SUBTASK,\"Subtask, 3\",NEW,\"Subtask 3 Description, with, commas\",1,2000-01-01T00:00:00,31";

        final Subtask subtask3 = (Subtask) deserialize(expectedCsvLineWithTime);

        assertAll("Deserialized Subtask2 fields with special characters",
                () -> assertEquals(3, subtask3.getId(), "ID does not match."),
                () -> assertEquals("Subtask, 3", subtask3.getName(), "Name does not match."),
                () -> assertEquals("Subtask 3 Description, with, commas", subtask3.getDescription(), "Description does not match."),
                () -> assertEquals(Status.NEW, subtask1.getStatus(), "Status does not match."),
                () -> assertNull(subtask3.getEpic(), "Epic must be null outside Taskmanager."),
                () -> assertEquals(LocalDateTime.of(2000, 1, 1, 0, 0, 0), subtask3.getStartTime()),
                () -> assertEquals(Duration.ofMinutes(31), subtask3.getDuration())
        );
    }

}