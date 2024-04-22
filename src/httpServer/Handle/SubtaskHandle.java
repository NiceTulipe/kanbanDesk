package httpServer.Handle;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Subtask;
import static java.net.HttpURLConnection.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static httpServer.Handle.WriteResponse.writeResponse;

public class SubtaskHandle implements HttpHandler {
    TaskManager taskManager;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson = new Gson();
    String response;

    public SubtaskHandle(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                getSubtask(exchange);
                break;
            case "POST":
                addOrUpdateSubtask(exchange);
                break;
            case "DELETE":
                deleteSubtask(exchange);
                break;
            default:
                writeResponse(exchange, "Такого операции не существует", 404);
        }
    }

    private void getSubtask(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            response = gson.toJson(taskManager.getSubtasks());
            writeResponse(exchange, response, HTTP_OK);
            return;
        }
        if (getTaskById(exchange).isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор!", HTTP_BAD_REQUEST);
            return;
        }
        int id = getTaskById(exchange).get();
        if (taskManager.getSubtasks().contains(taskManager.getSubtask(id))) {
            response = gson.toJson(taskManager.getSubtask(id));
        } else {
            writeResponse(exchange, "Задач с таким ID не найдена ", HTTP_BAD_REQUEST);
        }
        writeResponse(exchange, response, HTTP_OK);
    }



    private void addOrUpdateSubtask(HttpExchange exchange) throws IOException {
        try {
            InputStream json = exchange.getRequestBody();
            String jsonTask = new String(json.readAllBytes(), StandardCharsets.UTF_8);
            Subtask sub = gson.fromJson(jsonTask, Subtask.class);
            if (sub == null) {
                writeResponse(exchange, "Задача не должна быть пустой!", HTTP_BAD_REQUEST);
                return;
            }

            if ((sub.getId() != null) && taskManager.getSubtasks().contains(sub)) {
                taskManager.updateSubtask(sub);
                writeResponse(exchange, "Эпик обновлен!", 218);
                return;
            }
            taskManager.addNewSubtask(sub);
            writeResponse(exchange, "Задача успешно добавлена!", 201);

        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорректный JSON", HTTP_BAD_REQUEST);
        }
    }

    private void deleteSubtask(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            taskManager.deleteAllSubtasks();
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
        taskManager.deleteSubtask(id);
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
