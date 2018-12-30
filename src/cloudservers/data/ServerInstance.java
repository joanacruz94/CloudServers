package cloudservers.data;

import cloudservers.data.User;

public class ServerInstance {

    private String name;
    private String type;
    private User allocatedTo;

    public ServerInstance(String name, String type){
        this.name = name;
        this.type = type;
    }

    public void allocate(User u){
        this.allocatedTo = u;
    }

    public void deallocate(){
        this.allocatedTo = null;
    }


}
