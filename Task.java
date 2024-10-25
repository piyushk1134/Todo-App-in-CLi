public class Task {
    int id;
    String task_name;
    String description;
    boolean isDone;

    public Task(int id, String task_name, String description, boolean isDone) {
        this.id = id;
        this.task_name = task_name;
        this.description = description;
        this.isDone = isDone;
    }

    @Override
    public String toString() {
        return id + ": " + task_name + " [" + (isDone ? "✅ Done" : "❌ Pending") + "] - " + description;
    }
}
