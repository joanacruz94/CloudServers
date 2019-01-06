/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservers.server;

import cloudservers.data.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;

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
                    int nInstancesFree = servers.freeInstancesCount.get(demandServerType);
                    servers.unlock();
                    if(nInstancesFree != 0){
                        for (ServerInstance serverInstance : instancesOfType) {
                            serverInstance.lock();
                            System.out.println("Before cicle demand");
                            if (serverInstance.getState() == ServerState.FREE) {
                                System.out.println("Aloquei demand");
                                servers.allocateServerToReservation(serverInstance, reservation);
                                allocatedReservations.add(reservation);
                                serverInstance.unlock();
                                break;
                            }
                            serverInstance.unlock();  
                        }
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
                    int nInstancesFree = servers.freeInstancesCount.get(bidServerType);
                    servers.unlock();
                    if(nInstancesFree != 0){
                        for (ServerInstance serverInstance : instanceOfType) {
                            serverInstance.lock();
                             System.out.println("Before cicle bid");
                            if (serverInstance.getState() == ServerState.FREE) {
                                 System.out.println("Aloquei aqui bid");
                                servers.allocateServerToReservation(serverInstance, bid);

                                allocatedReservations.add(bid);
                                serverInstance.unlock();
                                break;
                            }
                            serverInstance.unlock();
                        }
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
                    while (!reservations.hasAnyReservation()) {
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
