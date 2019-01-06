package cloudservers.data;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class User {

    private String mail;
    private String password;
    private Map<String, Reservation> reservations;
    private ReentrantLock lock;

    public User(){
        this.mail = "";
        this.password = "";
        this.reservations = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public User(String mail, String password){
        this.mail = mail;
        this.password = password;
        this.reservations = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public boolean validPassword(String password){

        return this.password.equals(password);
    }
    
    
    public Map<String, Reservation> getReservations(){
        return this.reservations;
    }

    public Reservation getReservation(String numberReservation){
        return this.reservations.get(numberReservation);
    }
    
    public void  addReservation(Reservation r){
        this.reservations.put(r.getId(), r);
    }
    
    public String getEmail(){
        return this.mail;
    }
    
    public double getCurrentDebt(){
        DecimalFormat df = new DecimalFormat("#.00");
        double sum = 0;
        for(Reservation r : this.reservations.values()){
            sum += r.getCurrentCost();
        }
        return Double.parseDouble(df.format(sum));
    }    
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.mail);
        return hash;
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
        final User other = (User) obj;
        if (!Objects.equals(this.mail, other.mail)) {
            return false;
        }
        return true;
    }

    public void lock() {
        this.lock.lock();
    }
    
    public void unlock() {
        this.lock.unlock();
    }
    
    
}
