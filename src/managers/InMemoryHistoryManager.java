package managers;
import tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> queueTasks = new HashMap<>();

    private Node head;

    private Node tail;

    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        linkLast(task);
        queueTasks.put(task.getId(), tail);
    }

    @Override
    public void remove(int id) {
        removeNode(queueTasks.get(id));
    }

    public List<Task> getHistoryTasks() {
        return getTasks();
    }

    private void removeNode(Node node) {
        if (node != null) {
            if (node == head && node == tail) {
                head = null;
                tail = null;
            } else if (node == head && node != tail) {
                head = head.next;
                head.prev = null;
            } else if (node != head && node == tail) {
                tail = tail.prev;
                tail.next = null;
            } else {
                if (node.prev != null) {
                    node.prev.next = node.next;
                }
                if (node.next != null) {
                    node.next.prev = node.prev;
                }
            }
        }
    }



    public void linkLast(Task task) {
        final Node newNode = new Node(task, tail, null);
        if (head == null) {
            head = newNode;
        }
        if (tail != null) {
            tail.next = newNode;
        }
        tail = newNode;
    }

    private List<Task> getTasks() {
        List<Task> historyTasks = new ArrayList<>();
        Node curNode = head;
        while (curNode != null) {
            historyTasks.add(curNode.data);
            curNode = curNode.next;
        }
        return historyTasks;
    }

    class Node {
        Task data;
        Node next;
        Node prev;

        public Node(Task data, Node prev, Node next)  {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}

