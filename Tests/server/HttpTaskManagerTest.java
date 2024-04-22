package server;

import Utility.Managers;
import httpServer.KVServer;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest {
    private KVServer server;
    private TaskManager manager;

    @BeforeEach
    public void createManager() {
        try {
            server = new KVServer();
            server.start();
            manager = Managers.getDefault("http://localhost:8078");
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при создании менеджера");
        }
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void shouldLoadTasks() {
        Task task1 = new Task("name1", "description1", TaskStatus.NEW);
        Task task2 = new Task("name2", "description2", TaskStatus.NEW);
        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        List<Task> list = manager.getHistoryTasks();
        assertEquals(manager.getTasks(), list);
    }

    @Test
    public void shouldLoadEpics() {
        Epic epic1 = new Epic("name1", "description1");
        Epic epic2 = new Epic("name2", "description2");
        manager.addNewEpic(epic1);
        manager.addNewEpic(epic2);
        assertEquals(epic1, manager.getEpic(epic1.getId()));
        assertEquals(epic2, manager.getEpic(epic2.getId()));
        List<Task> list = manager.getHistoryTasks();
        assertEquals(manager.getEpics(), list);
    }

    @Test
    public void shouldLoadSubtasks() {
        Epic epic1 = new Epic("description1", "name1");
        manager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("taskSUBepicNAME1", "taskOpisanieSUBepic1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 2, 0, 0), 1);
        Subtask subtask2 = new Subtask("taskSUBepicNAME2", "taskOpisanieSUBepic2", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 3, 0, 0), 1);
        Subtask subtask3 = new Subtask("taskSUBepicNAME3", "taskOpisanieSUBepic3", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 4, 0, 0), 1);
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);
        manager.getSubtask(subtask1.getId());
        manager.getSubtask(subtask2.getId());
        List<Task> list = manager.getHistoryTasks();
        assertEquals(manager.getSubtasks(), list);
    }

}