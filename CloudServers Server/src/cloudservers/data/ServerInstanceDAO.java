package cloudservers.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cloudservers.data.ServerTypes.*;
import cloudservers.exceptions.InexistingServerException;
import cloudservers.exceptions.InexistingServerTypeException;
import cloudservers.exceptions.NoServersAvailableException;
import java.util.Arrays;
import java.util.Map.Entry;

public class ServerInstanceDAO {

    private Map<String, List<ServerInstance>> serverInstances;

    private static ServerInstanceDAO ourInstance = new ServerInstanceDAO();

    public static ServerInstanceDAO getInstance() {
        return ourInstance;
    }

    private List<ServerInstance> createNClonesOf(int n, ServerInstance instance) {
        List<ServerInstance> clones = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ServerInstance clone = instance.clone();
            clone.setId(clone.getName() + "-" + (i + 1));
            clones.add(clone);
        }

        return clones;
    }

    private ServerInstanceDAO() {
        serverInstances = new HashMap<>();
        serverInstances.put("S1", createNClonesOf(5, new S1()));
        serverInstances.put("S2", createNClonesOf(5, new S2()));
        serverInstances.put("M1", createNClonesOf(5, new M1()));
        serverInstances.put("M2", createNClonesOf(5, new M2()));
        serverInstances.put("L1", createNClonesOf(5, new L1()));
        serverInstances.put("L2", createNClonesOf(5, new L2()));

    }

    public String getServersAsString() {
        List<List<String>> tableLines = new ArrayList<>();
        tableLines.add(new ArrayList<>(Arrays.asList(new String[]{"Type", "Price per hour", "#free instances", "#ocupied instances demand", "#ocupied instances auction"})));
        serverInstances.entrySet().forEach((entry) -> {
            String type = entry.getKey();

            List<ServerInstance> instancesOfType = entry.getValue();
            double price = instancesOfType.get(0).getPricePerHour();
            int nInstancesFree = (int) instancesOfType.stream().filter(si -> si.getState() == ServerState.FREE).count();
            int nInstancesOcupiedDemand = (int) instancesOfType.stream().filter(si -> si.getState() == ServerState.BUSY_DEMAND).count();
            int nInstancesOcupiedAuction = (int) instancesOfType.stream().filter(si -> si.getState() == ServerState.BUSY_AUCTION).count();
            tableLines.add(new ArrayList<>(Arrays.asList(new String[]{
                type,
                String.valueOf(price),
                String.valueOf(nInstancesFree),
                String.valueOf(nInstancesOcupiedDemand),
                String.valueOf(nInstancesOcupiedAuction)
            })));
        });

        return new PrettyTable(tableLines).toString();
    }

    public void allocateServerToUser(User user, String serverType, String reservationNumber) throws NoServersAvailableException, InexistingServerTypeException {
        List<ServerInstance> instances = serverInstances.get(serverType);
        boolean allocated = false;
        
        Reservation reservation = new Reservation(serverType + reservationNumber, null, "WAITING");

        if (instances != null) {
            for (ServerInstance server : instances) {
                if (server.getState() == ServerState.FREE) {
                    server.allocate(user, ServerState.BUSY_DEMAND, serverType + reservationNumber);
                    reservation.setServer(server);
                    reservation.setState("ON ACTION");
                    user.addReservation(reservation);
                    allocated = true;
                    break;
                } else if (server.getState() == ServerState.BUSY_AUCTION) {
                    //TODO
                    break;
                } else if (server.getState() == ServerState.ON_AUCTION) //TODO
                {
                    break;
                }
            }
        } else {
            throw new InexistingServerTypeException();
        }

        if (!allocated) {
            //throw new NoServersAvailableException();
            user.addReservation(reservation);

        }

    }

    public void deallocateServer(String reservationNumber, String serverType) throws InexistingServerException {
        List<ServerInstance> instances = serverInstances.get(serverType);
        double finalCost = 0;
        boolean deallocated = false;
        
        for (ServerInstance server : instances) {
            if (server.getReservationId().equals(serverType + reservationNumber)) {
                User u = server.getAllocatedTo();
                Reservation r = u.getReservation(serverType + reservationNumber);
                r.setState("FINISHED");
                finalCost = server.deallocate();
                u.addtoCurrentDebt(finalCost);
                deallocated = true;
                break;
            }
        }

        if (!deallocated) {
            throw new InexistingServerException();
        }

    }
    
    /*
    public List<ServerInstance> listUserServers(User user) {
        List<ServerInstance> allocatedServers = new ArrayList<>();
        serverInstances.values().forEach((instances) -> {
            instances
                    .stream()
                    .filter((instance) -> (!instance.isAvailable() && instance.getAllocatedTo()
                    .equals(user)))
                    .forEach((instance) -> {
                        allocatedServers
                                .add(instance);
                    });
        });

        return allocatedServers;
    }*/
    
    public List<Reservation> listUserReservations(User user) {
        Map<String,Reservation> allReservations = user.getReservations();
        List<Reservation> reservations = new ArrayList<>();
        
        allReservations.values().stream().filter((r) -> (!r.getState().equals("FINISHED"))).forEachOrdered((r) -> {
            reservations.add(r);
        });

        return reservations;
    }
    
    public String getListUserServers(User user) {
        List<List<String>> tableLines = new ArrayList<>();
        tableLines.add(new ArrayList<>(Arrays.asList(new String[]{"IdReservation", "Type", "Current Time", "Price per hour", "Current Cost"})));

        List<Reservation> serversList = listUserReservations(user);
        serversList.forEach((reservation) -> {
            ServerInstance server = reservation.getServer();
            String id = reservation.getId();
            String type = server.getName();
            long currentTime = server.getCurrentTimeMilis();
            double price = server.getPricePerHour();
            double currentCost = server.getCurrentCost();
            tableLines.add(new ArrayList<>(Arrays.asList(new String[]{
                id,
                type,
                String.valueOf(currentTime / 1000) + " seconds",
                String.valueOf(price),
                String.valueOf(currentCost),})));
        });

        return new PrettyTable(tableLines).toString();
    }

}
