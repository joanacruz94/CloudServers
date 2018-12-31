package cloudservers.server;

import cloudservers.data.ServerInstanceDAO;
import cloudservers.data.User;
import cloudservers.data.UserDAO;
import cloudservers.exceptions.EmailAlreadyTakenException;
import cloudservers.exceptions.InexistingServerTypeException;
import cloudservers.exceptions.NoServersAvailableException;
import cloudservers.exceptions.NotExistantUserException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    private final Socket s;
    private User user;

    public Server(Socket s) {
        this.s = s;
    }

    public void run() {
        try {
            UserDAO users = UserDAO.getInstance();
            ServerInstanceDAO servers = ServerInstanceDAO.getInstance();

            PrintWriter w = new PrintWriter(s.getOutputStream());
            BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line;
            do {
                line = r.readLine();

                if (line != null) {
                    System.out.println("Received message: " + line);
                    // LOGIN
                    if (line.matches("login [^ \n]+ [^ \n]+")) {
                        String[] tokens = line.split(" ");
                        String email = tokens[1];
                        String password = tokens[2];

                        boolean success = users.auth(email, password);
                        if (success) {
                            try {
                                this.user = users.getUserByEmail(email);
                                w.println("Success: User logged in successfully");
                                w.flush();
                            } catch (NotExistantUserException e) {
                                w.println("Error: There is no user with the provided email");
                                w.flush();
                            }
                        } else {
                            w.println("Error: The user doesn't exist or the password is incorrect");
                            w.flush();
                        }
                    } // REGISTER
                    else if (line.matches("register [^ \n]+ [^ \n]+")) {
                        String[] tokens = line.split(" ");
                        String email = tokens[1];
                        String password = tokens[2];

                        try {
                            users.addUser(email, password);
                            w.println("Success: User registered successfully");
                            w.flush();
                        } catch (EmailAlreadyTakenException e) {
                            w.println("Error: An account has already been created with the provided email");
                            w.flush();
                        }
                    } // LOGOUT
                    else if (line.equals("logout")) {
                        this.user = null;
                        w.println("Success: User logged out successfully");
                        w.flush();
                    } // SERVERS LIST
                    else if (line.equals("serversList")) {
                        w.println(servers.getServersAsString().replace("\n", ";"));
                        w.flush();
                    } // MY SERVERS
                    else if (line.equals("myServers")) {
                        w.println();
                        w.flush();
                    } // CURRENT DEBT
                    else if (line.equals("currentDebt")) {
                        w.println();
                        w.flush();
                    }
                    else if(line.matches("serverDemand [SML][1-2]")) {
                        String[] tokens = line.split(" ");
                        String serverType = tokens[1];
                        try {
                            servers.allocateServerToUser(user, serverType);
                            w.println("Success");
                            w.flush();
                        } catch (NoServersAvailableException ex) {
                            w.println("Error: No servers of the requested type available at the moment");
                            w.flush();
                        } catch (InexistingServerTypeException ex) {
                            w.println("Error: Please provide a valid server type");
                            w.flush();
                        }
                        
                    }
                }
            } while (!line.equals("exit"));

            w.flush();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// User
// ServerType
// ServerInstance
//
// Reservation
// Bid
// Utilization
// Authentication
// Reserve
// Bid
// CancelReservation
// CancelBid
// GetUsageReport
