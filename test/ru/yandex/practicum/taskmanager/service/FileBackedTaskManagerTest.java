package ru.yandex.practicum.taskmanager.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Status;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;
import ru.yandex.practicum.taskmanager.service.exception.ManagerTaskNotFoundException;
import ru.yandex.practicum.taskmanager.service.exception.ManagerTaskNullException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest extends BaseTaskManagerTest<FileBackedTaskManager> {

    private Path tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() throws IOException {
         tempFile = Files.createTempFile("tasks", ".csv");
         return new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testDeserializeTask() {
        String csvLine = "1,TASK,Task 1,NEW,Description of Task 1,";

        Task task = TaskDeserializer.deserialize(csvLine);

        assertAll("Task should be deserialized correctly",
                () -> assertEquals(1, task.getId(), "Task ID should match"),
                () -> assertEquals("Task 1", task.getName(), "Task name should match"),
                () -> assertEquals("Description of Task 1", task.getDescription(), "Task description should match"),
                () -> assertEquals(Status.NEW, task.getStatus(), "Task status should match")
        );
    }

    @Test
    void testSave() throws IOException, ManagerTaskNullException, ManagerTaskNotFoundException {
        Task task = new Task("Task 1", "Description of Task 1");
        taskManager.addTask(task);

        Epic epic = new Epic("Epic 1", "Description of Epic 1");
        int epicId = taskManager.addEpic(epic);
        epic = epic.copy(epicId);

        Subtask subtask = new Subtask("Subtask 1", "Description of Subtask 1");
        taskManager.addSubtask(subtask, epic);

        List<String> csvLines = Files.readAllLines(tempFile, StandardCharsets.UTF_8);
        assertAll("Tasks should be saved correctly",
                () -> assertEquals(4, csvLines.size(), "Should have 4 lines (header + 3 tasks)"),
                () -> assertTrue(csvLines.get(1).contains("Task 1"), "Task 1 should be saved"),
                () -> assertTrue(csvLines.get(2).contains("Epic 1"), "Epic 1 should be saved"),
                () -> assertTrue(csvLines.get(3).contains("Subtask 1"), "Subtask 1 should be saved")
        );
    }


    @Test
    void shouldHandleEmptyFileSaveLoad() throws IOException {
        String testCsvData = """
                id,type,name,status,description,epic
                """;

        Files.writeString(tempFile, testCsvData);

        String content = Files.readString(tempFile);
        assertEquals("id,type,name,status,description,epic\n", content);

        FileBackedTaskManager taskManagerLoad = FileBackedTaskManager.loadFromFile(tempFile);
        List<Exception> listError = taskManagerLoad.getLoadErrorList();

        assertAll("New task manager should save empty file and be empty after load empty file",
                () -> assertTrue(listError.isEmpty(), "Load error should be empty"),
                () -> assertTrue(taskManager.getTasks().isEmpty()),
                () -> assertTrue(taskManager.getSubtasks().isEmpty()),
                () -> assertTrue(taskManager.getEpics().isEmpty())
        );
    }

    @Test
    void save_ShouldCorrectlySaveMultipleTasksToCsvFile() throws Exception {
        Task task = new Task("Task 1", "Description of Task 1");
        taskManager.addTask(task);

        Epic epicNew = new Epic("Epic 1", "Description of Epic 1");
        int epicNewId = taskManager.addEpic(epicNew);
        Epic epic = epicNew.copy(epicNewId);

        Subtask subtask = new Subtask("Subtask 1", "Description of Subtask 1");
        taskManager.addSubtask(subtask, epic);

        List<String> lines = Files.readAllLines(tempFile);

        assertAll(
                () -> assertEquals(4, lines.size(), "Header + 3 tasks"),
                () -> assertEquals("1,TASK,Task 1,NEW,Description of Task 1,", lines.get(1)),
                () -> assertEquals("2,EPIC,Epic 1,NEW,Description of Epic 1,", lines.get(2)),
                () -> assertEquals("3,SUBTASK,Subtask 1,NEW,Description of Subtask 1,2", lines.get(3))
        );
    }

    @Test
    void loadFromFile_ShouldLoadMultipleTasksFromFile() throws IOException {
        String testCsvData = """
                id,type,name,status,description,epic
                1,TASK,Task 1,IN_PROGRESS,Description of Task 1,
                2,EPIC,Epic 1,DONE,Description of Epic 1,
                3,SUBTASK,Subtask 1,DONE,Description of Subtask 1,2
                4,SUBTASK,Subtask 2,DONE,Description of Subtask 2,2
                """;

        Files.writeString(tempFile, testCsvData);

        FileBackedTaskManager taskManagerLoad = FileBackedTaskManager.loadFromFile(tempFile);
        List<Exception> errorList = taskManager.getLoadErrorList();

        assertAll("Task manager state verification after load CSV file",
                () -> assertTrue(errorList.isEmpty(), "Load error should be empty"),
                () -> assertEquals(1, taskManagerLoad.getTasks().size(), "Should contain 1 Task in the task list"),
                () -> assertEquals(1, taskManagerLoad.getEpics().size(), "Should contain 1 Epic in the epic list"),
                () -> assertEquals(2, taskManagerLoad.getSubtasks().size(), "Should contain 2 subtasks in the subtask list"),
                () -> assertEquals("Task 1", taskManagerLoad.getTaskById(1)
                        .orElseThrow(() -> new AssertionError("Task 1 not found"))
                        .getName(), "Task with ID=1 should have correct name"),
                () -> assertEquals("Epic 1", taskManagerLoad.getEpicById(2)
                        .orElseThrow(() -> new AssertionError("Epic 1 not found"))
                        .getName(), "Epic with ID=2 should have correct name"),
                () -> assertEquals("Subtask 2", taskManagerLoad.getSubtaskById(4)
                        .orElseThrow(() -> new AssertionError("Subtask 2 not found"))
                        .getName(), "Subtask with ID=4 should have correct name"),
                () -> assertEquals(2, taskManagerLoad.getSubtaskById(3)
                        .orElseThrow(() -> new AssertionError("Subtask 1 not found"))
                        .getEpic().getId(), "Subtask with ID=3 should be linked to epic with ID=2"),
                () -> assertEquals(2, taskManagerLoad.getSubtasksByEpicId(2).size(), "Epic with ID=2 should have 2 subtasks")
        );
    }

}