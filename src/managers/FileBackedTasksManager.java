package managers;

import Utility.Managers;
import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;


public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private String path   ;

    public FileBackedTasksManager() {
    }
    private static File file;

    public FileBackedTasksManager(String path) {
        this.path = path;
        this.file = file;
    }

    static String regexCh = ";";


    public static FileBackedTasksManager loadFromFile(File file) {
        String path = file.getPath();
        FileBackedTasksManager fileManager = new FileBackedTasksManager(path);

        String content = readFileContentsOrNull(path);
        String[] lines = content.split("\r?\n");
        for (int i = 1; i < lines.length - 5; i++) {
            String oneString = lines[i];
            Task task = taskFromString(oneString);
            if (fileManager.nextId < task.getId()) {
                fileManager.nextId = task.getId() + 2;
            }
            switch (task.getType()) {
                case TASK:
                    fileManager.tasks.put(task.getId(), task);
                    fileManager.cheackPriority(task);
                    break;
                case EPIC:
                    fileManager.epics.put(task.getId(), (Epic) task);
                    fileManager.cheackPriority(task);
                    break;
                case SUBTASK:
                    fileManager.subtasks.put(task.getId(), (Subtask) task);
                    fileManager.cheackPriority(task);
                    break;
            }
        }
        for (Subtask subtask : fileManager.subtasks.values()) {
            Epic epic = fileManager.epics.get(subtask.getEpicId());
            epic.addSubTaskId(subtask.getId());
            fileManager.timeForEpic(epic);
        }


        try {
            List<Integer> history = historyFromString(lines[lines.length - 1]);
            if (history != null) {
                for (Integer taskId : history) {
                    if (fileManager.tasks.containsKey(taskId)) {
                        fileManager.add(fileManager.getTask(taskId));
                    } else if (fileManager.epics.containsKey(taskId)) {
                        fileManager.add(fileManager.getEpic(taskId));
                    } else {
                        fileManager.add(fileManager.getSubtask(taskId));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return fileManager;
    }


    public static Task taskFromString(String oneString) {
        String[] parts = oneString.split(regexCh);
        Integer id = Integer.valueOf(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String nameTask = String.valueOf(parts[2]);
        TaskStatus taskStatus = TaskStatus.valueOf(parts[3]);
        String taskDescription = String.valueOf(parts[4]);
        Long duration;
        if (parts[5].equals("null")) {
            duration = null;
        } else {
            duration = Long.valueOf(parts[5]);
        }
        LocalDateTime startTime;
        if (parts[6].equals("null")) {
            startTime = null;
        } else {
            startTime = LocalDateTime.parse(parts[6]);
        }
        switch (type) {
            case TASK:
                return new Task(id, nameTask, taskDescription, taskStatus, duration, startTime);

            case EPIC:
                return new Epic(id, nameTask, taskDescription, taskStatus);
            case SUBTASK:
                Integer epicId = Integer.parseInt(parts[8]);
                return new Subtask(id, nameTask, taskDescription, taskStatus, duration, startTime, epicId);
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

    }

    static List<Integer> historyFromString(String value) {
        if (value.isBlank()) {
            return null;
        } else {
            try {
                String[] idString = value.split(regexCh);
                List<Integer> ids = new ArrayList<>();
                for (int i = 0; i < idString.length; i++) {
                    int id = Integer.parseInt(idString[i]);
                    ids.add(id);
                }
                return ids;
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    private static String readFileContentsOrNull(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            throw new ManagerLoadException("Невозможно прочитать файл. Возможно, файл не находится в нужной директории.");
        }
    }

    public void save() {
        try (Writer fileCreator = new FileWriter(String.valueOf(file))) {
            fileCreator.write("id,type,name,status,description,duration.startTime, endTime, epic\n");

            List list = new ArrayList();

            {
                for (Task task : tasks.values()) {
                    list.add(task);
                }
                writeTasks(list, fileCreator);
                list.clear();
            }
            {
                for (Epic epic : epics.values()) {
                    list.add(epic);
                }
                writeTasks(list, fileCreator);
                list.clear();
            }
            {
                for (Subtask subtask : subtasks.values()) {
                    list.add(subtask);
                }
                writeTasks(list, fileCreator);
                list.clear();
            }
            fileCreator.write("\n");
            fileCreator.write("Список приоритетности");
            fileCreator.write("\n");
            fileCreator.write(prioritizedTasksToStringCSV(getPrioritizedTasks()));
            fileCreator.write("\n");
            fileCreator.write("Список истории");
            fileCreator.write("\n");
            fileCreator.write(historyToStringCSV((getHistoryTasks())));
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка записи файла.");
        }
    }

    private String historyToStringCSV(List<Task> historyTasks) {
        List<String> taskIds = new ArrayList<>();

        for (Task task : historyTasks) {
            taskIds.add(String.valueOf(task.getId()));
        }
        return String.join(regexCh, taskIds);
    }

    private String prioritizedTasksToStringCSV(List<Task> prioritizedTasks) {
        List<String> taskIds = new ArrayList<>();

        for (Task task : prioritizedTasks) {
            taskIds.add(String.valueOf(task.getId()));
        }
        return String.join(regexCh, taskIds);
    }

    private <T extends Task> void writeTasks(List<T> tasks, Writer writer) throws IOException {
        for (Task task : tasks) {
            writer.write(toStringParamCSV(task) + "\n");
        }
    }

    private String toStringParamCSV(Task task) {
        TaskType type = task.getType();
        String stringEnding = "";

        if (task instanceof Epic) {
        } else if (task instanceof Subtask) {
            stringEnding = stringEnding + ((Subtask) task).getEpicId();
        }
        String tasksCSV = task.getId() + regexCh +
                type + regexCh +
                task.getNameTask() + regexCh +
                task.getTaskStatus() + regexCh +
                task.getTaskDescription() + regexCh +
                task.getDuration() + regexCh +
                task.getStartTime() + regexCh +
                task.getEndTime() + regexCh;
        return tasksCSV + stringEnding;
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic newEpic) {
        int id = super.addNewEpic(newEpic);
        save();
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        super.updateEpic(updateEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtaskUpdate) {
        super.updateSubtask(subtaskUpdate);
        save();
    }

    @Override
    public void updateTask(Task taskUpdate) {
        super.updateTask(taskUpdate);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public ArrayList<Subtask> getSubTaskForEpic(int id) {
        return super.getSubTaskForEpic(id);
    }

    @Override
    public void cheackPriority(Task task) {
        super.cheackPriority(task);
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");


        FileBackedTasksManager manager = new FileBackedTasksManager("src/resourse/backUp.csv");
        Epic epic2 = new Epic("taskepicNAME2", "taskOpisanieepic2");
        int epicId2 = manager.addNewEpic(epic2);
        Task task1 = new Task("taskNAME1", "taskOpisanie1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 1, 0, 0));
        Task task2 = new Task("taskNAME2", "taskOpisanie2", TaskStatus.IN_PROGRESS,
                60L, LocalDateTime.of(2023, 1, 2, 8, 0));
        Task task3 = new Task("taskNAME3", "taskOpisanie3", TaskStatus.DONE,
                60L, LocalDateTime.of(2023, 1, 3, 9, 0));

        int taskId1 = manager.addNewTask(task1);
        int taskId2 = manager.addNewTask(task2);
        int taskId3 = manager.addNewTask(task3);

        Epic epic1 = new Epic("taskepicNAME1", "taskOpisanieepic1");
        Epic epic3 = new Epic("taskepicNAME3", "taskOpisanieepic3");

        int epicId1 = manager.addNewEpic(epic1);
        int epicId3 = manager.addNewEpic(epic3);

        Subtask SUBepic1 = new Subtask("taskSUBepicNAME1", "taskOpisanieSUBepic1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 2, 0, 0), 5);
        Subtask SUBepic2 = new Subtask("taskeSUBepicNAME2", "taskOpisanieSUBepic2", TaskStatus.DONE,
                60L, LocalDateTime.of(2023, 1, 3, 0, 0), 5);
        Subtask SUBepic3 = new Subtask("taskSUBepicNAME3", "taskOpisanieSUBepic3", TaskStatus.IN_PROGRESS,
                60L, LocalDateTime.of(2023, 1, 2, 12, 0), 5);

        int subtaskId1 = manager.addNewSubtask((Subtask) SUBepic1);
        int subtaskId2 = manager.addNewSubtask((Subtask) SUBepic2);
        int subtaskId3 = manager.addNewSubtask((Subtask) SUBepic3);
        System.out.println(manager.getPrioritizedTasks().toString());
        System.out.println(manager.getEpics());
        System.out.println(manager.getPrioritizedTasks().toString());
        System.out.println(manager.getPrioritizedTasks().toString());

        // обновление эпика
        Epic epic = manager.getEpic(epicId1);
        epic.setNameTask("так должно быть лучше");
        epic.setTaskDescription("описание обновлено");
        manager.updateEpic(epic);

        // обновление сабтаска
        Subtask subtask = manager.getSubtask(subtaskId1);
        subtask.setNameTask("обновление сабтаска");
        subtask.setTaskDescription("должно произойти чудо");
        subtask.setTaskStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask);

        // обновление таска
        Task task = manager.getTask(taskId1);
        task.setNameTask("обновление таска");
        task.setTaskDescription("спасение человечества");
        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task);
        manager.getTask(taskId1);
        manager.getEpic(epicId1);
        manager.getSubtask(subtaskId2);
        manager.getSubtask(subtaskId2);
        manager.getSubtask(subtaskId2);
        manager.getTask(taskId1);
        manager.getTask(taskId2);

        System.out.println(manager.getHistoryTasks());
        System.out.println(manager.getHistoryTasks().size());
        System.out.println(manager.getTasks());
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

        System.out.println("приоритетность до загрузки");
        System.out.println(manager.getPrioritizedTasks());
        FileBackedTasksManager manager2 = loadFromFile(new File("src/resourse/backUp.csv"));
        System.out.println(manager2.getTasks());
        System.out.println(manager2.getEpics());
        System.out.println(manager2.getSubtasks());
        System.out.println(manager2.getHistoryTasks());
        System.out.println(manager2.getHistoryTasks().size());
        Epic epicTest = new Epic("какой ид?", "айди?");
        Task taskTest = new Task("11?", "получилось айди № 11 id?", TaskStatus.NEW,
                120L, LocalDateTime.of(2023, 1, 4, 0, 0));
        manager2.addNewEpic(epicTest);
        manager2.addNewTask(taskTest);
        System.out.println(manager2.getEpics());
        System.out.println(manager.getEpics());
        System.out.println(manager2.getSubtasks());
        System.out.println(manager2.getTasks());
        System.out.println("приоритетность");
        System.out.println(manager2.getPrioritizedTasks());
        Task task2Cheack = new Task("1122?", "получилось айди № 11 id?", TaskStatus.NEW,
                120L, LocalDateTime.of(2023, 1, 4, 0, 0));
        manager2.addNewTask(task2Cheack);
        System.out.println(manager2.getPrioritizedTasks());
        System.out.println(manager2.getTasks());
    }
}
