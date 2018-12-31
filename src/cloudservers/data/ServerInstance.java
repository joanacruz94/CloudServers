package cloudservers.data;

import cloudservers.data.User;

public abstract class ServerInstance {

    private String name;
    private String id;
    private User allocatedTo;
    private float pricePerHour;
    private ServerState state;
    private long allocatedTime;

    public ServerInstance(String name, float pricePerHour){
        this.name = name;
        this.id = "";
        this.pricePerHour = pricePerHour;
        this.allocatedTo = null;
        this.state = ServerState.FREE;
        this.allocatedTime = 0;
    }
    
    public ServerInstance(ServerInstance si){
        this.name = si.name;
        this.id = si.id;
        this.allocatedTo = si.allocatedTo;
        this.pricePerHour = si.pricePerHour;
        this.state = si.state;
        this.allocatedTime = si.allocatedTime;
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

    public float getPricePerHour() {
        return pricePerHour;
    }

    public ServerState getState() {
        return state;
    }

    public long getAllocatedTime() {
        return allocatedTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPricePerHour(float pricePerHour) {
        this.pricePerHour = pricePerHour;
    }
    
    public void allocate(User user, ServerState serverState){
        this.allocatedTo = user;
        this.state = serverState;
        this.allocatedTime = System.currentTimeMillis();
    }
    
    public float getCurrentCost(){
        long begin = this.allocatedTime;
        long end = System.currentTimeMillis();
        long timeInMilis = end - begin;
        float timeInHours = timeInMilis / 1000 / 3600;
        return timeInHours * this.pricePerHour;
    }

    public float deallocate(){
        float currentCost = getCurrentCost();
        this.allocatedTo = null;
        this.state = ServerState.FREE;
        this.allocatedTime = 0;
        return currentCost;
    }
    
    public abstract ServerInstance clone();


}
