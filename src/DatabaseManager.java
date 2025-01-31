import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;

public class DatabaseManager {
    private static final String URL = ""; // Credentials removed for safety
    private static final String USERNAME = "";
    private static final String PASSWORD = "";

    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database!");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isUsernameTaken(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isUserInGroup(int groupId, int userId) {
        String query = "SELECT COUNT(*) FROM group_members WHERE group_id = ? AND user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean addUser(String username, String email, String password, String firstName, String lastName) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        String query1 = "INSERT INTO users (username, password, created_at) VALUES (?, ?, NOW())";
        String query2 = "INSERT INTO info (username, first_name, last_name, email, password) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt1 = connection.prepareStatement(query1);
             PreparedStatement stmt2 = connection.prepareStatement(query2)) {
            stmt1.setString(1, username);
            stmt1.setString(2, password);
            stmt1.executeUpdate();

            stmt2.setString(1, username);
            stmt2.setString(2, firstName);
            stmt2.setString(3, lastName);
            stmt2.setString(4, email);
            stmt2.setString(5, password);
            stmt2.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyPassword(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password").equals(password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean saveMessage(int senderId, int recipientId, String message, int userId) {
        String query = "INSERT INTO messages (sender, recipient, message, user_id, sent_at) VALUES (?, ?, ?, ?, NOW())";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, recipientId);
            stmt.setString(3, message);
            stmt.setInt(4, userId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createGroup(String groupName, int userId) {
        String query = "INSERT INTO groups (group_name, created_by, created_at) VALUES (?, ?, NOW())";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, groupName);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addUserToGroup(int groupId, int userId) {
        String query = "INSERT INTO group_members (group_id, user_id, joined_at) VALUES (?, ?, NOW())";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getGroupMembers(int groupId) {
        List<String> members = new ArrayList<>();
        String query = "SELECT u.username FROM users u JOIN group_members gm ON u.user_id = gm.user_id WHERE gm.group_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                members.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public boolean saveGroupMessage(int groupId, int userId, String message) {
        String query = "INSERT INTO group_messages (group_id, user_id, message, sent_at) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.setInt(2, userId);
            stmt.setString(3, message);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getGroupMessages(int groupId) {
        List<String> messages = new ArrayList<>();
        String query = "SELECT u.username, gm.message, gm.sent_at FROM group_messages gm JOIN users u ON gm.user_id = u.user_id WHERE gm.group_id = ? ORDER BY gm.sent_at";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String message = rs.getString("username") + ": " + rs.getString("message") + " (" + rs.getTimestamp("sent_at") + ")";
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public int getGroupIdByName(String groupName) {
        String query = "SELECT group_id FROM groups WHERE group_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("group_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if group not found or an error occurs
    }
}
