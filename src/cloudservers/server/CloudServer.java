package cloudservers.server;

import java.net.ServerSocket;
import java.net.Socket;

public class CloudServer{
    public static void main(String[] args) throws Exception {

        ServerSocket ss = new ServerSocket(12346);
        System.out.println("CloudServers server is now up and listening for connections");
        while (true) {
            Socket s = ss.accept();
            System.out.println("New connection established");
            new Thread(new Server(s)).start();
        }
    }
}