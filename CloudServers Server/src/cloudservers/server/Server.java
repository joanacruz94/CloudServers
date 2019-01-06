package cloudservers.server;

import cloudservers.data.*;
import cloudservers.exceptions.EmailAlreadyTakenException;
import cloudservers.exceptions.InexistingReservationNumberException;
import cloudservers.exceptions.NotExistantUserException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
            ServerInstanceDAO servers = ServerInstanceDAO.getInstance();
            DemandDAO demands = DemandDAO.getInstance();
            BidsDAO bids = BidsDAO.getInstance();
            ReservationDAO reservations = ReservationDAO.getInstance();
            ReservationID reservationID = ReservationID.getInstance();

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
                    } // BIDS LIST
                    else if (line.equals("bidsList")) {
                        w.println(servers.getListUserBids(this.user).replace("\n", ";"));
                        w.flush();
                    } // SERVERS SERVERS
                    else if (line.equals("serversList")) {
                        w.println(servers.getServersAsString().replace("\n", ";"));
                        w.flush();
                    } // MY SERVERS
                    else if (line.equals("myServers")) {
                        w.println(servers.getListUserReservations(this.user).replace("\n", ";"));
                        w.flush();
                    } // CURRENT DEBT
                    else if (line.equals("currentDebt")) {
                        //TODO
                        this.user.lock();
                        double currentDebt = this.user.getCurrentDebt();
                        this.user.unlock();
                        w.println(String.valueOf(currentDebt));
                        w.flush();
                    } //DEALLOCATE 
                    else if (line.matches("deallocate [0-9]+")) {
                        String[] tokens = line.split(" ");
                        String reservationNumber = tokens[1];
                        try {
                            servers.deallocateReservation(reservationNumber, this.user);
                            servers.lock();
                            try {
                                servers.serversAvailable.signalAll();
                            } finally {
                                servers.unlock();
                            }
                            w.println("Success");
                            w.flush();
                        } catch (InexistingReservationNumberException ex) {
                            w.println("Error: Please provide a valid reservation ID");
                            w.flush();
                        }
                    } // GET SERVER ON DEMAND
                    else if (line.matches("serverDemand [SML][1-2]")) {
                        String[] tokens = line.split(" ");
                        String serverType = tokens[1];
                        String reservationNumber = String.valueOf(reservationID.nextID());
                        Reservation reservation = new Reservation(reservationNumber, user, serverType, 0, "DEMAND");
                        demands.lock();
                        try {
                            demands.waitingDemands.add(reservation);
                            reservations.lock();
                            try {
                                reservations.hasReservations.signalAll();
                            } finally {
                                reservations.unlock();
                            }
                        } finally {
                            demands.unlock();
                        }
                        w.println(reservationNumber);
                        w.flush();
                    } // GET SERVER ON AUCTION
                    else if (line.matches("serverAuction [SML][1-2] [0-9]+(\\.[0-9]+)?")) {
                        System.out.println("Recebi bid");
                        String[] tokens = line.split(" ");
                        String serverType = tokens[1];
                        double priceBid = Double.parseDouble(tokens[2]);
                        String reservationNumber = String.valueOf(reservationID.nextID());
                        Reservation reservation = new Reservation(reservationNumber, user, serverType, priceBid, "SPOT");
                        bids.lock();
                        try {
                            bids.waitingBids.add(reservation);
                            reservations.lock();
                            try {
                                reservations.hasReservations.signalAll();
                            } finally {
                                reservations.unlock();
                            }
                        } finally {
                            bids.unlock();
                        }
                        w.println(reservationNumber);
                        w.flush();
                    } // CANCEL RESERVATION 
                    else if (line.matches("cancel [0-9]+")) {
                        String[] tokens = line.split(" ");
                        String reservationNumber = tokens[1];
                        boolean exist = false;
                        demands.lock();
                        try {
                            Reservation res = new Reservation();
                            for (Reservation result : demands.waitingDemands) {
                                if (result.getId().equals(reservationNumber)) {
                                    res = result;
                                    exist = true;
                                }
                            }
                            demands.waitingDemands.remove(res);
                        } finally {
                            demands.unlock();
                        }
                        if(exist){
                            w.println("Sucess: Reservation canceled");
                            w.flush();
                        }
                        else{
                            w.println("Error: Please provide a valid reservation ID");
                            w.flush();
                        }
                    } //CANCEL BID RESERVATION 
                    else if (line.matches("cancelBid [0-9]+")) {
                        String[] tokens = line.split(" ");
                        String reservationNumber = tokens[2];
                        boolean exist = false;
                        bids.lock();
                        try {
                            Reservation res = new Reservation();
                            for (Reservation result : bids.waitingBids) {
                                if (result.getId().equals(reservationNumber)) {
                                    res = result;
                                    exist = true;
                                }
                            }
                            bids.waitingBids.remove(res);
                        } finally {
                            bids.unlock();
                        }
                        if(exist){
                            w.println("Sucess: Reservation canceled");
                            w.flush();
                        }
                        else{
                            w.println("Error: Please provide a valid reservation ID");
                            w.flush();
                        }
                    } else {
                        w.println("Error: Command not recognized");
                        w.flush();
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


