import org.glassfish.tyrus.server.Server;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/chat")
public class WebSocketServer {

    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();

    // When a new client connects
    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("New connection: " + session.getId());
    }

    // When a client sends a message
    @OnMessage
    public void onMessage(String message, Session senderSession) {
        System.out.println("Received message from " + senderSession.getId() + ": " + message);

        // Broadcast message to all connected clients
        for (Session session : sessions) {
            if (session.isOpen() && !session.equals(senderSession)) { // Avoid echoing back to the sender
                try {
                    session.getBasicRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // When a client disconnects
    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Connection closed: " + session.getId());
    }

    public static void main(String[] args) {
        Server server = new Server("localhost", 8080, "/websocket", null, WebSocketServer.class);
        try {
            server.start();
            System.out.println("WebSocket server started...");
            Thread.sleep(Long.MAX_VALUE); // Keep the server running
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}