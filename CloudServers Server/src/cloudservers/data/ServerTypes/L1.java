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
public class L1 extends ServerInstance{
    
    public L1() {
        super("L1", 1.99);
    }
    
    public L1(L1 instance) {
        super(instance);
    }

    @Override
    public ServerInstance clone() {
        return new L1(this);
    }
    
}
