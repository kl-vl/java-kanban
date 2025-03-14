package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, HistoryNode> tasksHistory = new HashMap<>();
    private HistoryNode first;
    private HistoryNode last;
    private static final InMemoryHistoryManager instance = new InMemoryHistoryManager();

    private InMemoryHistoryManager() {
    }

    public static InMemoryHistoryManager getInstance() {
        return instance;
    }

    @Override
    public void clearHistory() {
        tasksHistory.clear();
        first = null;
        last = null;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        HistoryNode newNode = linkLast(task.copy());
        tasksHistory.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        if (!tasksHistory.containsKey(id)) {
            return;
        }
        HistoryNode oldNode = tasksHistory.remove(id);
        removeNode(oldNode);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasksHistory = new ArrayList<>();
        HistoryNode node = first;
        while (node != null) {
            if (node.item != null) {
                tasksHistory.add(node.item.copy());
            }
            node = node.next;
        }
        return tasksHistory;
    }

    private void removeNode(HistoryNode node) {
        if (node == null) {
            return;
        }

        HistoryNode prevNode = node.prev;
        HistoryNode nextNode = node.next;

        if (prevNode != null) {
            prevNode.next = nextNode;
        } else {
            first = nextNode;
        }

        if (nextNode != null) {
            nextNode.prev = prevNode;
        } else {
            last = prevNode;
        }

        node.prev = null;
        node.next = null;
        node.item = null;
    }

    private HistoryNode linkLast(Task task) {
        if (task == null) {
            return null;
        }
        final HistoryNode l = last;
        final HistoryNode newNode = new HistoryNode(l, task, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        return newNode;
    }

    private static class HistoryNode {
        Task item;
        HistoryNode next;
        HistoryNode prev;

        HistoryNode(HistoryNode prev, Task task, HistoryNode next) {
            this.item = task;
            this.next = next;
            this.prev = prev;
        }
    }

}
