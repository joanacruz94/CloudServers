package cloudservers.data;

import java.util.ArrayList;
import java.util.List;

public class BidsDAO {

    private List<Bid> bids;

    private static BidsDAO ourInstance = new BidsDAO();

    public static BidsDAO getInstance() {
        return ourInstance;
    }

    private BidsDAO() {
        this.bids = new ArrayList<>();
    }
}
