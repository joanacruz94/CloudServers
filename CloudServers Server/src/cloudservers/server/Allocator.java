/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservers.server;

import cloudservers.data.*;
import cloudservers.exceptions.InexistingServerTypeException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author joanacruz
 */
public class Allocator implements Runnable {

    ReentrantLock reservationsLock;
    ReentrantLock bidsLock;
    ReentrantLock availableServersLock;
    Condition hasReservations;
    Condition hasBids;
    Condition serversAvailable;

    public Allocator(ReentrantLock reservationsLock, ReentrantLock bidsLock, ReentrantLock availableServersLock, Condition hasReservations, Condition hasBids, Condition availableServers) {
        this.reservationsLock = reservationsLock;
        this.bidsLock = bidsLock;
        this.availableServersLock = availableServersLock;
        this.hasReservations = hasReservations;
        this.hasBids = hasBids;
        this.serversAvailable = availableServers;
    }

    @Override
    public void run() {
        ReservationDAO reservationDAO = ReservationDAO.getInstance();
        BidsDAO bidsDAO = BidsDAO.getInstance();
        ServerInstanceDAO serverInstanceDAO = ServerInstanceDAO.getInstance();
        while (true) {
            reservationsLock.lock();
            try {

                List<Reservation> allocatedReservations = new ArrayList<>();
                for (Reservation reservation : reservationDAO.waitingReservations) {
                    String reservationServerType = reservation.getServerType();
                    serverInstanceDAO.getLock().lock();
                    List<ServerInstance> instancesOfType = serverInstanceDAO.serverInstances.get(reservationServerType);
                    serverInstanceDAO.getLock().unlock();
                    for (ServerInstance serverInstance : instancesOfType) {
                        serverInstance.lock();
                        if (serverInstance.getState() == ServerState.FREE) {
                            serverInstanceDAO.allocateServerToReservation(serverInstance, reservation);
                            allocatedReservations.add(reservation);
                            serverInstance.unlock();
                            break;
                        }
                        serverInstance.unlock();
                    }
                    reservationDAO.removeFromList(allocatedReservations);

                }

            } finally {
                reservationsLock.unlock();
            }
            try {
                availableServersLock.lock();
                try {
                    while (serverInstanceDAO.freeServersCount() == 0) {
                        serversAvailable.await();
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
                try {
                    while (bidsDAO.waitingBids.isEmpty()){
                        hasBids.await();
                    }
                }finally {
                    bidsLock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

}
