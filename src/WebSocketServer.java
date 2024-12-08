import org.glassfish.tyrus.server.Server;
import jakarta.websocket.OnMessage;
import jakarta.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint("/chat")
public class WebSocketServer {

    // Handle incoming messages from clients
    @OnMessage
    public String onMessage(String message) {
        System.out.println("Received message: " + message);
        return "Server: Message received: " + message;  // Echoes the message back to the client
    }

    public static void main(String[] args) {
        // Create configuration map
        Map<String, Object> serverConfig = new HashMap<>();

        // Set up and start the WebSocket server
        Server server = new Server("localhost", 8080, "/websocket", serverConfig, WebSocketServer.class);
        try {
            // Start the server
            server.start();
            System.out.println("WebSocket server started...");
            // Keep the server running indefinitely
            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Ensuring the server stops gracefully
            server.stop();
        }
    }
}