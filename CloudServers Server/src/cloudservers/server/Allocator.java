/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservers.server;

import cloudservers.data.ListAndLockPair;
import cloudservers.data.Reservation;
import cloudservers.data.ReservationDAO;
import cloudservers.data.ServerInstance;
import cloudservers.data.ServerInstanceDAO;
import cloudservers.data.ServerState;
import cloudservers.exceptions.InexistingServerTypeException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joanacruz
 */
public class Allocator implements Runnable {

    ReentrantLock reservationsLock;
    ReentrantLock availableServersLock;
    Condition hasReservations;
    Condition availableServers;

    public Allocator(ReentrantLock reservationsLock, ReentrantLock availableServersLock, Condition hasReservations, Condition availableServers) {
        this.reservationsLock = reservationsLock;
        this.availableServersLock = availableServersLock;
        this.hasReservations = hasReservations;
        this.availableServers = availableServers;
    }

    @Override
    public void run() {
        ReservationDAO reservationDAO = ReservationDAO.getInstance();
        ServerInstanceDAO serverInstanceDAO = ServerInstanceDAO.getInstance();
        while (true) {
            reservationsLock.lock();
            try {
                List<Reservation> allocatedReservations = new ArrayList<>();
                
                for (Reservation reservation : reservationDAO.waitingReservations) {
                    String reservationServerType = reservation.getServerType();
                    List<ServerInstance> instancesOfType = serverInstanceDAO.serverInstances.get(reservationServerType);
                    //instancesOfType.lock();
                    for (ServerInstance serverInstance : instancesOfType) {
                        serverInstance.lock();
                        if (serverInstance.getState() == ServerState.FREE) {
                            //reservation.allocate(serverInstance);
                            serverInstanceDAO.allocateServerToReservation(serverInstance.getName(), reservation);
                            allocatedReservations.add(reservation);
                            break;
                        }
                        serverInstance.unlock();
                    }
                    //instancesOfType.unlock();
                }
                reservationDAO.removeFromList(allocatedReservations);

            } catch (InexistingServerTypeException ex) {
                
            } finally {
                reservationsLock.unlock();
            }
            try {
                availableServersLock.lock();
                try {
                    while (serverInstanceDAO.freeServersCount() == 0) {
                        availableServers.await();
                    }
                } finally {
                    availableServersLock.unlock();
                }
               
                reservationsLock.lock();
                try {
                    while (reservationDAO.waitingReservations.isEmpty()) {
                        hasReservations.await();
                    }
                } finally {
                    reservationsLock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

}
