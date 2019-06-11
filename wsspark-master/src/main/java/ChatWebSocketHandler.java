import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONArray;
import org.json.JSONObject;

@WebSocket
public class ChatWebSocketHandler {
    private String sender, msg;

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        String username = "User" + Chat.nextUserNumber++;
        Chat.userUserNameMap.put(user, username);
        Chat.broadcastMessage(user, "Server", (username + " joined the chat"));
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = Chat.userUserNameMap.get(user);
        Chat.userUserNameMap.remove(user);
        Chat.broadcastMessage(user, "Server", (username + " left the chat"));
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        //String username = Chat.userUserNameMap.get(user);
        //Chat.broadcastMessage(username, message);
        JSONObject json = new JSONObject(message);
        String nama = (String)json.get("nama");
        String pesan = (String)json.get("pesan");

        try {
            HttpResponse<JsonNode> response = Unirest.get("https://ghibliapi.herokuapp.com/locations").asJson();
            JSONArray array = response.getBody().getArray();
            StringBuilder str = new StringBuilder();
            
            
            for (int i = 0; i < array.length(); i++) {
                if(array.getJSONObject(i).getString("name").toLowerCase().contains(pesan)){
                str.append(array.getJSONObject(i).getString("name"));
                str.append(",");
                
                }
            }
            Chat.broadcastMessage(user, nama, str.toString());
        } catch (Exception e) {
            Chat.broadcastMessage(user, nama, "Request tidak dapat dilakukan");
        }

    }
}
