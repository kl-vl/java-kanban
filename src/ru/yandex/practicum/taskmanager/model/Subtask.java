package ru.yandex.practicum.taskmanager.model;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String name, String description) {

        super(name, description);
    }

    public Subtask(Subtask other) {
        super(other);
        this.epic = other.epic;
    }

    @Override
    public Subtask copy() {
        return new Subtask(this);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Epic cannot be null.");
            return;
        }
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
