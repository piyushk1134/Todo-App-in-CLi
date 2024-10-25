import java.util.Map;
import java.util.Scanner;

public class TodoApp {
    private static final TaskManager taskManager = new TaskManager();
    private static final UserManager userManager = new UserManager();
    private static final Scanner scanner = new Scanner(System.in);
    private static int userId = -1;  // Stores the logged-in user's ID
    private static final int TASK_NAME_MAX_LENGTH = 20;
    private static final int DESCRIPTION_MAX_LENGTH = 30;

    public static void main(String[] args) {
        System.out.println("Welcome to the Todo Application!");

        if (authenticateUser()) {
            while (true) {
                displayTodoTable();

                System.out.print("\nChoose an option (Add, completed, remove, close): ");
                String input = scanner.nextLine();

                if (input.startsWith("Add")) {
                    addTask();
                } else if (input.startsWith("completed")) {
                    markTaskDone(input);
                } else if (input.startsWith("remove")) {
                    deleteTask(input);
                } else if (input.equals("close")) {
                    System.out.println("👋 Exiting... Have a productive day!");
                    break;
                } else {
                    System.out.println("⚠️ Invalid input. Try again.");
                }
            }
        } else {
            System.out.println("Authentication failed. Exiting application.");
        }
        scanner.close();
    }

    private static boolean authenticateUser() {
        System.out.println("Please choose an option:\n1. Sign Up\n2. Log In");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        return switch (choice) {
            case 1 -> signUp();
            case 2 -> login();
            default -> {
                System.out.println("⚠️ Invalid option.");
                yield false;
            }
        };
    }

    private static boolean signUp() {
        System.out.print("Enter a username: ");
        String username = scanner.nextLine();
        System.out.print("Enter a password: ");
        String password = scanner.nextLine();

        if (userManager.signUp(username, password)) {
            System.out.println("Sign up successful! Please log in.");
            return login();
        } else {
            System.out.println("⚠️ Sign up failed. Try a different username.");
            return false;
        }
    }

    private static boolean login() {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        if (userManager.login(username, password)) {
            userId = userManager.getUserId(username, password); // Fetch userId
            if (userId != -1) {
                System.out.println("Login successful! Welcome, " + username);
                taskManager.loadTasksFromDatabase(userId); // Load user-specific tasks
                return true;
            }
        } else {
            System.out.println("⚠️ Invalid credentials. Please try again.");
        }
        return false;
    }

    private static void addTask() {
        System.out.print("Enter task id: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter task name (max 20 chars): ");
        String task_name = scanner.nextLine();
        if (task_name.length() > TASK_NAME_MAX_LENGTH) {
            System.out.println("⚠️ Task name exceeds 20 characters.");
            return;
        }

        System.out.print("Enter task description (max 30 chars): ");
        String description = scanner.nextLine();
        if (description.length() > DESCRIPTION_MAX_LENGTH) {
            System.out.println("⚠️ Description exceeds 30 characters.");
            return;
        }

        taskManager.addTask(userId, id, task_name, description);
        System.out.println("✅ Task added successfully!");
    }

    private static void markTaskDone(String input) {
        String[] parts = input.split(" ");
        if (parts.length == 2 && parts[1].matches("\\d+")) {
            int id = Integer.parseInt(parts[1]);
            taskManager.markTaskDone(userId, id);
            System.out.println("🎉 Task marked as done!");
        } else {
            System.out.println("⚠️ Invalid command format. Use: completed <id>");
        }
    }

    private static void deleteTask(String input) {
        String[] parts = input.split(" ");
        if (parts.length == 2 && parts[1].matches("\\d+")) {
            int id = Integer.parseInt(parts[1]);
            taskManager.deleteTask(userId, id);
            System.out.println("🗑️ Task deleted successfully!");
        } else {
            System.out.println("⚠️ Invalid command format. Use: remove <id>");
        }
    }

    private static void displayTodoTable() {
        System.out.println("\n📝 To-Do List:");
        Map<Integer, Task> tasks = taskManager.getTasks();
        if (tasks.isEmpty()) {
            System.out.println("Your list is empty. Start adding some tasks!");
        } else {
            System.out.print("+-----+----------------------+--------------------------------+-------------+\n");
            System.out.printf("| %-3s | %-20s | %-30s | %-11s |\n", "ID", "Task Name", "Description", "Status");
            System.out.print("+-----+----------------------+--------------------------------+-------------+\n");

            for (Task task : tasks.values()) {
                String status = task.isDone ? "✅ Done" : "❌ Pending";

                String taskName = task.task_name.length() > TASK_NAME_MAX_LENGTH
                        ? task.task_name.substring(0, TASK_NAME_MAX_LENGTH - 3) + "..."
                        : task.task_name;

                String taskDescription = task.description.length() > DESCRIPTION_MAX_LENGTH
                        ? task.description.substring(0, DESCRIPTION_MAX_LENGTH - 3) + "..."
                        : task.description;

                System.out.printf("| %-3d | %-20s | %-30s | %-11s |\n", task.id, taskName, taskDescription, status);
            }
            System.out.print("+-----+----------------------+--------------------------------+-------------+\n");
        }
    }
}
