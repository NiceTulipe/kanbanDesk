package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    int id = 1;
    public int nextId() {
        return id++;
    }

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }
    protected Task testAddTask() {
        return new Task ("taskNAME3", "taskOpisanie3", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 3, 9, 0));
    }

    @Test
    void getEmptyHistory() {

        List<Task> history =  historyManager.getHistoryTasks();
        assertTrue(history.isEmpty());
    }

    @Test
    void getHistoryTasksSize1() {
    Task task1 = testAddTask();
    historyManager.add(task1);
        List<Task> history =  historyManager.getHistoryTasks();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void dontDoubledTaskAtHistory() {
        Task task1 = testAddTask();
        historyManager.add(task1);
        historyManager.add(task1);
        List<Task> history =  historyManager.getHistoryTasks();
        assertEquals(1, history.size());
    }

    @Test
    void deleteAtStartHistory() {
        Task task1 = new Task("taskNAME1", "taskOpisanie1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 1, 0, 0));
        Task task2 = new Task("taskNAME2", "taskOpisanie2", TaskStatus.IN_PROGRESS,
                60L, LocalDateTime.of(2023, 1, 2, 8, 0));
        Task task3 = new Task("taskNAME3", "taskOpisanie3", TaskStatus.DONE,
                60L, LocalDateTime.of(2023, 1, 3, 9, 0));
        int setIdAtTask1 = nextId();
        task1.setId(setIdAtTask1);
        int setIdAtTask2 = nextId();
        task2.setId(setIdAtTask2);
        int setIdAtTask3 = nextId();
        task3.setId(setIdAtTask3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getId());
        List<Task> history =  historyManager.getHistoryTasks();
        assertEquals(2,history.size());
        assertEquals(task2,history.get(0));
        assertEquals(task3,history.get(1));
    }

    @Test
    void deleteAtMidleHistory() {
        Task task1 = new Task("taskNAME1", "taskOpisanie1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 1, 0, 0));
        Task task2 = new Task("taskNAME2", "taskOpisanie2", TaskStatus.IN_PROGRESS,
                60L, LocalDateTime.of(2023, 1, 2, 8, 0));
        Task task3 = new Task("taskNAME3", "taskOpisanie3", TaskStatus.DONE,
                60L, LocalDateTime.of(2023, 1, 3, 9, 0));
        int setIdAtTask1 = nextId();
        task1.setId(setIdAtTask1);
        int setIdAtTask2 = nextId();
        task2.setId(setIdAtTask2);
        int setIdAtTask3 = nextId();
        task3.setId(setIdAtTask3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());
        List<Task> history =  historyManager.getHistoryTasks();
        assertEquals(2,history.size());
        assertEquals(task1,history.get(0));
        assertEquals(task3,history.get(1));
    }

    @Test
    void deleteAtEndHistory() {
        Task task1 = new Task("taskNAME1", "taskOpisanie1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 1, 0, 0));
        Task task2 = new Task("taskNAME2", "taskOpisanie2", TaskStatus.IN_PROGRESS,
                60L, LocalDateTime.of(2023, 1, 2, 8, 0));
        Task task3 = new Task("taskNAME3", "taskOpisanie3", TaskStatus.DONE,
                60L, LocalDateTime.of(2023, 1, 3, 9, 0));
        int setIdAtTask1 = nextId();
        task1.setId(setIdAtTask1);
        int setIdAtTask2 = nextId();
        task2.setId(setIdAtTask2);
        int setIdAtTask3 = nextId();
        task3.setId(setIdAtTask3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task3.getId());
        List<Task> history =  historyManager.getHistoryTasks();
        assertEquals(2,history.size());
        assertEquals(task1,history.get(0));
        assertEquals(task2,history.get(1));
    }
}