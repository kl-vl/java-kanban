package ru.yandex.practicum.taskmanager.model;

public class Subtask extends Task {
    // TODO Для каждой подзадачи известно, в рамках какого эпика она выполняется.
    private int epicId;

    /*public Subtask(String name, String description, int id, int epicId) {
        super(name, description, id);
        this.epicId = epicId;
    }*/

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    // TODO проверить использования
    public int getEpicId() {
        return epicId;
    }

    // TODO проверить использования
//    public void setEpicId(int epicId) {
//        this.epicId = epicId;
//    }

    @Override
    public String toString() {
        // TODO json
        return "{ \"Subtask\" : {" +
                "\"id\" : " + getId() +
                ", \"name\" : \"" + getName() +
                "\", \"description\" : \"" + getDescription() +
                "\", \"status\" : \"" + getStatus() +
                "\", epicId : " + epicId +
                "}}";
    }
}
