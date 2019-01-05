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
public class NoServersAvailableException extends Exception {

    /**
     * Creates a new instance of <code>NoServersAvailableException</code>
     * without detail message.
     */
    public NoServersAvailableException() {
    }

    /**
     * Constructs an instance of <code>NoServersAvailableException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoServersAvailableException(String msg) {
        super(msg);
    }
}
