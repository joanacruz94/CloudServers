package cloudservers.server;

import cloudservers.data.BidsDAO;
import cloudservers.data.DemandDAO;
import cloudservers.data.ServerInstanceDAO;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CloudServer {

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(12346);
        new Thread(new Allocator()).start();
        System.out.println("CloudServers server is now up and listening for connections");
        while (true) {
            Socket s = ss.accept();
            System.out.println("New connection established");
            new Thread(new Server(s)).start();
        }
    }
}
