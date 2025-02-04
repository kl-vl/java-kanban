package ru.yandex.practicum.taskmanager.model;

public class Subtask extends Task {
    // TODO Для каждой подзадачи известно, в рамках какого эпика она выполняется.
    private int epicId;

    /*public Subtask(String name, String description, int id, int epicId) {
        super(name, description, id);
        this.epicId = epicId;
    }*/

    // TODO привязать Epic нужно внутри TaskManager
    public Subtask(final String name, final String description) {
        super(name, description);
        //this.epicId = epicId;
    }

    public Subtask(final Subtask subtask) {
        super(subtask);
        this.epicId = subtask.epicId;
    }

    // TODO проверить использования
    public int getEpicId() {
        return epicId;
    }

    // TODO проверить использование
//    public void setEpicId(int epicId) {
//        this.epicId = epicId;
//    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + getEpicId() +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
