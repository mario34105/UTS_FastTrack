import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import static j2html.TagCreator.*;
import static spark.Spark.*;

public class Chat {
    static Map<Session, String> userUserNameMap = new ConcurrentHashMap();
    static Map<Session, Character> playerMap = new ConcurrentHashMap<>();
    static int nextUserNumber = 1;

    public static void main(String []args) {
        staticFileLocation("/public");
        webSocket("/chat", ChatWebSocketHandler.class);
        webSocket("/game", GameWebSocketHandler.class);
        init();
    }

    public static void broadcastMessage(Session session, String sender, String message) {
        JSONObject jsonPayload = new JSONObject()
                //.put("userMessage", createHtmlMessageFromSender(sender, message))
                .put("userMessage", createJsonMessageFromSender(sender, message))
                .put("userList", userUserNameMap.values());

        userUserNameMap.keySet().stream().filter(x -> x == session).forEach(s -> {
            try {
                session.getRemote().sendString(String.valueOf(jsonPayload));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        /*userUserNameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(jsonPayload));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });*/
    }

    public static String createHtmlMessageFromSender(String sender, String message) {
        return article().with(
                b(sender + " says:"),
                p(message),
                span().withClass("timestamp").withText(
                        new SimpleDateFormat("HH:mm:ss").format(new Date())
                )
        ).render();
    }

    public static JSONObject createJsonMessageFromSender(String sender, String message) {
        return new JSONObject()
                .put("from", sender)
                .put("message", message)
                .put("timestamp", new SimpleDateFormat("HH:mm:ss").format(new Date()));
    }

    public static boolean playerXJoined() {
        return playerMap.containsValue('X');
    }

    public static boolean playerOJoined() {
        return playerMap.containsValue('O');
    }
}
