package cloudservers.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BidsDAO {

    public Set<Reservation> waitingBids = new TreeSet<Reservation>();

    public static ReentrantLock lock = new ReentrantLock();

    private static BidsDAO ourInstance = new BidsDAO();

    public static BidsDAO getInstance() {
        return ourInstance;
    }

    public void removeFromList(List<Reservation> reservations){
        waitingBids.removeAll(reservations);
    }


    public List<Reservation> getUserBids(User u){
        List<Reservation> reservations = new ArrayList<>();
        waitingBids.stream().filter((r) -> (r.getUser().equals(u))).forEach((r) -> {
            reservations.add(r);
        });
        return reservations;
    }


}
