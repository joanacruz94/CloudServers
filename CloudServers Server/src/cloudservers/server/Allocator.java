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

    @Override
    public void run() {
        DemandDAO demandDAO = DemandDAO.getInstance();
        BidsDAO bidsDAO = BidsDAO.getInstance();
        ServerInstanceDAO serverInstanceDAO = ServerInstanceDAO.getInstance();
        ReentrantLock demandsLock = DemandDAO.lock;
        ReentrantLock bidsLock = BidsDAO.lock;
        ReentrantLock reservationsLock = ReservationDAO.lock;
        ReentrantLock availableServersLock = ServerInstanceDAO.lock;
        Condition hasReservations = ReservationDAO.hasReservations;
        Condition serversAvailable = ServerInstanceDAO.serversAvailable;

        while (true) {
            demandsLock.lock();
            try {
                List<Reservation> allocatedReservations = new ArrayList<>();
                for (Reservation reservation : demandDAO.waitingReservations) {
                    String demandServerType = reservation.getServerType();
                    serverInstanceDAO.getLock().lock();
                    List<ServerInstance> instancesOfType = serverInstanceDAO.serverInstances.get(demandServerType);
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
                }
                demandDAO.removeFromList(allocatedReservations);
            } finally {
                demandsLock.unlock();
            }
            bidsLock.lock();
            try {
                List<Reservation> allocatedReservations = new ArrayList<>();
                for (Reservation bid : bidsDAO.waitingBids) {
                    String bidServerType = bid.getServerType();
                    serverInstanceDAO.getLock().lock();
                    List<ServerInstance> instanceOfType = serverInstanceDAO.serverInstances.get(bidServerType);
                    serverInstanceDAO.getLock().unlock();

                    for (ServerInstance serverInstance : instanceOfType) {
                        serverInstance.lock();
                        if (serverInstance.getState() == ServerState.FREE) {

                            serverInstanceDAO.allocateServerToReservation(serverInstance, bid);

                            allocatedReservations.add(bid);
                            serverInstance.unlock();
                            break;
                        }
                        serverInstance.unlock();
                    }
                }
                bidsDAO.removeFromList(allocatedReservations);
            } finally {
                bidsLock.unlock();
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
                    while (!ReservationDAO.hasReservations()) {
                        System.out.println("vou dormir");
                        hasReservations.await();
                        System.out.println("acordei");
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
