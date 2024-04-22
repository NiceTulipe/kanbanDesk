package httpServer.Handle;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import static java.net.HttpURLConnection.*;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static httpServer.Handle.WriteResponse.writeResponse;

public class TaskHandle implements HttpHandler {
    TaskManager taskManager;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson = new Gson();
    String response;

    public TaskHandle(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                getTasks(exchange);
                break;
            case "POST":
                addOrUpdateTask(exchange);
                break;
            case "DELETE":
                deleteTask(exchange);
                break;
            default:
                writeResponse(exchange, "Такого операции не существует", 404);
        }
    }

    private void getTasks(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            response = gson.toJson(taskManager.getTasks());
            writeResponse(exchange, response, HTTP_OK);
            return;
        }
        if (getTaskById(exchange).isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор!", HTTP_BAD_REQUEST);
            return;
        }
        int id = getTaskById(exchange).get();
        if (taskManager.getTasks().contains(taskManager.getTask(id))) {
            response = gson.toJson(taskManager.getTask(id));
        } else {
            writeResponse(exchange, "Задач с таким ID не найдена ", HTTP_BAD_REQUEST);
        }
        writeResponse(exchange, response, HTTP_OK);
    }



    private void addOrUpdateTask(HttpExchange exchange) throws IOException {
        try {
            InputStream json = exchange.getRequestBody();
            String jsonTask = new String(json.readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(jsonTask, Task.class);
            if (task == null) {
                writeResponse(exchange, "Задача не должна быть пустой!", HTTP_BAD_REQUEST);
                return;
            }

            if ((task.getId() != null) && taskManager.getTasks().contains(task)) {
                taskManager.updateTask(task);
                writeResponse(exchange, "Эпик обновлен!", 218);
                return;
            }
            taskManager.addNewTask(task);
            writeResponse(exchange, "Задача успешно добавлена!", 201);

        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорректный JSON", HTTP_BAD_REQUEST);
        }
    }

    private void deleteTask(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            taskManager.deleteAllTasks();
            writeResponse(exchange, "Задачи успешно удалены!", HTTP_OK);
            return;
        }
        if (!getTaskById(exchange).isPresent()) {
            return;
        }
        int id = getTaskById(exchange).get();
        if (taskManager.getSubtasks().contains(taskManager.getSubtask(id))) {
            writeResponse(exchange, "Задач с таким id не найдено!", 404);
            return;
        }
        taskManager.deleteTask(id);
        writeResponse(exchange, "Задача успешно удалена!", HTTP_OK);
    }


    private Optional<Integer> getTaskById(HttpExchange exchange) {
        String[] parts = exchange.getRequestURI().getQuery().split("=");
        try {
            return Optional.of(Integer.parseInt(parts[1]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

}
