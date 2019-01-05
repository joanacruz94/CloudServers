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
public class InexistingServerException extends Exception {

    /**
     * Creates a new instance of <code>InexistingServerException</code> without
     * detail message.
     */
    public InexistingServerException() {
    }

    /**
     * Constructs an instance of <code>InexistingServerException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InexistingServerException(String msg) {
        super(msg);
    }
}
