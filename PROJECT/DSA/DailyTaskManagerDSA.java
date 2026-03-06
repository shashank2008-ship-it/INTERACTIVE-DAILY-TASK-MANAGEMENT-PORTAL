import java.util.*;

class Task {
    long id;
    String title;
    String priority;
    String time;
    boolean completed;

    public Task(long id, String title, String priority, String time) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.time = time;
        this.completed = false;
    }

    @Override
    public String toString() {
        return "[" + (completed ? "✓" : " ") + "] "
                + title + " | Priority: " + priority
                + " | Time: " + (time.isEmpty() ? "No time" : time);
    }
}

// For Undo functionality
class Action {
    String type; // ADD, DELETE, TOGGLE
    String date;
    Task task;

    public Action(String type, String date, Task task) {
        this.type = type;
        this.date = date;
        this.task = task;
    }
}

public class DailyTaskManagerDSA {

    // HashMap<Date, LinkedList of Tasks>
    static HashMap<String, LinkedList<Task>> taskStorage = new HashMap<>();

    // Stack for Undo
    static Stack<Action> undoStack = new Stack<>();

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n=== Daily Task Manager (LinkedList + Stack) ===");
            System.out.println("1. Add Task");
            System.out.println("2. View Tasks");
            System.out.println("3. Toggle Complete");
            System.out.println("4. Delete Task");
            System.out.println("5. Undo Last Action");
            System.out.println("6. Exit");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> addTask();
                case 2 -> viewTasks();
                case 3 -> toggleTask();
                case 4 -> deleteTask();
                case 5 -> undoAction();
                case 6 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    static void addTask() {
        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = sc.nextLine();

        System.out.print("Enter Task Title: ");
        String title = sc.nextLine();

        System.out.print("Enter Priority (high/medium/low): ");
        String priority = sc.nextLine();

        System.out.print("Enter Time: ");
        String time = sc.nextLine();

        taskStorage.putIfAbsent(date, new LinkedList<>());

        Task newTask = new Task(System.currentTimeMillis(), title, priority, time);
        taskStorage.get(date).add(newTask);

        // Push to stack for undo
        undoStack.push(new Action("ADD", date, newTask));

        System.out.println("Task added!");
    }

    static void viewTasks() {
        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = sc.nextLine();

        LinkedList<Task> tasks = taskStorage.get(date);

        if (tasks == null || tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }

        int i = 1;
        for (Task t : tasks) {
            System.out.println(i++ + ". " + t);
        }
    }

    static void toggleTask() {
        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = sc.nextLine();

        LinkedList<Task> tasks = taskStorage.get(date);
        if (tasks == null || tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }

        viewTasks();

        System.out.print("Enter Task Number: ");
        int index = sc.nextInt() - 1;

        if (index >= 0 && index < tasks.size()) {
            Task task = tasks.get(index);
            task.completed = !task.completed;

            undoStack.push(new Action("TOGGLE", date, task));

            System.out.println("Task updated!");
        } else {
            System.out.println("Invalid number.");
        }
    }

    static void deleteTask() {
        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = sc.nextLine();

        LinkedList<Task> tasks = taskStorage.get(date);
        if (tasks == null || tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }

        viewTasks();

        System.out.print("Enter Task Number: ");
        int index = sc.nextInt() - 1;

        if (index >= 0 && index < tasks.size()) {
            Task removed = tasks.remove(index);

            undoStack.push(new Action("DELETE", date, removed));

            System.out.println("Task deleted!");
        } else {
            System.out.println("Invalid number.");
        }
    }

    static void undoAction() {

        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo!");
            return;
        }

        Action last = undoStack.pop();
        LinkedList<Task> tasks = taskStorage.get(last.date);

        switch (last.type) {

            case "ADD":
                tasks.remove(last.task);
                System.out.println("Undo: Task addition reverted.");
                break;

            case "DELETE":
                taskStorage.putIfAbsent(last.date, new LinkedList<>());
                taskStorage.get(last.date).add(last.task);
                System.out.println("Undo: Task restored.");
                break;

            case "TOGGLE":
                last.task.completed = !last.task.completed;
                System.out.println("Undo: Toggle reverted.");
                break;
        }
    }
}