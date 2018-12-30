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
            do{
                line = r.readLine();
                // LOGIN
                if(line.matches("login [^ \n]+ [^ \n]+")){
                    String []tokens = line.split(" ");
                    String email = tokens[1];
                    String password = tokens[2];
                    
                    boolean success = users.auth(email, password);
                    if(success){
                        try{
                            users.getUserByEmail(email);
                            w.println("Success");
                        }
                        catch(NotExistantUserException e){
                            w.println("Error: There is no user with the provided email");
                        }
                    }
                    else{
                        w.println("Error: The user doens't exist or the password is incorrect");
                    }
                }
                // REGISTER
                else if(line.matches("register [^ \n]+ [^ \n]+")){
                    String []tokens = line.split(" ");
                    String email = tokens[1];
                    String password = tokens[2];
                    
                    try{
                        users.addUser(email, password);
                        w.println("Success");
                    }
                    catch(EmailAlreadyTakenException e){
                        w.println("Error: An account has already been created with the provided email");
                    }
       
                }
           
            }while(!line.equals("exit"));
            
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