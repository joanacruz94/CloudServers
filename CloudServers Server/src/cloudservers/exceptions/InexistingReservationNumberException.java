/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservers.exceptions;

/**
 *
 * @author joanacruz
 */
public class InexistingReservationNumberException extends Exception {

    /**
     * Creates a new instance of <code>InexistingServerException</code> without
     * detail message.
     */
    public InexistingReservationNumberException() {
    }

    /**
     * Constructs an instance of <code>InexistingServerException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InexistingReservationNumberException(String msg) {
        super(msg);
    }
}
