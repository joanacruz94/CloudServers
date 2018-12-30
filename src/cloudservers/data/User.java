package cloudservers.data;

public class User {

    private String mail;
    private String password;

    public User(){
        this.mail = "";
        this.password = "";
    }

    public User(String mail, String password){
        this.mail = mail;
        this.password = password;
    }

    public boolean validPassword(String password){

        return this.password.equals(password);
    }
}
