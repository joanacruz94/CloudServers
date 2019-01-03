package cloudservers.data;

import cloudservers.data.User;
import java.util.concurrent.locks.ReentrantLock;

public class ServerInstance {

    private String name;
    private String id;
    private Reservation allocatedTo;
    private double pricePerHour;
    private ServerState state;
    private ReentrantLock lock;

    public ServerInstance(String name, String id, double pricePerHour) {
        this.name = name;
        this.id = "";
        this.pricePerHour = pricePerHour;
        this.allocatedTo = null;
        this.state = ServerState.FREE;
        this.lock = new ReentrantLock();
    }

    public ServerInstance(ServerInstance si) {
        this.name = si.name;
        this.id = si.id;
        this.allocatedTo = si.allocatedTo;
        this.pricePerHour = si.pricePerHour;
        this.state = si.state;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Reservation getAllocatedTo() {
        return allocatedTo;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public ServerState getState() {
        return state;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isAvailable() {
        return this.state == ServerState.FREE;
    }

    public void allocate(Reservation reservation, ServerState serverState) {
        this.allocatedTo = reservation;
        this.state = serverState;
    }

    public void deallocate() {
        this.allocatedTo = null;
        this.state = ServerState.FREE;
    }

    public ServerInstance clone() {
        return new ServerInstance(this);
    }
    
    public void lock(){
        this.lock.lock();
    }
    
    public void unlock(){
        this.lock.unlock();
    }

}
