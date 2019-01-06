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
                System.out.println("aloooocater um gajo12");

                for (Reservation reservation : demandDAO.waitingReservations) {
                    System.out.println("aloooocater um gajo13");

                    String reservationServerType = reservation.getServerType();
                    System.out.println("aloooocater um gajo14");

                    serverInstanceDAO.getLock().lock();
                    System.out.println("aloooocater um gajo15");

                    List<ServerInstance> instancesOfType = serverInstanceDAO.serverInstances.get(reservationServerType);
                    System.out.println("aloooocater um gajo16");

                    serverInstanceDAO.getLock().unlock();
                    System.out.println("aloooocater um gajo");

                    for (ServerInstance serverInstance : instancesOfType) {
                        serverInstance.lock();
                        System.out.println("aloooocater um 22");

                        if (serverInstance.getState() == ServerState.FREE) {
                            System.out.println("aloquei um gajo");
                            serverInstanceDAO.allocateServerToReservation(serverInstance, reservation);
                            allocatedReservations.add(reservation);
                            serverInstance.unlock();
                            break;
                        }
                        serverInstance.unlock();
                    }
                    demandDAO.removeFromList(allocatedReservations);

                }
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
                    bidsDAO.removeFromList(allocatedReservations);
                }
            } finally {
                bidsLock.unlock();
            }
            try {
                availableServersLock.lock();
                try {
                    while (serverInstanceDAO.freeServersCount() == 0) {
                        System.out.println("server passei");
                        serversAvailable.await();
                        System.out.println("server passei 1");
                    }
                } finally {
                    availableServersLock.unlock();
                }

                reservationsLock.lock();
                try {
                    while (!ReservationDAO.hasReservations()) {
                        System.out.println("reservation passei 1");

                        hasReservations.await();

                        System.out.println("reservation passei 2");

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
