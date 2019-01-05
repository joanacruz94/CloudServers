/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservers.server;

/**
 *
 * @author joanacruz
 */
public class ReservationId {
    private int id=1;
    
    synchronized String nextId(){
        return String.valueOf(id++);
    }
    
}
