package cloudservers.server;

public class ReservationID {

    private int id;

    private static ReservationID ourInstance = new ReservationID();

    public static ReservationID getInstance() {
        return ourInstance;
    }

    private ReservationID() {
        this.id = 1;
    }

    public int nextID(){
        return id++;
    }
}
