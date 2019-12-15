package ws;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/ws")
public class WebSocketAnnotation {
    private String username;
    private Session session;
    private static final Set<WebSocketAnnotation> users = new CopyOnWriteArraySet<>();
    public WebSocketAnnotation() {
    }

    @OnOpen
    public void start(Session session) {
        users.add(this);
        this.session = session;
    }

    @OnClose
    public void end() {
        users.remove(this);
        // clean up once the WebSocket connection is closed
    }

    @OnMessage
    public void receiveMessage(String message) {
        sendMessage("NOW YOU HAVE ADMIN RIGHTS");
    }

    @OnError
    public void handleError(Throwable t) {
        t.printStackTrace();
    }

    private void sendMessage(String text) {
        // uses *this* object's session to call sendText()
        try {
            for(WebSocketAnnotation user : users){
                user.session.getBasicRemote().sendText(text);
            }

        } catch (IOException e) {
            // clean up once the WebSocket connection is closed
            try {
                this.session.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
