package ru.yandex.practicum.taskmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.service.exception.InvalidManagerTaskException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.taskmanager.service.TaskDeserializer.deserialize;

class EpicTest {

    private Epic epic1;
    private Subtask subtask1;
    private Subtask subtask2;
    final private LocalDateTime startTime1 = LocalDateTime.of(2025, 4, 8, 10, 12);;
    final private Duration duration1 = Duration.ofMinutes(59);
    final private LocalDateTime startTime2 = LocalDateTime.of(2025, 4, 8, 15, 47);
    final private Duration duration2 = Duration.ofMinutes(31);

    @BeforeEach
    void setUp() {
        epic1 = new Epic("Test Epic 1", "Test Epic 1 Description");
        epic1 = epic1.copy(1);

        subtask1 = new Subtask("Test Subtask 1", "Test Subtask Description 1", startTime1, duration1);
        subtask2 = new Subtask("Test Subtask 2", "Test Subtask Description 2", startTime2, duration2);
    }

    @Test
    void testEqualsWorkForEpicsWithSameId() {
        epic1.addSubtasksList(subtask1);

        Epic epicNew2 = new Epic("Test Epic 2", "Test Epic Description 2");
        final Epic epic2 = epicNew2.copy(1);
        epic2.addSubtasksList(subtask2);

        Epic epicNew3 = new Epic("Test Epic", "Test Epic Description");
        final Epic epic3 = epicNew3.copy(2);
        epic3.addSubtasksList(subtask1);

        assertAll("Epic objects equality comparison",
                () -> assertEquals(epic1, epic2, "Epics with same id should be equals"),
                () -> assertNotEquals(epic1, epic3, "Epics with different id and same fields should not be equals")
        );
    }

    @Test
    void testConstructorReturnProperlyInitializedEpicObject() {
        final Epic epic2 = new Epic("Test Epic 2", "Test Epic 2 Description");

        assertAll("Epic object should be correctly initialized",
                () -> assertNotNull(epic2, "Epic object should not be null after initialization"),
                () -> assertEquals("Test Epic 2", epic2.getName(), "Epic name should match the provided value"),
                () -> assertEquals("Test Epic 2 Description", epic2.getDescription(), "Epic description should match the provided value"),
                () -> assertEquals(Status.NEW, epic2.getStatus(), "Epic status should be NEW after initialization"),
                () -> assertEquals(0, epic2.getId(), "Epic ID should be 0 for a newly created object"),
                () -> assertTrue(epic2.getSubtasksList().isEmpty(), "Subtasks list should be empty for a newly created Epic"),
                () -> assertNull(epic2.getStartTime(), "Epic startTime should be null for a newly created Epic"),
                () -> assertNull(epic2.getEndTime(), "Epic endTime should be null for a newly created Epic"),
                () -> assertNull(epic2.getDuration(), "Epic suration should be null for a newly created Epic")
        );
    }

    @Test
    void testCopyReturnSameFieldsOfEpicObject() {
        epic1.setStatus(Status.IN_PROGRESS);
        epic1.addSubtasksList(subtask1);
        epic1.addSubtasksList(subtask2);

        Epic copiedEpic1 = epic1.copy();

        assertAll("Copy of Epic object should equal the original Epic object",
        () -> assertEquals(epic1.getId(), copiedEpic1.getId(), "Copied Epic ID should match the original Epic ID"),
                () -> assertEquals(epic1.getName(), copiedEpic1.getName(), "Copied Epic name should match the original Epic name"),
                () -> assertEquals(epic1.getDescription(), copiedEpic1.getDescription(), "Copied Epic description should match the original Epic description"),
                () -> assertEquals(epic1.getStatus(), copiedEpic1.getStatus(), "Copied Epic status should match the original Epic status"),
                () -> assertEquals(epic1.getSubtasksList().size(), copiedEpic1.getSubtasksList().size(), "Copied Epic subtasks list size should match the original Epic subtasks list size")
        );
    }

    @Test
    void testCopyIdReturnSameFieldsOfEpicObject() {
        epic1.setStatus(Status.IN_PROGRESS);
        epic1.addSubtasksList(subtask1);
        epic1.addSubtasksList(subtask2);
        Epic copiedEpic2 = epic1.copy(22);

        assertAll("Copy of Epic object with new ID should equal the original Epic object",
                () -> assertEquals(22, copiedEpic2.getId(), "Copied Epic with new ID should match 22"),
                () -> assertNotEquals(epic1.getId(), copiedEpic2.getId(), "Copied Epic with new ID should not match the original Epic ID"),
                () -> assertEquals(epic1.getName(), copiedEpic2.getName(), "Copied Epic name should match the original Epic name"),
                () -> assertEquals(epic1.getDescription(), copiedEpic2.getDescription(), "Copied Epic description should match the original Epic description"),
                () -> assertEquals(epic1.getStatus(), copiedEpic2.getStatus(), "Copied Epic status should match the original Epic status"),
                () -> assertEquals(epic1.getSubtasksList().size(), copiedEpic2.getSubtasksList().size(), "Copied Epic subtasks list size should match the original Epic subtasks list size")
        );
    }

    @Test
    void testCopyWithReturnSameFieldsOfEpicObject() {
        final Epic copiedEpic1 = epic1.copyWith("Test Epic 2", "Test Epic 2 Description", Status.DONE);

        assertAll("Copy with fields of Subtask object should have new values except ID",
                () -> assertEquals(epic1.getId(), copiedEpic1.getId(), "Copied Task ID should match the original subtask ID"),
                () -> assertEquals("Test Epic 2", copiedEpic1.getName(), "Copied Task name should match new name"),
                () -> assertEquals("Test Epic 2 Description", copiedEpic1.getDescription(), "Copied Task description should match new description"),
                () -> assertEquals(Status.DONE, copiedEpic1.getStatus(), "Copied Task status should match new status"),
                () -> assertNull(copiedEpic1.getStartTime(), "Copied Epic startTime be null"),
                () -> assertNull(copiedEpic1.getDuration(), "Copied Epic duration should be null")
        );
    }

    @Test
    void testSubtasksListWorkCorrectly() {
        subtask1 = subtask1.copy(2);
        subtask2 = subtask2.copy(3);

        epic1.addSubtasksList(subtask1);
        epic1.addSubtasksList(subtask2);

        final List<Subtask> subtasks = epic1.getSubtasksList();

        assertAll("Add to subtask list should work correctly",
                () -> assertEquals(2, subtasks.size(), "Subtasks list should contain 2 subtasks after adding two subtasks"),
                () -> assertTrue(subtasks.contains(subtask1), "Subtasks list should contain subtask1 after it was added"),
                () -> assertTrue(subtasks.contains(subtask2), "Subtasks list should contain subtask2 after it was added")
        );

        epic1.removeSubtask(subtask1);
        final List<Subtask> subtasks2 = epic1.getSubtasksList();

        assertAll("Remove from subtask list should work correctly",
                () -> assertEquals(1, subtasks2.size(), "Subtasks list should contain 1 subtask after removing subtask1"),
                () -> assertFalse(subtasks2.contains(subtask1), "Subtasks list should not contain subtask1 after it was removed"),
                () -> assertTrue(subtasks2.contains(subtask2), "Subtasks list should still contain subtask2 after removing subtask1")
        );

        epic1.removeSubtask(subtask2);
        final List<Subtask> subtasks3 = epic1.getSubtasksList();

        assertTrue(subtasks3.isEmpty(), "Subtasks list should be empty after removing all subtasks");

    }

    @Test
    void testGetSubtasksListReturnUnmodifiableList() {
        subtask1 = subtask1.copy(1);
        epic1.addSubtasksList(subtask1);

        List<Subtask> subtasks = epic1.getSubtasksList();

        assertThrows(UnsupportedOperationException.class, () -> subtasks.add(subtask2),
                "Adding a subtask to the returned list should throw an UnsupportedOperationException, as the list should be unmodifiable");
    }

    @Test
    void testHashCodeWorkCorrectly() {
        Epic epicNew2 = new Epic("Test Epic 2", "Test Epic 2 Description");
        final Epic epic2 = epicNew2.copy(1);

        Epic epicNew3 = new Epic("Test Epic 3", "Test Epic 3 Description");
        final Epic epic3 = epicNew3.copy(2);

        assertAll("Hashcode should correctly work for Epic",
                () -> assertEquals(epic1.hashCode(), epic2.hashCode(), "Epic with same id should return same hash code value"),
                () -> assertNotEquals(epic1.hashCode(), epic3.hashCode(), "Epic with different id should return different hash code values")
        );
    }

    @Test
    void testToStringWorkCorrectly() {
        String expected = "Epic{id=1, name='Test Epic 1', description='Test Epic 1 Description', status='NEW', startTime='', endTime='', duration=, subtasksList=[2]}";

        final Subtask newSubtask1 = subtask1.copy(2);
        epic1.addSubtasksList(newSubtask1);

        assertEquals(expected, epic1.toString(), "toString() method should return the correct string representation of the Epic object");
    }

    @Test
    void testGetTypeReturnEpicType() {
        assertEquals(Type.EPIC, epic1.getType(), "Epic type should be EPIC.");
    }

    @Test
    void testSerializeCsvEpicObject() {
        final String expectedCsv = "1,EPIC,Test Epic 1,NEW,Test Epic 1 Description,,,";

        assertEquals(expectedCsv, epic1.serializeCsv(), "Serialized CSV does not match expected format of Task object.");
    }

    @Test
    void testDeserializeCsvEpicObject() throws InvalidManagerTaskException {
        final String expectedCsvLine = "2,EPIC,Test Epic 2,NEW,Test Epic 2 Description,,,";
        final String expectedCsvWithTime = "3,EPIC,Test Epic 3,DONE,Test Epic 3 Description,4,2025-04-08T15:47:00,31";

        final Task epic2 = deserialize(expectedCsvLine);

        assertAll("Deserialized Epic fields",
                () -> assertEquals(2, epic2.getId(), "ID does not match."),
                () -> assertEquals("Test Epic 2", epic2.getName(), "Name does not match."),
                () -> assertEquals("Test Epic 2 Description", epic2.getDescription(), "Description does not match."),
                () -> assertEquals(Status.NEW, epic2.getStatus(), "Status does not match."),
                () -> assertNull(epic2.getStartTime(), "startTime is not null"),
                () -> assertNull(epic2.getDuration(), "Duration is not null")
        );

        final Task epic3 = deserialize(expectedCsvWithTime);

        assertAll("Deserialized Epic fields",
                () -> assertEquals(3, epic3.getId(), "ID does not match."),
                () -> assertEquals("Test Epic 3", epic3.getName(), "Name does not match."),
                () -> assertEquals("Test Epic 3 Description", epic3.getDescription(), "Description does not match."),
                () -> assertEquals(Status.DONE, epic3.getStatus(), "Status does not match."),
                () -> assertNull(epic3.getStartTime(), "startTime is not null"),
                () -> assertNull(epic3.getDuration(), "Duration is not null")
        );
    }

    @Test
    void testCalcEpicTimeAndDurationWorkCorrectly() {
        subtask1 = subtask1.copy(2);
        subtask2 = subtask2.copy(3);

        epic1.addSubtasksList(subtask1);
        epic1.addSubtasksList(subtask2);

        assertAll("Calc Epic startTime, endTime and Duration work correctly",
                () -> assertEquals(startTime1,epic1.calcStartTime(), "StartTime does not match."),
                () -> assertEquals(duration1.plus(duration2),epic1.calcDuration(), "Duration does not match."),
                () -> assertEquals(startTime2.plus(duration2),epic1.calcEndTime(), "EndTime does not match.")
        );
    }

    @Test
    void testSettersAndGettersWorkCorrectly() {
        epic1.setStatus(Status.DONE);
        epic1.setStartTime(startTime1);
        epic1.setDuration(duration1);
        epic1.setEndTime(startTime1.plus(duration1));

        assertAll("Getters should work correctly for modified Epic",
                () -> assertEquals(1, epic1.getId(), "Epic ID should remain unchanged and match the expected value"),
                () -> assertEquals(Status.DONE, epic1.getStatus(), "Epic status should be updated to DONE"),
                () -> assertEquals(startTime1, epic1.getStartTime(), "Epic startTime should be updated to start time"),
                () -> assertEquals(duration1, epic1.getDuration(), "Epic duration should be updated to duration"),
                () -> assertEquals(startTime1.plus(duration1), epic1.getEndTime(), "Epic endTime should be equals to startTime + duration")
        );
    }

}