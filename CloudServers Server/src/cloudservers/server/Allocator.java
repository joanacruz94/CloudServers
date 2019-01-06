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
        DemandDAO demands = DemandDAO.getInstance();
        BidsDAO bids  = BidsDAO.getInstance();
        ServerInstanceDAO servers = ServerInstanceDAO.getInstance();
        ReservationDAO reservations = ReservationDAO.getInstance();
        Condition hasReservations = ReservationDAO.hasReservations;
        Condition serversAvailable = servers.serversAvailable;

        while (true) {
            demands.lock();
            try {
                List<Reservation> allocatedReservations = new ArrayList<>();
                for (Reservation reservation : demands.waitingDemands) {
                    String demandServerType = reservation.getServerType();
                    servers.lock();
                    List<ServerInstance> instancesOfType = servers.serverInstances.get(demandServerType);
                    servers.unlock();
                    for (ServerInstance serverInstance : instancesOfType) {
                        serverInstance.lock();
                        if (serverInstance.getState() == ServerState.FREE) {
                            servers.allocateServerToReservation(serverInstance, reservation);
                            
                            allocatedReservations.add(reservation);
                            serverInstance.unlock();
                            break;
                        }
                        serverInstance.unlock();
                    }
                }
                demands.removeFromList(allocatedReservations);
            } finally {
                demands.unlock();
            }
            bids.lock();
            try {
                List<Reservation> allocatedReservations = new ArrayList<>();
                for (Reservation bid : bids.waitingBids) {
                    String bidServerType = bid.getServerType();
                    servers.lock();
                    List<ServerInstance> instanceOfType = servers.serverInstances.get(bidServerType);
                    servers.unlock();

                    for (ServerInstance serverInstance : instanceOfType) {
                        serverInstance.lock();
                        if (serverInstance.getState() == ServerState.FREE) {

                            servers.allocateServerToReservation(serverInstance, bid);

                            allocatedReservations.add(bid);
                            serverInstance.unlock();
                            break;
                        }
                        serverInstance.unlock();
                    }
                }
                bids.removeFromList(allocatedReservations);
            } finally {
                bids.unlock();
            }
            try {
                servers.lock();
                try {
                    while (servers.freeServersCount() == 0) {
                        serversAvailable.await();
                    }
                } finally {
                    servers.unlock();
                }

                reservations.lock();
                try {
                    while (!reservations.hasReservations()) {
                        hasReservations.await();
                    }
                } finally {
                    reservations.unlock();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

}
