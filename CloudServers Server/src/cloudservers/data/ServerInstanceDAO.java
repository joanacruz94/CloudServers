package cloudservers.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cloudservers.exceptions.InexistingServerException;
import cloudservers.exceptions.InexistingServerTypeException;
import cloudservers.exceptions.NoServersAvailableException;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ServerInstanceDAO {

    public Map<String, List<ServerInstance>> serverInstances;
    public Map<String, Integer> freeInstancesCount;

    
    private static ServerInstanceDAO ourInstance = new ServerInstanceDAO();
    public static ReentrantLock lock = new ReentrantLock();
    public static Condition serversAvailable = lock.newCondition();
    public static ServerInstanceDAO getInstance() {
        return ourInstance;
    }

    private ServerInstanceDAO() {
        serverInstances = new HashMap<>();
        freeInstancesCount = new HashMap<>();
        ArrayList<ServerInstance> servers = new ArrayList<>();
        String[] serverTypes = {"S1", "S2", "M1", "M2", "L1", "L2"};

        for (String serverType : serverTypes) {
            servers = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                servers.add(new ServerInstance(serverType, serverType + "-" + (i + 1), 1.99));
            }
            serverInstances.put(serverType, servers);
            freeInstancesCount.put(serverType, 5);
            
        }
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public String getServersAsString() {
        List<List<String>> tableLines = new ArrayList<>();
        tableLines.add(new ArrayList<>(Arrays.asList(new String[]{"Type", "Price per hour", "#free instances", "#ocupied instances demand", "#ocupied instances auction"})));
        ServerInstanceDAO.lock.lock();
        try {
            serverInstances.entrySet().forEach((entry) -> {
                String type = entry.getKey();
                
                List<ServerInstance> instancesOfType = entry.getValue();
                double price = instancesOfType.get(0).getPricePerHour();
                int nInstancesFree = (int) instancesOfType.stream().filter(si -> si.getState() == ServerState.FREE).count();
                int nInstancesOcupiedDemand = (int) instancesOfType.stream().filter(si -> si.getState() == ServerState.BUSY_DEMAND).count();
                int nInstancesOcupiedSpot = (int) instancesOfType.stream().filter(si -> si.getState() == ServerState.BUSY_SPOT).count();
                tableLines.add(new ArrayList<>(Arrays.asList(new String[]{
                    type,
                    String.valueOf(price),
                    String.valueOf(nInstancesFree),
                    String.valueOf(nInstancesOcupiedDemand),
                    String.valueOf(nInstancesOcupiedSpot)
                })));
            });
        } finally {
            ServerInstanceDAO.lock.unlock();
        }
        return new PrettyTable(tableLines).toString();
    }

    public void allocateServerToReservation(String serverType, Reservation reservation) throws InexistingServerTypeException {
        List<ServerInstance> instances = null;
        boolean alocated = false;
        ServerInstanceDAO.lock.lock();
        try {
            instances = serverInstances.get(serverType);
        } finally {
            ServerInstanceDAO.lock.unlock();
        }

        if (instances != null) {
            
            OUTER:
            for (ServerInstance server : instances) {
                server.lock();
                if (null != server.getState()) {
                    switch (server.getState()) {
                        case FREE:
                            reservation.allocate(server);
                            reservation.setState(ReservationState.ACTIVE);
                            reservation.getUser().addReservation(reservation);
                            this.freeInstancesCount.put(serverType, freeInstancesCount.get(serverType)-1);
                            alocated = true;
                            break;
                        case BUSY_SPOT:
                            //TODO
                            break;
                        default:
                            break;
                    }
                }
                if(alocated) break;
                server.unlock();
            }
        } else {
            throw new InexistingServerTypeException();
        }
    }

    public void deallocateReservation(String reservationNumber, User user) throws InexistingServerException {
        Reservation reservation = user.getReservation(reservationNumber);
        reservation.lock();
        reservation.deallocate();
        reservation.unlock();
    }
    
    public List<Reservation> listUserWaitingResevations(User user){
        return ReservationDAO.getInstance().getUserWaitingReservations(user);
    }
    
    public List<Reservation> listUserReservations(User user) {
        user.lock();
        Map<String, Reservation> allReservations = user.getReservations();
        List<Reservation> reservations = new ArrayList<>();

        allReservations.values().stream().filter((r) -> (r.getState() != ReservationState.FINISHED)).forEach((r) -> {
            reservations.add(r);
        });
        user.unlock();
        return reservations;
    }

    public String getListUserReservations(User user) {
        List<List<String>> tableLines = new ArrayList<>();
        tableLines.add(new ArrayList<>(Arrays.asList(new String[]{"IdReservation", "State Reservation", "Type", "Current Time", "Price per hour", "Current Cost"})));

        List<Reservation> reservationsList = listUserReservations(user);
        System.out.println("Tenho reservas ativas" + reservationsList.size());
        List<Reservation> waitingReservation = listUserWaitingResevations(user);
        System.out.println("Tenho reservas ativas" + waitingReservation.size());
        List<Reservation> allReservation = new ArrayList<>();
        reservationsList.forEach((reservation) -> {
            ServerInstance server = reservation.getServerInstance();
            String id = reservation.getId();
            //String idServer = server.getId();
            ReservationState stateReservation = reservation.getState();
            String type = server.getName();
            long currentTime = reservation.getSpentTimeMilis();
            double price = server.getPricePerHour();
            double currentCost = reservation.getCurrentCost();
            tableLines.add(new ArrayList<>(Arrays.asList(new String[]{
                id,
                //idServer,
                stateReservation.toString(),
                type,
                String.valueOf(currentTime / 1000) + " seconds",
                String.valueOf(price),
                String.valueOf(currentCost),})));
        });
        
        waitingReservation.forEach((reservation) -> {
            String id = reservation.getId();
            ReservationState stateReservation = reservation.getState();
            String type = reservation.getServerType();
            String currentTime = "";
            String price = "";
            String currentCost = "";
            tableLines.add(new ArrayList<>(Arrays.asList(new String[]{
                id,
                //idServer,
                stateReservation.toString(),
                type,
                currentTime,
                price,
                currentCost,})));
        });

        return new PrettyTable(tableLines).toString();
    }

    public int freeServersCount() {
        int sum = 0;
        for (int f : this.freeInstancesCount.values()) {
            sum += f;
        }
        return sum;
    }

}