package cloudservers.server;

import cloudservers.data.User;
import cloudservers.data.UserDAO;
import cloudservers.exceptions.EmailAlreadyTakenException;
import cloudservers.exceptions.NotExistantUserException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    private final Socket s;
    private User user;

    public Server(Socket s) {
        this.s = s;
    }

    public void run() {
        try {
            UserDAO users = UserDAO.getInstance();

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
                                System.out.println("Success: User logged in successfully");
                            } catch (NotExistantUserException e) {
                                w.println("Error: There is no user with the provided email");
                                System.out.println("Error: There is no user with the provided email");
                            }
                        } else {
                            w.println("Error: The user doens't exist or the password is incorrect");
                            System.out.println("Error: The user doens't exist or the password is incorrect");
                        }
                    } // REGISTER
                    else if (line.matches("register [^ \n]+ [^ \n]+")) {
                        String[] tokens = line.split(" ");
                        String email = tokens[1];
                        String password = tokens[2];

                        try {
                            users.addUser(email, password);
                            w.println("Success: User registered successfully");
                            System.out.println("Success: User registered successfully");
                        } catch (EmailAlreadyTakenException e) {
                            w.println("Error: An account has already been created with the provided email");
                            System.out.println("Error: An account has already been created with the provided email");
                        }

                    }
                    else if(line.equals("logout")){
                        this.user = null;
                        w.println("Success: User logged out successfully");
                        System.out.println("Success: User logged out successfully");
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
