package managers;

import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    TaskManager taskManager;


    protected Epic testAddEpic() {
        return new Epic("taskepicNAME1", "taskOpisanieepic1");
    }

    protected Task testAddTask() {
        return new Task("taskNAME3", "taskOpisanie3", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 3, 9, 0));
    }

    protected Subtask testAddSubTask() {
        return new Subtask("taskSUBepicNAME1", "taskOpisanieSUBepic1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 2, 0, 0), 1);
    }


    @Test
    void getHistoryTasks() {
        Task task = testAddTask();
        taskManager.addNewTask(task);
        Epic epic = testAddEpic();
        taskManager.addNewEpic(epic);

        taskManager.getTask(task.getId());
        taskManager.getEpic(epic.getId());

        List<Task> history = taskManager.getHistoryTasks();

        assertEquals(2, history.size());
    }

    @Test
    void returnEmptyHistory() {
        List<Task> history = taskManager.getHistoryTasks();
        assertTrue(history.isEmpty());
    }

    @Test
    void testAddNewTask() {
        Task task = testAddTask();
        final int taskId = taskManager.addNewTask(task);
        final Task newTask = taskManager.getTask(taskId);
        assertNotNull(newTask, "Такой задачи нет");
        assertEquals(task, newTask, "Задачи разные");
        final List<Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size(), "Количество задач неверное");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
        assertNotNull(tasks, "Не возвращаются задачи");
    }

    @Test
    void testAddNewSubtask() {
        Epic epic = testAddEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("taskSUBepicNAME1", "taskOpisanieSUBepic1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 2, 0, 0), 1);
        final int subtaskId = taskManager.addNewSubtask(subtask);
        final Subtask newSubtask = taskManager.getSubtask(subtaskId);
        assertNotNull(newSubtask, "Нет данной сабтаски");
        assertEquals(subtask, newSubtask, "Сабтаски разные");
        final List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(1, subtasks.size(), "Количество сабтаск неверное");
        assertEquals(subtask, subtasks.get(0), "Сабтаски не совпадают.");
        assertNotNull(subtasks, "Не возвращаются сабтаски");
    }

    @Test
    void testAddNewEpic() {
        Epic epic = testAddEpic();
        final int epicId = taskManager.addNewEpic(epic);
        final Epic newEpic = taskManager.getEpic(epicId);
        assertNotNull(newEpic, "Эпик не найден");
        assertEquals(epic, newEpic, "Эпики не совпадают");
        final List<Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
        assertNotNull(epics, "Эпики не возвращаются");
    }

    @Test
    void testRemoveTask() {
        Task task = testAddTask();
        final int taskId = taskManager.addNewTask(task);
        final Task newTask = taskManager.getTask(taskId);
        assertEquals(1,taskManager.getTasks().size(),"Неверное количество задач");
        taskManager.deleteTask(taskId);
        assertEquals(0,taskManager.getTasks().size(),"Неверно работает удаление");
    }


    @Test
    void testRemoveSubtask() {
        Epic epic = testAddEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("taskSUBepicNAME1", "taskOpisanieSUBepic1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 2, 0, 0), 1);
        final int subtaskId = taskManager.addNewSubtask(subtask);
        final Subtask newSubtask = taskManager.getSubtask(subtaskId);
        assertEquals(1,taskManager.getSubtasks().size(),"Неверное количество задач");
        taskManager.deleteSubtask(subtaskId);
        assertEquals(0,taskManager.getSubtasks().size(),"Неверно работает удаление");
    }

    @Test
    void testRemoveEpic() {
        Epic epic = testAddEpic();
        final int epicID = taskManager.addNewEpic(epic);
        final Epic newEpic = taskManager.getEpic(epicID);
        assertEquals(1,taskManager.getEpics().size(),"Неверное количество задач");
        taskManager.deleteEpic(epicID);
        assertEquals(0,taskManager.getEpics().size(),"Неверно работает удаление");
    }

    @Test
    void testRemoveAllTask() {
        Task task1 = new Task("taskNAME1", "taskOpisanie1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 1, 0, 0));
        Task task2 = new Task("taskNAME2", "taskOpisanie2", TaskStatus.IN_PROGRESS,
                60L, LocalDateTime.of(2023, 1, 2, 8, 0));
        Task task3 = new Task("taskNAME3", "taskOpisanie3", TaskStatus.DONE,
                60L, LocalDateTime.of(2023, 1, 3, 9, 0));

        final int taskId1 = taskManager.addNewTask(task1);
        final Task newTask1 = taskManager.getTask(taskId1);
        final int taskId2 = taskManager.addNewTask(task2);
        final Task newTask2 = taskManager.getTask(taskId2);
        final int taskId3 = taskManager.addNewTask(task3);
        final Task newTask3 = taskManager.getTask(taskId3);
        assertEquals(3,taskManager.getTasks().size(),"Неверное количество задач");
        taskManager.deleteAllTasks();
        assertEquals(0,taskManager.getTasks().size(),"Неверно работает удаление");
    }

    @Test
    void testRemoveAllSubtask() {
        Epic epic = testAddEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("taskSUBepicNAME1", "taskOpisanieSUBepic1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 2, 0, 0), 1);
        Subtask subtask2 = new Subtask("taskSUBepicNAME2", "taskOpisanieSUBepic2", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 3, 0, 0), 1);
        Subtask subtask3 = new Subtask("taskSUBepicNAME3", "taskOpisanieSUBepic3", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 4, 0, 0), 1);
        final int subtaskId1 = taskManager.addNewSubtask(subtask1);
        final Subtask newSubtask1 = taskManager.getSubtask(subtaskId1);
        final int subtaskId2 = taskManager.addNewSubtask(subtask2);
        final Subtask newSubtask2 = taskManager.getSubtask(subtaskId2);
        final int subtaskId3 = taskManager.addNewSubtask(subtask3);
        final Subtask newSubtask3 = taskManager.getSubtask(subtaskId3);
        assertEquals(3,taskManager.getSubtasks().size(),"Неверное количество задач");
        taskManager.deleteAllSubtasks();
        assertEquals(0,taskManager.getSubtasks().size(),"Неверно работает удаление");
    }

    @Test
    void testRemoveAllEpic() {
        Epic epic1 = testAddEpic();
        Epic epic2 = new Epic("taskepicNAME1", "taskOpisanieepic1");
        Epic epic3 = new Epic("taskepicNAME3", "taskOpisanieepic3");
        final int epicID1 = taskManager.addNewEpic(epic1);
        final Epic newEpic1 = taskManager.getEpic(epicID1);
        final int epicID2 = taskManager.addNewEpic(epic2);
        final Epic newEpic2 = taskManager.getEpic(epicID2);
        final int epicID3 = taskManager.addNewEpic(epic3);
        final Epic newEpic3 = taskManager.getEpic(epicID3);
        assertEquals(3,taskManager.getEpics().size(),"Неверное количество задач");
        taskManager.deleteAllEpics();
        assertEquals(0,taskManager.getEpics().size(),"Неверно работает удаление");
    }

    @Test
    void testUpdateTask() {
        Task task = testAddTask();
        final int taskId = taskManager.addNewTask(task);
        Task task1 = new Task(taskId,"test1", "reloaded", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 1, 0, 0));
        taskManager.updateTask(task1);
        assertNotEquals(task.toString(),taskManager.getTasks().get(0));
    }
    @Test
    void testUpdateSubtask() {
        Epic epic = testAddEpic();
        final int epicId = taskManager.addNewEpic(epic);
        taskManager.getEpic(epicId);
        Subtask subtask = testAddSubTask ();
        final int subtaskId =  taskManager.addNewSubtask(subtask);
        Subtask SUBepic1 = new Subtask(subtaskId,"testsss", "taskOpisanieSUBepic1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 2, 0, 0), epicId);
        taskManager.updateSubtask(SUBepic1);
        assertNotEquals(subtask.toString(), taskManager.getSubtasks().get(0));
    }

    @Test
    void testUpdateEpic() {
        Epic epic = testAddEpic();
        final int epicId = taskManager.addNewEpic(epic);
        Epic epic2 = new Epic(epicId,"taskepicNAME1", "taskOpisanieepic1");
        taskManager.updateEpic(epic2);
        assertNotEquals(epic.toString(),taskManager.getEpics().get(0));
    }

    @Test
    void testGetSubtaskForEpic() {
        Epic epic = testAddEpic();
        final int  epicID = taskManager.addNewEpic(epic);
        final Epic newEpic = taskManager.getEpic(epicID);
        Subtask SUBepic1 = new Subtask("taskSUBepicNAME1", "taskOpisanieSUBepic1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 2, 0, 0), epic.getId());
        Subtask SUBepic2 = new Subtask("taskeSUBepicNAME2", "taskOpisanieSUBepic2", TaskStatus.DONE,
                60L, LocalDateTime.of(2023, 1, 3, 0, 0), epic.getId());
        Subtask SUBepic3 = new Subtask("taskSUBepicNAME3", "taskOpisanieSUBepic3", TaskStatus.IN_PROGRESS,
                60L, LocalDateTime.of(2023, 1, 2, 12, 0), epic.getId());
        final int subId1 = taskManager.addNewSubtask(SUBepic1);
        taskManager.getSubtask(subId1);
        final int subId2 = taskManager.addNewSubtask(SUBepic2);
        taskManager.getSubtask(subId2);
        final int subId3  = taskManager.addNewSubtask(SUBepic3);
        taskManager.getSubtask(subId3);
        assertEquals(3,taskManager.getEpic(epicID).getSubtaskIds().size());
    }

}