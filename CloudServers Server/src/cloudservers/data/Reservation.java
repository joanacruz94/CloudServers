/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservers.data;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author joanacruz
 */
public class Reservation implements Comparable<Reservation> {

    private String id;
    private User user;
    private String serverType;
    private ServerInstance serverInstance;
    private ReservationState state;
    private long allocatedTime;
    private long deallocatedTime;
    private double pricePerHour;
    private String reservationType;
    private ReentrantLock lock;

    public Reservation(){
    }

    public Reservation(String id, User user, String serverType, double pricePerHour, String reservationType) {
        this.id = id;
        this.user = user;
        this.serverType = serverType;
        this.serverInstance = null;
        this.state = ReservationState.WAITING;
        this.allocatedTime = 0;
        this.deallocatedTime = 0;
        this.pricePerHour = pricePerHour;
        this.reservationType = reservationType;
        this.lock = new ReentrantLock();
    }

    public User getUser() {
        return user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ReservationState getState() {
        return state;
    }

    public void setState(ReservationState state) {
        this.state = state;
    }

    public String getServerType() {
        return serverType;
    }

    public double getPricePerHour() {
        return this.pricePerHour;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public ServerInstance getServerInstance() {
        return serverInstance;
    }

    public void setServerInstance(ServerInstance serverInstance) {
        this.serverInstance = serverInstance;
    }

    public long getAllocatedTime() {
        return allocatedTime;
    }

    public void allocate(ServerInstance serverInstance) {
        //TODO : modify allocation when auctions are implemented
        this.serverInstance = serverInstance;
        this.allocatedTime = System.currentTimeMillis();
        if(this.reservationType.equals("DEMAND")){
            this.serverInstance.allocate(this, ServerState.BUSY_DEMAND);
        }
        else{
            this.serverInstance.allocate(this, ServerState.BUSY_SPOT);
        }
    }

    public long getSpentTimeMilis() {
        long begin = this.allocatedTime;
        long end;
        if (this.deallocatedTime == 0) {
            end = System.currentTimeMillis();
        } else {
            end = this.deallocatedTime;
        }
        long timeInMilis = end - begin;
        return timeInMilis;
    }

    public double getCurrentCost() {
        //TODO: the price per hour will change when auctions are implemented
        DecimalFormat df = new DecimalFormat("#.00");
        long timeInMilis = getSpentTimeMilis();

        double timeInHours = ((Number) timeInMilis).doubleValue() / 1000 / 3600;
        double pricePerHour;
        if (this.reservationType.equals("SPOT")) {
            pricePerHour = this.pricePerHour;
        } else {
            pricePerHour = this.serverInstance.getPricePerHour();
        }
        return Double.parseDouble(df.format(timeInHours * pricePerHour));
    }

   
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Reservation other = (Reservation) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Reservation reservation){
        int res;

        if(this.getPricePerHour() < reservation.getPricePerHour()) res = 1;
        else if(this.getPricePerHour() > reservation.getPricePerHour()) res = -1;
        else res = 0;

        return res;
    }

    public void deallocate() {
        this.state = ReservationState.FINISHED;
        this.serverInstance.lock();
        this.serverInstance.deallocate();
        this.serverInstance.unlock();
        this.deallocatedTime = System.currentTimeMillis();
    }

    public void lock() {
        this.lock.lock();
    }

    public void unlock() {
        this.lock.unlock();
    }

}
