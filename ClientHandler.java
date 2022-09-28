import java.net.*;
import java.io.*;

public class ClientHandler implements Runnable {
    private Socket client;
    private String username;
    private BufferedReader in;
    private PrintWriter out;
    private RoomHandler room;
    private static final String COMMANDS = "Type /join [room #] to join a room\nType /create [room name] to create a room\nType /rooms to display open rooms\nType /help to display this message again";

    ClientHandler(Socket client) throws IOException {
        this.client = client;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
    }

    ClientHandler(Socket client, String username) throws IOException {
        this.client = client;
        this.username = username;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
    }

    public void run() {
        try {
            username = in.readLine();
            send(Server.roomList() + COMMANDS);
            while(true) {
                String messageIn = in.readLine();
                parseInput(messageIn);
            }
        } catch(IOException e) {
            room.sendServerMsg(username + " disconnected.");
            Server.log("Client disconnected: " + client.toString());
        } finally {
            out.close();
            try {
                in.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void parseInput(String input) {
        if(input.charAt(0) == '/') {
            String[] subStr = input.split(" ", 2);
            switch(subStr[0]) {
                case "/join":
                    try {
                        if(room != null) {
                            RoomHandler prev = room;
                            Server.getRoom(Integer.parseInt(subStr[1])).addClient(this);
                            room = Server.getRoom(Integer.parseInt(subStr[1]));
                            prev.removeClient(this);
                            send("Joined " + room.getRoomName());
                        } else {
                            Server.getRoom(Integer.parseInt(subStr[1])).addClient(this);
                            room = Server.getRoom(Integer.parseInt(subStr[1]));
                            send("Joined " + room.getRoomName());
                        }
                    } catch(IndexOutOfBoundsException e) {
                        send("Invalid room number");
                    }
                    break;
                case "/create":
                    try {
                        Server.addRoom(subStr[1]);
                        if(room != null) {
                            room.removeClient(this);
                        }
                        Server.getRoom(Server.numRooms() - 1).addClient(this);
                        room = Server.getRoom(Server.numRooms() - 1);
                        send("Joined " + room.getRoomName());
                    } catch(ArrayIndexOutOfBoundsException e) {
                        send("command syntax: /create RoomName");
                    }
                    break;
                case "/rooms":
                    send(Server.roomList());
                    break;
                case "/help":
                    send(COMMANDS);
                    break;
                default:
                    send("Invalid command");
            }
        }
        else {
            try {
                room.sendToAll(this, input);
            } catch(NullPointerException e) {
                send("Not currently in room");
            }
        }
    }
    
    public void send(String message) {
        out.println(message);
    }
    
    public String getUsername() {
        return username;
    }
}
