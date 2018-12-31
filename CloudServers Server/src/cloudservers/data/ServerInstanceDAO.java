package cloudservers.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cloudservers.data.ServerTypes.*;
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
            clones.add(instance.clone());
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
            float price = instancesOfType.get(0).getPricePerHour();
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

    public void allocateServerToUser(User user, String serverType) throws NoServersAvailableException, InexistingServerTypeException {
        List<ServerInstance> instances = serverInstances.get(serverType);
        boolean allocated = false;

        if (instances != null) {
            for (ServerInstance server : instances) {
                if (server.getState() == ServerState.FREE) {
                    server.allocate(user, ServerState.BUSY_DEMAND);
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
            throw new NoServersAvailableException();
        }

    }
}
