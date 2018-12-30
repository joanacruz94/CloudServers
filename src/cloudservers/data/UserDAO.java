package cloudservers.data;

import cloudservers.data.User;
import cloudservers.exceptions.EmailAlreadyTakenException;
import cloudservers.exceptions.NotExistantUserException;

import java.util.HashMap;

public class UserDAO {

    private HashMap<String, User> users = new HashMap<>();

    private static UserDAO ourInstance = new UserDAO();

    public static UserDAO getInstance() {
        return ourInstance;
    }

    private UserDAO() {
        this.users.put("1", new User("1", "1"));
        this.users.put("2", new User("2", "2"));
    }

    public boolean auth(String email, String password){

        User u = this.users.get(email);

        if(u == null) {
            return false;
        }

        return u.validPassword(password);
    }
    
    public User getUserByEmail(String email) throws NotExistantUserException{
        User u = this.users.get(email);

        if(u == null) {
            throw new NotExistantUserException();
        }

        return u;
    }
    
    public void addUser(String email, String password) throws EmailAlreadyTakenException{
        if(!users.containsKey(email)){
            users.put(email, new User(email, password));
        }
        else{
            throw new EmailAlreadyTakenException();
        }
    }
}
