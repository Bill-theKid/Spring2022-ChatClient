import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.*;

public class Client {
    public static void main(String[] args) throws Exception {
        String host;
        int port = 7791;
        Scanner input = new Scanner(System.in);
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Socket server;

        try {
            System.out.print("Please Enter ip address: ");
            host = input.nextLine();
            server = new Socket(InetAddress.getByName(host), port);

            PrintWriter out = new PrintWriter(server.getOutputStream(), true);
            ClientThread thread = new ClientThread(server);
            exec.execute(thread);

            System.out.print("Enter Username: ");
            while(true) {
                String messageOut = input.nextLine();
                out.println(messageOut);
            }
        } catch(UnknownHostException e) {
            System.err.println("Error: Address not found.");
        }
    }

    public static void printMsg(String message) {
        System.out.println(message);
    }
}
