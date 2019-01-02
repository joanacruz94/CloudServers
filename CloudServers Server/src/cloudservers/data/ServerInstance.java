package cloudservers.data;

import cloudservers.data.User;

public abstract class ServerInstance {

    private String name;
    private String id;
    private User allocatedTo;
    private double pricePerHour;
    private ServerStates state;
    private long allocatedTime;
    private String reservationId;

    public ServerInstance(String name, double pricePerHour) {
        this.name = name;
        this.id = "";
        this.pricePerHour = pricePerHour;
        this.allocatedTo = null;
        this.state = ServerStates.FREE;
        this.allocatedTime = 0;
        this.reservationId = "";
    }

    public ServerInstance(ServerInstance si) {
        this.name = si.name;
        this.id = si.id;
        this.allocatedTo = si.allocatedTo;
        this.pricePerHour = si.pricePerHour;
        this.state = si.state;
        this.allocatedTime = si.allocatedTime;
        this.reservationId = si.reservationId;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public User getAllocatedTo() {
        return allocatedTo;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public ServerStates getState() {
        return state;
    }

    public long getAllocatedTime() {
        return allocatedTime;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPricePerHour(float pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public boolean isAvailable() {
        return this.state == ServerStates.FREE || this.state == ServerStates.ON_SPOT;
    }

    public void allocate(User user, ServerStates serverState, String reservationId) {
        this.allocatedTo = user;
        this.state = serverState;
        this.allocatedTime = System.currentTimeMillis();
        this.reservationId = reservationId;
    }
    
    public long getCurrentTimeMilis(){
        long begin = this.allocatedTime;
        long end = System.currentTimeMillis();
        long timeInMilis = end - begin;
        return timeInMilis;
    }
    
    public double getCurrentCost() {
        long timeInMilis = getCurrentTimeMilis();
        double timeInHours = ((Number)timeInMilis).doubleValue() / 1000 / 3600;
        return timeInHours * this.pricePerHour;
    }

    public double deallocate() {
        double currentCost = getCurrentCost();
        this.allocatedTo = null;
        this.state = ServerStates.FREE;
        this.allocatedTime = 0;
        return currentCost;
    }

    public abstract ServerInstance clone();

}
