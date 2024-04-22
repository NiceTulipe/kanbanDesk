package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    int addNewTask(Task task);

    int addNewEpic(Epic newEpic);

    Integer addNewSubtask(Subtask subtask);

    void updateEpic(Epic updateEpic);

    void updateSubtask(Subtask subtaskUpdate);

    void updateTask(Task taskUpdate);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    ArrayList<Subtask> getSubTaskForEpic(int id);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    void add(Task task);

    public List<Task> getHistoryTasks();

    List<Task> getPrioritizedTasks();






}
