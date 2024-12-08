import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import org.glassfish.tyrus.client.ClientManager;

import java.net.URI;

@ClientEndpoint
public class WebSocketClient {

    private Session userSession = null;

    // Handle incoming messages from the server
    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received from server: " + message);
    }

    // Handle when the WebSocket connection is established
    @OnOpen
    public void onOpen(Session session) {
        this.userSession = session;
        System.out.println("Connected to the server!");
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

    public void sendMessage(String message) {
        try {
            if (userSession != null && userSession.isOpen()) {
                userSession.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WebSocketClient client = new WebSocketClient();
        client.startConnection();
        client.sendMessage("Hello from client!");
    }
}