import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;

class Server {

    private static final int PORT = 7791;
    private static ArrayList<RoomHandler> rooms = new ArrayList<>();
    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(25);

    public static void main(String[] args) throws Exception {

        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Server opened at " + InetAddress.getLocalHost());
        System.out.println("Awaiting client connections...");

        while(true) {
            Socket client = server.accept();
            System.out.println("Client connected: " + client.toString());
            ClientHandler clientThread = new ClientHandler(client);
            clients.add(clientThread);
            pool.execute(clientThread);
        }
    }

    public static void log(String msg) {
        System.out.println(msg);
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

    public static RoomHandler getRoom(int index) {
        return rooms.get(index);
    }

    public static void addRoom(String name) {
        rooms.add(new RoomHandler(name));
        log("New room created: " + name);
    }

    public static void removeRoom(RoomHandler room) {
        rooms.remove(room);
        log("Room deleted: " + room.getRoomName());
    }

    public static int numRooms() {
        return rooms.size();
    }

    public static String roomList() {
        String str = "";
        for(int i = 0; i < Server.numRooms(); i++) {
            str = str + i + ": " + rooms.get(i).getRoomName() + "\n";
        }
        return str;
    }
}