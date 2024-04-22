package managers;

import Utility.Managers;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyTasks = Managers.getDefaultHistory();
    protected Integer nextId = 1;
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, Subtask> subtasks;
    protected List<Task> emptyTimeTasksList;
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));

    Comparator<Task> comparator = new Comparator<>() {
        @Override
        public int compare(Task o1, Task o2) {

            return o1.getStartTime().compareTo(o2.getStartTime());
        }
    };


    public InMemoryTaskManager() {

        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.emptyTimeTasksList = new ArrayList<>();
    }
    // Создание заданий
    // обычные

    @Override
    public int addNewTask(Task task) {
        int id = task.setId(nextId++);
        tasks.put(task.getId(), task);
        cheackPriority(task);
        return id;
    }

    // эпики
    @Override
    public int addNewEpic(Epic newEpic) {
        int id = newEpic.setId(nextId++);
        epics.put(newEpic.getId(), newEpic);
        newEpic.setTaskStatus(TaskStatus.NEW);
        timeForEpic(newEpic);
        return id;
    }

    // сабЭпики
    @Override
    public Integer addNewSubtask(Subtask subtask) {
        int id = subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epics == null) {
            return null;
        }
        if (epics.get(subtask.getEpicId()) == null) {
            return null;
        }
        epic.addSubTaskId(subtask.getId());
        timeForEpic(epic);
        cheackPriority(subtask);
        updateEpicStatus(epic.getId());
        return id;
    }

    // Обновления заданий
    // Обновление эпиков
    @Override
    public void updateEpic(Epic updateEpic) {
        if (epics.containsKey(updateEpic.getId())) {
            epics.put(updateEpic.getId(), updateEpic);
            timeForEpic(updateEpic);
        }
    }

    // Обновление Сабтаск
    @Override
    public void updateSubtask(Subtask subtaskUpdate) {
        if (subtasks.containsKey(subtaskUpdate.getId()) && epics.containsKey(subtaskUpdate.getEpicId())) {
            subtasks.put(subtaskUpdate.getId(), subtaskUpdate);
            updateEpicStatus(subtaskUpdate.getEpicId());
            timeForEpic(getEpic(subtaskUpdate.getEpicId()));
            cheackPriority(subtaskUpdate);
        }
    }

    // Обновление таск
    @Override
    public void updateTask(Task taskUpdate) {
        if (tasks.containsKey((taskUpdate.getId()))) {
            tasks.put(taskUpdate.getId(), taskUpdate);
            cheackPriority(taskUpdate);
        }
    }

    // Полное удаление
    // Удаление таск
    @Override
    public void deleteAllTasks() {
        for (int tasksDelete : tasks.keySet()) {
            historyTasks.remove(tasksDelete);
        }
        for (Task tasks : tasks.values()) {

            prioritizedTasks.remove(tasks);
        }
        tasks.clear();
    }

    // Удаление Эпиков
    @Override
    public void deleteAllEpics() {
        for (int epicsDelete : epics.keySet()) {
            historyTasks.remove(epicsDelete);
        }
        epics.clear();
        for (int subtasksDelete : subtasks.keySet()) {
            historyTasks.remove(subtasksDelete);
        }
        subtasks.clear();
    }

    // Удаление сабЭпиков
    @Override
    public void deleteAllSubtasks() {
        for (int subEpicsDelete : subtasks.keySet()) {
            historyTasks.remove(subEpicsDelete);
        }
        for (Subtask subtasks : subtasks.values()) {

            prioritizedTasks.remove(subtasks);
        }
        subtasks.clear();
        for (Integer numbTaskId : epics.keySet()) {
            Epic dataEpic = epics.get(numbTaskId);
            dataEpic.getSubtaskIds().clear();
            timeForEpic(dataEpic);
        }
    }

    // Поиск по id
    // таски по id
    @Override
    public Task getTask(int id) {
        historyTasks.add(tasks.get(id));
        return tasks.get(id);
    }

    // Эпики по id
    @Override
    public Epic getEpic(int id) {
        historyTasks.add(epics.get(id));
        return epics.get(id);
    }

    // сабЭпики по id
    @Override
    public Subtask getSubtask(int id) {
        historyTasks.add(subtasks.get(id));
        return subtasks.get(id);
    }

    // Удаление конкретных задач по id
    // Удаление таск по id
    @Override
    public void deleteTask(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyTasks.remove(id);
    }

    // Удаление Эпиков по id
    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        historyTasks.remove(id);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
            historyTasks.remove(subtaskId);
        }
    }

    // Удаление сабтаск по id
    @Override
    public void deleteSubtask(int id) {

        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return;
        }
        ArrayList<Integer> reloadList = epic.getSubtaskIds();
        reloadList.remove(reloadList.indexOf(id));
        updateEpicStatus(subtask.getEpicId());
        prioritizedTasks.remove(subtasks.get(id));
        subtasks.remove(id);
        historyTasks.remove(id);
        timeForEpic(epic);

    }

    // Получение сабтаск для эпика
    @Override
    public ArrayList<Subtask> getSubTaskForEpic(int id) {
        Epic epic = epics.get(id);
        ArrayList<Integer> getSubsList = epic.getSubtaskIds();
        ArrayList<Subtask> list = new ArrayList<>();
        for (Integer i : getSubsList) {
            list.add(subtasks.get(i));
        }
        return list;
    }

    // ТЗ7 расчет для эпиков
    void timeForEpic(Epic epic) {
        LocalDateTime start = LocalDateTime.MAX;
        LocalDateTime end = LocalDateTime.MIN;
        long duration = 0L;
        List<Subtask> listOfSubs = getSubTaskForEpic(epic.getId());
        if (listOfSubs.size() > 0) {
            for (Subtask subtask : listOfSubs) {
                start = (subtask.getStartTime().isBefore(start)) ? subtask.getStartTime() : start;
                epic.setStartTime(start);
                end = (subtask.getEndTime().isAfter(end)) ? subtask.getEndTime() : end;
                epic.setEndTime(end);
                duration = (subtask.getDuration() > duration) ? subtask.getDuration() : duration;
                epic.setDuration(duration);
                emptyTimeTasksList.remove(epic);
            }
            } else {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
            emptyTimeTasksList.add(epic);
        }
    }

    // ТЗ7 приоритетность и проверка на выполнение только 1 задачи
    public List<Task> getPrioritizedTasks() {
        List<Task>listForTaskPrior = new ArrayList<>(prioritizedTasks);
        listForTaskPrior.addAll(emptyTimeTasksList);
        return listForTaskPrior;
    }

    public void cheackPriority(Task task) {
        boolean cheack = false;
        if (task.getStartTime() != null) {
            if (prioritizedTasks.isEmpty()) {
                prioritizedTasks.add(task);
            } else {
                for ( Task taskForCheack :prioritizedTasks) {
                    LocalDateTime startNewTask = task.getStartTime();
                    LocalDateTime endNewTask = task.getEndTime();
                    LocalDateTime startCurrent = taskForCheack.getStartTime();
                    LocalDateTime endCurrent = taskForCheack.getEndTime();
                     cheack = (startCurrent.isBefore(startNewTask) && endCurrent.isAfter(startNewTask))
                                || (startCurrent.isAfter(startNewTask) && startCurrent.isBefore(endNewTask)
                                || (startCurrent.equals(startNewTask)));
                    }
                if (!cheack) {
                    prioritizedTasks.add(task);
                  } else{
                    System.out.println("Задача " + task.getId() + " перескается с уже существующими");
                }
            }
        } else {
            emptyTimeTasksList.add(task);
        }
    }

    public void cheackNullTime(Task task) {
        if (task.getStartTime() == null) {
            emptyTimeTasksList.add(task);
        } else {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void add(Task task) {

    }

    @Override
    public List<Task> getHistoryTasks() {
        return historyTasks.getHistoryTasks();
    }

    // Работа со статусами эпиков
    private void updateEpicStatus(int id) {
        int sumNewTasks = 0;
        int sumDoneTasks = 0;
        Epic updateEpic = epics.get(id);
        ArrayList<Integer> subList = updateEpic.getSubtaskIds();
        for (Integer subListId : subList) {
            Subtask taskSubEpic = subtasks.get(subListId);
            TaskStatus statusSub = taskSubEpic.getTaskStatus();
            if (statusSub == TaskStatus.NEW) {
                sumNewTasks++;
            } else if (statusSub == TaskStatus.DONE) {
                sumDoneTasks++;
            }
        }
        if (sumNewTasks == subList.toArray().length) {
            updateEpic.setTaskStatus(TaskStatus.NEW);
        } else if (sumDoneTasks == subList.toArray().length) {
            updateEpic.setTaskStatus(TaskStatus.DONE);
        } else {
            updateEpic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
        timeForEpic(updateEpic);
    }

}

