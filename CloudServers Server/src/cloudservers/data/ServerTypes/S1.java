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
public class S1 extends ServerInstance{
    
    public S1() {
        super("S1", 1.99);
    }
    
    public S1(S1 instance) {
        super(instance);
    }

    @Override
    public ServerInstance clone() {
        return new S1(this);
    }
    
}
