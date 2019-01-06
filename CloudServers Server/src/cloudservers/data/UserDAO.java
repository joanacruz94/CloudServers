package cloudservers.data;

import cloudservers.exceptions.EmailAlreadyTakenException;
import cloudservers.exceptions.NotExistantUserException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class UserDAO {

    private HashMap<String, User> users = new HashMap<>();
    private static UserDAO ourInstance = new UserDAO();
    public List<String> notificationsList = new ArrayList<>();
    public static ReentrantLock lock = new ReentrantLock();
    public static Condition hasNotifications = lock.newCondition(); 


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
