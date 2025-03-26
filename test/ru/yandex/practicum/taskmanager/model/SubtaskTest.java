package ru.yandex.practicum.taskmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.service.exception.InvalidManagerTaskException;

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
        subtask1 = new Subtask("Test Subtask 1", "Test Subtask 1 Description");
        subtask1 = subtask1.copy(1);
        epic1 = new Epic("Test Epic 1", "Test Epic 1 Description");
        epic1 = epic1.copy(2);
    }

    @Test
    void equals_shouldReturnTrueForSubtasksWithSameId() {
        subtask1.setEpic(epic1);

        Subtask subtask2new = new Subtask("Test Subtask 2", "Test Subtask 2 Description");
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
    void constructor_shouldReturnProperlyInitializedSubtaskObject() {
        Subtask subtask2 = new Subtask("Test Subtask 2", "Test Subtask 2 Description");

        assertAll("Subtask should be properly initialized",
                () -> assertNotNull(subtask2, "Subtask object should not be null after initialization"),
                () -> assertEquals("Test Subtask 2", subtask2.getName(), "Subtask name should match the provided value"),
                () -> assertEquals("Test Subtask 2 Description", subtask2.getDescription(), "Subtask description should match the provided value"),
                () -> assertEquals(Status.NEW, subtask2.getStatus(), "Subtask status should be NEW after initialization"),
                () -> assertEquals(0, subtask2.getId(), "Subtask ID should be 0 for a newly created object"),
                () -> assertNull(subtask2.getEpic(), "Subtask should not be associated with an epic after initialization")
        );
    }

    @Test
    void copy_ShouldReturnSameFieldsOfSubtaskObject() {
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask1.setEpic(epic1);
        Subtask copiedSubtask1 = subtask1.copy();

        assertAll("Copy of Subtask object should equal the original Subtask object",
                () -> assertEquals(subtask1.getId(), copiedSubtask1.getId(), "Copied Subtask ID should match the original subtask ID"),
                () -> assertEquals(subtask1.getName(), copiedSubtask1.getName(), "Copied Subtask name should match the original subtask name"),
                () -> assertEquals(subtask1.getDescription(), copiedSubtask1.getDescription(), "Copied Subtask description should match the original subtask description"),
                () -> assertEquals(subtask1.getStatus(), copiedSubtask1.getStatus(), "Copied Subtask status should match the original subtask status"),
                () -> assertEquals(subtask1.getEpic(), copiedSubtask1.getEpic(), "Copied Subtask Epic should match the original subtask epic")
        );
    }

    @Test
    void copyWithId_ShouldReturnSameFieldsOfSubtaskObject() {
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask1.setEpic(epic1);
        Subtask copiedSubtask2 = subtask1.copy(33);

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
    void assignEpicToSubtaskShouldWorkCorrectly() {
        epic1 = epic1.copy(2);
        subtask1.setEpic(epic1);

        assertEquals(epic1, subtask1.getEpic(), "Subtask's Epic should be set to epic1");

        Epic epicNew2 = new Epic("New Epic", "Test Epic Description");
        final Epic epic2 = epicNew2.copy(3);
        subtask1.setEpic(epic2);

        assertEquals(epic2, subtask1.getEpic(), "Subtask's Epic should be updated to the new Epic");

    }

    @Test
    void hashCode_shouldWorkCorrectly() {
        Subtask subtaskNew2 = new Subtask("Test Subtask 2", "Test Subtask 2 Description");
        Subtask subtask2 = subtaskNew2.copy(1);

        Subtask subtaskNew3 = new Subtask("Test Subtask 3", "Test Subtask 3 Description");
        final Subtask subtask3 = subtaskNew3.copy(2);

        assertAll("Hashcode should correctly work for Subtask",
                () -> assertEquals(subtask1.hashCode(), subtask2.hashCode(), "Subtasks with same id should return same hash code value"),
                () -> assertNotEquals(subtask1.hashCode(), subtask3.hashCode(), "Subtasks with different id should return different hash code values")
        );
    }

    @Test
    void toString_ShouldWorkCorrectly() {
        subtask1.setStatus(Status.NEW);
        String expected1 = "Subtask{id=1, name='Test Subtask 1', description='Test Subtask 1 Description', status='NEW', epicId=}";

        assertEquals(expected1, subtask1.toString(), "The toString() method should return the correct string representation of the Subtask object without Epic");

        subtask1.setEpic(epic1.copy(2));
        String expected2 = "Subtask{id=1, name='Test Subtask 1', description='Test Subtask 1 Description', status='NEW', epicId=2}";

        assertEquals(expected2, subtask1.toString(), "toString() method should return the correct string representation of the Subtask object with Epic");
    }

    @Test
    void getType_ShouldReturnTaskType() {
        assertEquals(Type.SUBTASK, subtask1.getType(), "Epic type should be SUBTASK.");
    }

    @Test
    void serializeCsv_ShouldSerializeTaskObjectCorrectly() {
        String expectedCsv = "1,SUBTASK,Test Subtask 1,NEW,Test Subtask 1 Description,2";

        subtask1.setEpic(epic1);

        assertEquals(expectedCsv, subtask1.serializeCsv(), "Serialized CSV does not match expected format of Subtask object.");
    }

    @Test
    void testDeserializeCsv() throws InvalidManagerTaskException {
        String csvLine = "2,SUBTASK,Test Subtask 1,IN_PROGRESS,Test Subtask 1 Description,1";

        final Subtask subtask = (Subtask) deserialize(csvLine);

        assertAll("Deserialized Subtask fields",
                () -> assertEquals(2, subtask.getId(), "ID does not match."),
                () -> assertEquals("Test Subtask 1", subtask.getName(), "Name does not match."),
                () -> assertEquals("Test Subtask 1 Description", subtask.getDescription(), "Description does not match."),
                () -> assertEquals(Status.IN_PROGRESS, subtask.getStatus(), "Status does not match.")
        );
    }

    @Test
    void testDeserializeCsvWithInvalidFormat() {
        String invalidCsvLine = "1,SUBTASK,Subtask 1";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> deserialize(invalidCsvLine)
        );

        assertTrue(exception.getMessage().contains("Invalid CSV format"), "Exception message should indicate invalid CSV format.");
    }

    @Test
    void testSerializeCsvWithSpecialCharacters() {
        String expectedCsv = "1,SUBTASK,\"Subtask, 1\",IN_PROGRESS,\"Subtask 1, Description, with, commas\",2";

        Subtask subtask = new Subtask("Subtask, 1", "Subtask 1, Description, with, commas");
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setEpic(epic1);
        subtask = subtask.copy(1);

        assertEquals(expectedCsv, subtask.serializeCsv(), "Serialized Subtask CSV with special characters does not match expected format.");
    }

    @Test
    void testDeserializeCsvWithSpecialCharacters() throws InvalidManagerTaskException {
        String csvLine = "2,SUBTASK,\"Subtask, 1\",NEW,\"Subtask 1 Description, with, commas\",1";

        Subtask subtask = (Subtask) deserialize(csvLine);

        assertAll("Deserialized Subtask fields with special characters",
                () -> assertEquals(2, subtask.getId(), "ID does not match."),
                () -> assertEquals("Subtask, 1", subtask.getName(), "Name does not match."),
                () -> assertEquals("Subtask 1 Description, with, commas", subtask.getDescription(), "Description does not match."),
                () -> assertEquals(Status.NEW, subtask.getStatus(), "Status does not match.")
        );
    }

}