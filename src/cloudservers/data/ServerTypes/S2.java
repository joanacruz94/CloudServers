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
public class S2 extends ServerInstance{
    
    public S2() {
        super("S2", (float)1.99);
    }
    
    public S2(S2 instance) {
        super(instance);
    }

    @Override
    public ServerInstance clone() {
        return new S2(this);
    }
}
