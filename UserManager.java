import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {

    // Register a new user
    public boolean signUp(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password)); // Hashing password for security
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Login an existing user
    public boolean login(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return verifyPassword(password, storedPassword); // Verifying password
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get user ID by username and password
    public int getUserId(String username, String password) {
        String query = "SELECT id, password FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (verifyPassword(password, storedPassword)) {
                    return rs.getInt("id"); // Return user ID if credentials are valid
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Invalid credentials
    }

    // Placeholder method for hashing password (you can implement with BCrypt or any hashing library)
    private String hashPassword(String password) {
        return password; // Replace this with actual hashing
    }

    // Placeholder method for verifying password (use BCrypt if hashing)
    private boolean verifyPassword(String password, String storedPassword) {
        return password.equals(storedPassword); // Replace with actual verification
    }
}
