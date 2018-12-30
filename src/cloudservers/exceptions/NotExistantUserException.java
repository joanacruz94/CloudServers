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
public class NotExistantUserException extends Exception {

    /**
     * Creates a new instance of <code>NotExistantUserExceptio</code> without
     * detail message.
     */
    public NotExistantUserException() {
    }

    /**
     * Constructs an instance of <code>NotExistantUserExceptio</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NotExistantUserException(String msg) {
        super(msg);
    }
}
