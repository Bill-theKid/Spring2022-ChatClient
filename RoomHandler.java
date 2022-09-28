import java.util.ArrayList;

public class RoomHandler {
    private String name;
    private ArrayList<ClientHandler> clients = new ArrayList<>();

    public RoomHandler(String name) {
        this.name = name;
    }

    public void sendToAll(ClientHandler client, String message) {
        message = client.getUsername() + ": " + message;
        for(int i = 0; i < clients.size(); i++) {
            if(clients.get(i).getUsername().equals(client.getUsername()))
                continue;
            else
                clients.get(i).send(message);
        }
    }

    public void sendToAll(String message) {
        for(int i = 0; i < clients.size(); i++)
            clients.get(i).send(message);
    }

    public void sendServerMsg(String message) {
        String serverMsg = "[SERVER] " + message;
        sendToAll(serverMsg);
    }

    public String getRoomName() {
        return name;
    }

    public void addClient(ClientHandler client) {
        sendToAll(client.getUsername() + " joined the room.");
        clients.add(client);
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        sendToAll(client.getUsername() + " left the room.");
        if(clients.size() == 0) {
            Server.removeRoom(this);
        }
    }
}
