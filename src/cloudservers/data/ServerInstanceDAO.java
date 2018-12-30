package cloudservers.data;

import java.util.HashMap;
import java.util.List;

public class ServerInstanceDAO {

    private List<ServerInstance> serverInstances;

    private static ServerInstanceDAO ourInstance = new ServerInstanceDAO();

    public static ServerInstanceDAO getInstance() {
        return ourInstance;
    }

    private ServerInstanceDAO() {
        this.serverInstances.add(new ServerInstance("s1", "small"));
        this.serverInstances.add(new ServerInstance("s2", "small"));
        this.serverInstances.add(new ServerInstance("l1", "large"));
        //this.serverInstances.add(new ServerInstance("m1", "medium")); Podia haver mas não há
    }
}
