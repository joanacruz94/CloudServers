package cloudservers.data;

public class Bid {

    private User user;
    private String serverType;
    private String priceToPay;

    public Bid(){
        this.user = new User();
        this.serverType = "";
        this.priceToPay = "";
    }

    public Bid(User user, String serverType, String priceToPay){
        this.user = user;
        this.serverType = serverType;
        this.priceToPay = priceToPay;
    }

    public User getUser(){
        return this.user;
    }

    public void setUser(String email, String password){
        this.user = new User(email, password);
    }

    public String getServerType(){
        return this.serverType;
    }

    public void setServerType(String serverType){
        this.serverType = serverType;
    }

    public String getPriceToPay(){
        return this.priceToPay;
    }

    public void setPriceToPay(String priceToPay){
        this.priceToPay = priceToPay;
    }
}
