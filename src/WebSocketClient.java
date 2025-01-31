import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import org.glassfish.tyrus.client.ClientManager;
import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@ClientEndpoint
public class WebSocketClient {

    private Session userSession = null;
    private final DatabaseManager databaseManager = new DatabaseManager(); // Connects to the database

    @OnOpen
    public void onOpen(Session session) {
        this.userSession = session;
        System.out.println("Connected to the server!");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received from server: " + message);

        String[] parts = message.split(":", 3); // Example format: "sender:recipient:message"
        if (parts.length == 3) {
            String sender = parts[0].trim();
            String recipient = parts[1].trim();
            String msg = parts[2].trim();
            if (!sender.equals("Server")) {
                int senderId = getUserIdByUsername(sender);
                int recipientId = getUserIdByUsername(recipient);
                int userId = senderId; // Assuming the userId is the same as senderId
                databaseManager.saveMessage(senderId, recipientId, msg, userId);
            }
        }
    }

    private int getUserIdByUsername(String username) {
        String query = "SELECT user_id FROM users WHERE username = ?";
        try (PreparedStatement stmt = databaseManager.getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return an invalid user ID if not found
    }

    public void startConnection() {
        try {
            WebSocketContainer container = ClientManager.createClient();
            URI uri = new URI("ws://localhost:8080/websocket/chat");
            container.connectToServer(this, uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String username, String message) {
        try {
            if (userSession != null && userSession.isOpen()) {
                String formattedMessage = username + ": " + message; // Include sender info
                userSession.getBasicRemote().sendText(formattedMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WebSocketClient client = new WebSocketClient();
        client.startConnection();
        client.sendMessage("YourUsername", "Hello from client!");
    }
}
