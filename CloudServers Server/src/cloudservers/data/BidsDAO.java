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
        this.lock();
        waitingBids.removeAll(reservations);
        lock.unlock();
    }

    public List<Reservation> getUserBids(User u){
        List<Reservation> reservations = new ArrayList<>();
        this.lock();
        waitingBids.stream().filter((r) -> (r.getUser().equals(u))).forEach((r) -> {
            reservations.add(r);
        });
        this.unlock();
        return reservations;
    }
    
    public void lock(){
        this.lock.lock();
    }
    
    public void unlock(){
        this.lock.unlock();
    }


}
