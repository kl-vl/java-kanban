package ru.yandex.practicum.taskmanager.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Status;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;
import ru.yandex.practicum.taskmanager.service.exception.InvalidManagerTaskException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private Path tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() throws IOException {
         tempFile = Files.createTempFile("tasks", ".csv");
         return new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() throws Exception {
        // TODO
        //Files.deleteIfExists(tempFile);
    }

    @Test
    void testDeserializeTask() throws InvalidManagerTaskException {
        String csvLine = "1,TASK,Task 1,NEW,Description of Task 1,,,0";

        Task task = TaskDeserializer.deserialize(csvLine);

        assertAll("Task should be deserialized correctly",
                () -> assertEquals(1, task.getId(), "Task ID should match"),
                () -> assertEquals("Task 1", task.getName(), "Task name should match"),
                () -> assertEquals("Description of Task 1", task.getDescription(), "Task description should match"),
                () -> assertEquals(Status.NEW, task.getStatus(), "Task status should match")
        );
    }

    @Test
    void testSave() throws IOException, InvalidManagerTaskException {
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
                id,type,name,status,description,epic,start_time,duration
                """;

        Files.writeString(tempFile, testCsvData);

        String content = Files.readString(tempFile);
        assertEquals("id,type,name,status,description,epic,start_time,duration\n", content);

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

        System.out.println(tempFile.toAbsolutePath());

        assertAll(
                () -> assertEquals(4, lines.size(), "Header + 3 tasks"),
                () -> assertEquals("1,TASK,Task 1,NEW,Description of Task 1,,,", lines.get(1)),
                () -> assertEquals("2,EPIC,Epic 1,NEW,Description of Epic 1,,,", lines.get(2)),
                () -> assertEquals("3,SUBTASK,Subtask 1,NEW,Description of Subtask 1,2,,", lines.get(3))
        );
    }

    @Test
    void loadFromFile_ShouldLoadMultipleTasksFromFile() throws IOException {
        String testCsvData = """
                id,type,name,status,description,epic,start_time,duration
                1,TASK,Task 1,IN_PROGRESS,Description of Task 1,,,
                2,EPIC,Epic 1,DONE,Description of Epic 1,,,
                3,SUBTASK,Subtask 1,DONE,Description of Subtask 1,2,,
                4,SUBTASK,Subtask 2,DONE,Description of Subtask 2,2,,
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

    /**
     * Additional test (Sprint 7) instead of User scenario in static void main(String[] args) at FileBackedTaskManager class
     * * <li>Create multiple tasks, epics, and subtasks.</li>
     * * <li>Instantiate a new FileBackedTaskManager using the existing file.</li>
     * * <li>Ensure all previously created tasks/epics/subtasks are correctly loaded.</li>
     * * </ul>
     */
    @Test
    void testSavedAndLoadedManagersStatesAreEquals() throws IOException, InvalidManagerTaskException {
        Task task1 = new Task("Task 1", "Description of Task 1");
        Task task2 = new Task("Task 2", "Description of Task 2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description of Epic 2");
        int epicId1 = taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Optional<Epic> oEpic1 = taskManager.getEpicById(epicId1);
        epic1 = oEpic1.orElseThrow(() -> new AssertionError("Epic 1 not found"));

        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1");
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2");
        Subtask subtask3 = new Subtask("Subtask 3", "Description of Subtask 3");
        taskManager.addSubtask(subtask1, epic1);
        taskManager.addSubtask(subtask2, epic1);
        taskManager.addSubtask(subtask3, epic1);

        FileBackedTaskManager taskManagerLoaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertNotSame(taskManager, taskManagerLoaded);
        assertEquals(taskManager.getTasks(), taskManagerLoaded.getTasks());
        assertEquals(taskManager.getSubtasks(), taskManagerLoaded.getSubtasks());
        assertEquals(taskManager.getEpics(), taskManagerLoaded.getEpics());
    }

}