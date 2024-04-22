package tasks;

import org.junit.jupiter.api.Test;
import managers.TaskManager;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.TaskStatus;
import tasks.Subtask;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStatus.*;

class EpicTest {
    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }
    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        final int taskId = taskManager.addNewTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewEpicAndCheckStatusINPROGRESS() {
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.addNewEpic(epic);
        Subtask SUBepic1 = new Subtask("taskSUBepicNAME1", "taskOpisanieSUBepic1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 2, 0, 0), taskId);
        Subtask SUBepic2 = new Subtask("taskSUBepicNAME21", "taskOpisanieSUBepic1", TaskStatus.IN_PROGRESS,
                60L, LocalDateTime.of(2023, 1, 3, 0, 0), taskId);
        Subtask SUBepic3 = new Subtask("taskSUBepicNAME33", "taskOpisanieSUBepic1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 4, 0, 0), taskId);

        taskManager.addNewSubtask(SUBepic1);
        taskManager.addNewSubtask(SUBepic3);
        taskManager.addNewSubtask(SUBepic2);
        assertEquals(IN_PROGRESS, epic.getTaskStatus());
    }
    @Test
    void addNewEpicAndCheckStatusNEW() {
        Epic epic2 = new Epic("Test addNewTask", "Test addNewTask description");
        final int taskId2 = taskManager.addNewEpic(epic2);

        Subtask SUBepic4 = new Subtask("taskSUBepicNAME33", "taskOpisanieSUBepic1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 2, 0, 0), taskId2);
        Subtask SUBepic6 = new Subtask("taskSUBepicNAME33", "taskOpisanieSUBepic1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 2, 0, 0), taskId2);
        taskManager.addNewSubtask(SUBepic4);
        taskManager.addNewSubtask(SUBepic6);
        assertEquals(NEW, epic2.getTaskStatus());
        assertEquals(TaskType.EPIC,epic2.getType());
    }

    @Test
    void addNewEpicAndCheckTypeEPIC() {
        Epic epic2 = new Epic("Test addNewTask", "Test addNewTask description");
        taskManager.addNewEpic(epic2);
        assertEquals(TaskType.EPIC,epic2.getType());
    }
    @Test
    void addNewEpicAndCheckStatusDone() {
        Epic epic2 = new Epic("Test addNewTask", "Test addNewTask description");
        final int taskId2 = taskManager.addNewEpic(epic2);

        Subtask SUBepic4 = new Subtask("taskSUBepicNAME33", "taskOpisanieSUBepic1", TaskStatus.DONE,
                60L, LocalDateTime.of(2023, 1, 2, 0, 0), taskId2);
        Subtask SUBepic6 = new Subtask("taskSUBepicNAME33", "taskOpisanieSUBepic1", TaskStatus.DONE,
                60L, LocalDateTime.of(2023, 1, 2, 0, 0), taskId2);
        taskManager.addNewSubtask(SUBepic4);
        taskManager.addNewSubtask(SUBepic6);
        assertEquals(DONE, epic2.getTaskStatus());
        assertEquals(TaskType.EPIC,epic2.getType());
        assertEquals(TaskType.SUBTASK,SUBepic4.getType());
    }


}