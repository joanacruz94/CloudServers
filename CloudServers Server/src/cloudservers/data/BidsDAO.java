package cloudservers.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

public class BidsDAO {

    public Set<Reservation> waitingBids = new TreeSet<>();

    public static ReentrantLock lock = new ReentrantLock();

    private static BidsDAO ourInstance = new BidsDAO();

    public static BidsDAO getInstance() {
        return ourInstance;
    }

    public void removeFromList(List<Reservation> reservations){
        lock.lock();
        waitingBids.removeAll(reservations);
        lock.unlock();
    }

    public List<Reservation> getUserBids(User u){
        List<Reservation> reservations = new ArrayList<>();
        lock.lock();
        waitingBids.stream().filter((r) -> (r.getUser().equals(u))).forEach((r) -> {
            reservations.add(r);
        });
        lock.unlock();
        return reservations;
    }


}
