/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservers.data;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author joanacruz
 */
public class ReservationDAO {

    public LinkedList<Reservation> waitingReservations = new LinkedList<>();

    public static ReentrantLock lock = new ReentrantLock();
    public static Condition hasReservations = lock.newCondition();
    
    private static ReservationDAO ourInstance = new ReservationDAO();

    public static ReservationDAO getInstance() {
        return ourInstance;
    }
    
    public void removeFromList(List<Reservation> reservations){
        waitingReservations.removeAll(reservations);
    }
    
    public List<Reservation> getUserWaitingReservations(User u){
        List<Reservation> reservations = new ArrayList<>();
        waitingReservations.stream().filter((r) -> (r.getUser().equals(u))).forEach((r) -> {
            reservations.add(r);
        });
        return reservations;
    }

}
