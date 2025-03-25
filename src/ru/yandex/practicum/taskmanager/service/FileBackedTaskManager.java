package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;
import ru.yandex.practicum.taskmanager.model.Type;
import ru.yandex.practicum.taskmanager.service.exception.ManagerLoadException;
import ru.yandex.practicum.taskmanager.service.exception.ManagerSaveException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    /**
     * CSV file header: id,type,name,status,description,epic and consists of 6 fields
     */
    private static final String CSV_HEADER = "id,type,name,status,description,epic";
    private final Path storage;
    private final TaskManagerHelper helper;

    public FileBackedTaskManager(Path storage) {
        super();
        this.storage = storage;
        helper = getHelper();
    }
    // TODO возможно тут нужно встроенное исключение может? Говорят такое редко бывает на стековерфлоу

    // TODO разобраться с IOException вида ManagerSaveException
    private List<Exception> load() {
        try {
            List<String> csvLines = Files.readAllLines(storage, StandardCharsets.UTF_8);
            return deserialize(csvLines);
        } catch (IOException e) {
            throw new ManagerLoadException("Failed to load CSV file to Task manager: " + storage, e);
        }

    }

    public List<Exception> load(Path filePath) {
        try {
            List<String> csvLines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            return deserialize(csvLines);
        } catch (IOException e) {
            throw new ManagerLoadException("Failed to load CSV file to Task manager: " + filePath, e);
        }
    }

    /**
     * Exact method signature to meet the technical requirements of the final task of Sprint 7
     */
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file.toPath());
        taskManager.load();
        return taskManager;
    }

    public List<String> serialize(List<Task> tasks) {
        List<String> csvLines = new ArrayList<>();
        csvLines.add(CSV_HEADER);
        for (Task task : tasks) {
            csvLines.add(task.serializeCsv());
        }
        return csvLines;
    }

    public List<Exception> deserialize(List<String> csvLines) {
        if (csvLines.isEmpty() || !csvLines.getFirst().trim().equalsIgnoreCase(CSV_HEADER)) {
            throw new IllegalArgumentException("Invalid CSV file header. Expected: " + CSV_HEADER);
        }
        List<Exception> errors = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        List<Task> subtasks = new ArrayList<>();
        int maxId = 0;

        for (int i = 1; i < csvLines.size(); i++) {
            Task task = TaskDeserializer.deserialize(csvLines.get(i), this);
            if (task.getType() == Type.EPIC || task.getType() == Type.TASK) {
                // Epics and Tasks are loaded first to maintain storage integrity before Subtasks with Epic link load
                try {
                    helper.addInternal(task);
                    tasks.add(task);
                } catch (IllegalArgumentException e) {
                    errors.add(e);
                }
            } else {
                subtasks.add(task);
            }
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
        }

        for (Task task : subtasks) {
            helper.addInternal(task);
            tasks.add(task);
            try {
                tasks.add(task);
            } catch (IllegalArgumentException e) {
                errors.add(e);
            }
        }
        helper.setIdCounter(maxId + 1);

        return errors;
    }

    void save() {
        try {
            List<Task> tasks = new ArrayList<>();
            tasks.addAll(getTasks());
            tasks.addAll(getEpics());
            tasks.addAll(getSubtasks());

            List<String> csvLines = serialize(tasks);

            Files.write(storage, csvLines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ManagerSaveException("Failed to save tasks to file: " + storage, e);
        }
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public int addTask(Task task) {
        int taskId = super.addTask(task);
        save();
        return taskId;
    }

    @Override
    public int addSubtask(Subtask subtask, Epic epic) {
        int subtaskId = super.addSubtask(subtask, epic);
        save();
        return subtaskId;
    }

    @Override
    public int addEpic(Epic epic) {
        int epicId = super.addEpic(epic);
        save();
        return epicId;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

}
