package cloudservers.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cloudservers.exceptions.InexistingReservationNumberException;
import java.util.Arrays;
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
        int nInstancesOfEach = 5;
        for (String serverType : serverTypes) {
            servers = new ArrayList<>();
            for (int i = 0; i < nInstancesOfEach; i++) {
                servers.add(new ServerInstance(serverType, serverType + "-" + (i + 1), 1.99));
            }
            serverInstances.put(serverType, servers);
            freeInstancesCount.put(serverType, nInstancesOfEach);
            
        }
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

    public void allocateServerToReservation(ServerInstance server, Reservation reservation) {
        reservation.allocate(server);
        reservation.setState(ReservationState.ACTIVE);
        reservation.getUser().addReservation(reservation);
        this.lock();
        try {
            this.freeInstancesCount.put(server.getName(), freeInstancesCount.get(server.getName())-1);
        } finally {
            this.unlock();
        }

    }

    public void deallocateReservation(String reservationNumber, User user) throws InexistingReservationNumberException{
        boolean reservationExist = user.getReservations().containsKey(reservationNumber);
        
        if(reservationExist){
            Reservation reservation = user.getReservation(reservationNumber);
            reservation.lock();
            reservation.deallocate();        
            reservation.unlock();
            this.lock();
            try {
                this.freeInstancesCount.put(reservation.getServerType(), freeInstancesCount.get(reservation.getServerType())+1);
            } finally {
                this.unlock();
            }
        }
        else{
            throw new InexistingReservationNumberException();
        }
    }
    
    public List<Reservation> listUserWaitingDemands(User user){
        return DemandDAO.getInstance().getUserWaitingReservations(user);
    }
    
    public List<Reservation> listUserWaitingBids(User user){
        return BidsDAO.getInstance().getUserBids(user);
    }
    
    public List<Reservation> listUserReservations(User user) {
        user.lock();
        Map<String, Reservation> allReservations = user.getReservations();
        List<Reservation> reservations = new ArrayList<>();

        allReservations.values().stream().filter((r) -> (r.getState() == ReservationState.ACTIVE)).forEach((r) -> {
            reservations.add(r);
        });
        user.unlock();
        return reservations;
    }

    
    public String getListWaitingReservations(User user){
        List<List<String>> tableLines = new ArrayList<>();
        tableLines.add(new ArrayList<>(Arrays.asList(new String[]{"IdReservation", "State Reservation", "Server Type", "Reservation Type","Price per hour"})));

        List<Reservation> bidsUser = listUserWaitingBids(user);
        List<Reservation> waitingReservation = listUserWaitingDemands(user);
        List<Reservation> allWaitingReservation = new ArrayList<>();
        allWaitingReservation.addAll(bidsUser);
        allWaitingReservation.addAll(waitingReservation);
        
        allWaitingReservation.forEach((reservation) -> {
            ServerInstance server = reservation.getServerInstance();
            String id = reservation.getId();
            ReservationState stateReservation = reservation.getState();
            String serverType = reservation.getServerType();
            String reservationType = reservation.getReservationType();
            double price = reservation.getPricePerHour();
            tableLines.add(new ArrayList<>(Arrays.asList(new String[]{
                id,
                stateReservation.toString(),
                serverType,
                reservationType,
                String.valueOf(price),})));
        });
        

        return new PrettyTable(tableLines).toString();
    }
    
    
    public String getListUserServers(User user) {
        List<List<String>> tableLines = new ArrayList<>();
        tableLines.add(new ArrayList<>(Arrays.asList(new String[]{"IdReservation", "State Reservation", "Server Type", "Reservation Type", "Current Time", "Price per hour", "Current Cost"})));

        List<Reservation> reservationsList = listUserReservations(user);
        reservationsList.forEach((reservation) -> {
            ServerInstance server = reservation.getServerInstance();
            String id = reservation.getId();
            ReservationState stateReservation = reservation.getState();
            String serverType = reservation.getServerType();
            String reservationType = reservation.getReservationType();
            long currentTime = reservation.getSpentTimeMilis();
            double price = reservation.getPricePerHour();
            double currentCost = reservation.getCurrentCost();
            tableLines.add(new ArrayList<>(Arrays.asList(new String[]{
                id,
                stateReservation.toString(),
                serverType,
                reservationType,
                String.valueOf(currentTime / 1000) + " seconds",
                String.valueOf(price),
                String.valueOf(currentCost),})));
        });
        

        return new PrettyTable(tableLines).toString();
    }

    public int freeServersCount() {
        int sum = 0;
        sum = this.freeInstancesCount.values().stream().map((f) -> f).reduce(sum, Integer::sum);
        return sum;
    }
    
    public int busySpotServersCount(){
        int sum = 0;
        
        for(String type : serverInstances.keySet()){
            sum+= busySpotServersCount(type);
        }
        return sum;
    }
    
    public int busySpotServersCount(String serverType){
        int sum = 0;
        List<ServerInstance> servers = this.serverInstances.get(serverType);
        
        for(ServerInstance server : servers){
            if(server.getState() == ServerState.BUSY_SPOT) sum++;
        }
        return sum;
    }
    
    public void lock(){
        this.lock.lock();
    }
    
    public void unlock(){
        this.lock.unlock();
    }

}
