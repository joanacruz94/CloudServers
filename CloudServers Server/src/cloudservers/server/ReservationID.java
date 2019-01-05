package cloudservers.server;

public class ReservationID {
    private static ReservationID ourInstance = new ReservationID();

    public static ReservationID getInstance() {
        return ourInstance;
    }

    private ReservationID() {
    }
}
