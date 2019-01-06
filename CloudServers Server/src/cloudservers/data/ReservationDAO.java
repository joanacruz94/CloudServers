/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservers.data;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author joanacruz
 */
public class ReservationDAO {
    public static ReentrantLock lock = new ReentrantLock();
    public static Condition hasReservations = lock.newCondition(); 
    private static ReservationDAO ourInstance = new ReservationDAO();

    public static ReservationDAO getInstance() {
        return ourInstance;
    }
    
    public static boolean hasAnyReservation(){
        //Apenas funciona sem estes Locks
        boolean result;
        //DemandDAO.lock.lock();
        //BidsDAO.lock.lock();
        result = !(DemandDAO.getInstance().waitingDemands.isEmpty() && BidsDAO.getInstance().waitingBids.isEmpty());
        //DemandDAO.lock.unlock();
        //BidsDAO.lock.unlock();
        return result;
    }
    
    public void lock(){
        this.lock.lock();
    }
    
    public void unlock(){
        this.lock.unlock();
    }
}
