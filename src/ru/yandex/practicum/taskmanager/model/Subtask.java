package ru.yandex.practicum.taskmanager.model;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(Subtask subtask) {
        super(subtask);
        this.epic = subtask.epic;
    }

    @Override
    public Subtask copy() {
        return new Subtask(this);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + ((getEpic() != null) ? getEpic().getId() : "") +
                '}';
    }
}
