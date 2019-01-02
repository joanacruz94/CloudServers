/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservers.data;

/**
 *
 * @author joanacruz
 */
public class Reservation {
    private String id;
    private ServerInstance serverType;
    private String state;

    public Reservation(String id, ServerInstance serverType, String state) {
        this.id = id;
        this.serverType = serverType;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ServerInstance getServer() {
        return serverType;
    }

    public void setServer(ServerInstance serverType) {
        this.serverType = serverType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
    
}
