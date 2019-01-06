/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservers.data;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author joanacruz
 */
public class DemandDAO {

    public LinkedList<Reservation> waitingDemands = new LinkedList<>();
    public static ReentrantLock lock = new ReentrantLock(); 
    private static DemandDAO ourInstance = new DemandDAO();

    public static DemandDAO getInstance() {
        return ourInstance;
    }
    
    public void removeFromList(List<Reservation> reservations){
        this.lock();
        waitingDemands.removeAll(reservations);
        this.unlock();
    }
    
    public List<Reservation> getUserWaitingReservations(User u){
        List<Reservation> reservations = new ArrayList<>();
        this.lock();
        waitingDemands.stream().filter((r) -> (r.getUser().equals(u))).forEach((r) -> {
            reservations.add(r);
        });
        this.unlock();
        return reservations;
    }
    
    public void lock(){
        this.lock.lock();
    }
    
    public void unlock(){
        this.lock.unlock();
    }

}
