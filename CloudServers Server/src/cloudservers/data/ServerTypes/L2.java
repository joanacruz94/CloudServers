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
public class L2 extends ServerInstance{
    
    public L2() {
        super("L2", 1.99);
    }
    
    public L2(L2 instance) {
        super(instance);
    }

    @Override
    public ServerInstance clone() {
        return new L2(this);
    }
    
}
