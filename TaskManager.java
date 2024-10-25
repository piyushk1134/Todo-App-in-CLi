import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();

    public void loadTasksFromDatabase(int userId) {
        tasks.clear();
        String query = "SELECT * FROM tasks WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String task_name = rs.getString("task_name");
                String description = rs.getString("description");
                boolean isDone = rs.getBoolean("is_done");
                tasks.put(id, new Task(id, task_name, description, isDone));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addTask(int userId, int id, String task_name, String description) {
        String query = "INSERT INTO tasks (id, task_name, description, is_done, user_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.setString(2, task_name);
            stmt.setString(3, description);
            stmt.setBoolean(4, false);
            stmt.setInt(5, userId);
            stmt.executeUpdate();

            loadTasksFromDatabase(userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void markTaskDone(int userId, int id) {
        String query = "UPDATE tasks SET is_done = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setBoolean(1, true);
            stmt.setInt(2, id);
            stmt.setInt(3, userId);
            stmt.executeUpdate();

            loadTasksFromDatabase(userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTask(int userId, int id) {
        String query = "DELETE FROM tasks WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

            loadTasksFromDatabase(userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, Task> getTasks() {
        return tasks;
    }
}
