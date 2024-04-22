package Utility;

import managers.*;

import java.io.IOException;

public abstract class Managers {
    static public HttpTaskManager getDefault(String URL) throws IOException, InterruptedException {
        return new HttpTaskManager(URL);
    }

    static public TaskManager getInMemoryTaskManger() {
        return new InMemoryTaskManager();
    }

    static public HistoryManager getHistoryManager() {
        return new InMemoryHistoryManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
