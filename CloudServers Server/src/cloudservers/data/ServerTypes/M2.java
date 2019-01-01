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
public class M2 extends ServerInstance{
    
    public M2() {
        super("M2", 1.99);
    }
    
    public M2(M2 instance) {
        super(instance);
    }

    @Override
    public ServerInstance clone() {
        return new M2(this);
    }
    
}
