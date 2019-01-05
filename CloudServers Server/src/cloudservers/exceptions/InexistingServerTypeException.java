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
public class InexistingServerTypeException extends Exception {

    /**
     * Creates a new instance of <code>InexistingServerTypeException</code>
     * without detail message.
     */
    public InexistingServerTypeException() {
    }

    /**
     * Constructs an instance of <code>InexistingServerTypeException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public InexistingServerTypeException(String msg) {
        super(msg);
    }
}
