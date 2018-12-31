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
public class EmailAlreadyTakenException extends Exception {

    /**
     * Creates a new instance of <code>EmailAlreadyTakenException</code> without
     * detail message.
     */
    public EmailAlreadyTakenException() {
    }

    /**
     * Constructs an instance of <code>EmailAlreadyTakenException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public EmailAlreadyTakenException(String msg) {
        super(msg);
    }
}
