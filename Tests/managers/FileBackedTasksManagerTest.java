package managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    String pathString = ("src/resourse/backUp.csv");
    public static final Path path = Path.of("src/resourse/backUp.csv");
    File file = new File(String.valueOf(path));

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTasksManager(pathString);
    }

    @Test
    public void saveAndLoadCorrect() {
        Task task1 = new Task("taskNAME1", "taskOpisanie1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 1, 0, 0));
        Task task2 = new Task("taskNAME2", "taskOpisanie2", TaskStatus.IN_PROGRESS,
                60L, LocalDateTime.of(2023, 1, 2, 8, 0));
        Task task3 = new Task("taskNAME3", "taskOpisanie3", TaskStatus.DONE,
                60L, LocalDateTime.of(2023, 1, 3, 9, 0));

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);
        taskManager.getTask(task1.getId());

        FileBackedTasksManager.loadFromFile(new File("src/resourse/backUp.csv"));
        assertEquals(List.of(task1, task2, task3), taskManager.getTasks());
    }

    @Test
    public void emptyTasksAndSubtasksOneEpicSaveAdnLoad() {
        Epic epic1 = new Epic("taskepicNAME2", "taskOpisanieepic2");
        taskManager.addNewEpic(epic1);


        FileBackedTasksManager.loadFromFile(new File("src/resourse/backUp.csv"));

        assertEquals(Collections.EMPTY_LIST, taskManager.getTasks());
        assertEquals(List.of(epic1), taskManager.getEpics());
        assertEquals(Collections.EMPTY_LIST, taskManager.getSubtasks());
    }

    @Test
    public void emptyHistoryTasksLoad() {
        FileBackedTasksManager.loadFromFile(new File("src/resourse/backUp.csv"));
        assertEquals(Collections.EMPTY_LIST, taskManager.getHistoryTasks());
    }

    @Test
    public void CheackTimeCrossingIsTrue() {
        Task task1 = new Task("taskNAME1", "taskOpisanie1", TaskStatus.NEW,
                120L, LocalDateTime.of(2023, 2, 1, 0, 0));
        Task task2 = new Task("taskNAME2", "taskOpisanie2", TaskStatus.IN_PROGRESS,
                60L, LocalDateTime.of(2023, 2, 1, 1, 0));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        Assertions.assertEquals(1, taskManager.getPrioritizedTasks().size());
    }

    @Test
    public void CheackTimeCrossingIsFalse() {
        Task task1 = new Task("taskNAME1", "taskOpisanie1", TaskStatus.NEW,
                120L, LocalDateTime.of(2023, 2, 1, 0, 0));
        Task task2 = new Task("taskNAME2", "taskOpisanie2", TaskStatus.IN_PROGRESS,
                60L, LocalDateTime.of(2023, 2, 1, 4, 0));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        Assertions.assertNotEquals(1, taskManager.getPrioritizedTasks().size());
    }


}