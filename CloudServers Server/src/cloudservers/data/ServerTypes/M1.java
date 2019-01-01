/*
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservers.data.ServerTypes;

import cloudservers.data.ServerInstance;

/**
 *
 * @author joanacruz
 */
public class M1 extends ServerInstance{
    
    public M1() {
        super("M1", 1.99);
    }
    
    public M1(M1 instance) {
        super(instance);
    }

    @Override
    public ServerInstance clone() {
        return new M1(this);
    }
}
