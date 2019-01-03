/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservers.data;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Samuel
 */
public class ListAndLockPair {

    List<ServerInstance> list;
    ReentrantLock lock;

    public ListAndLockPair(List<ServerInstance> list, ReentrantLock lock) {
        this.list = list;
        this.lock = lock;
    }

    public List<ServerInstance> getList() {
        return list;
    }

    public ReentrantLock getLock() {
        return lock;
    }
    
    public void lock(){
        this.lock.lock();
    }
    
    public void unlock(){
        this.lock.unlock();
    }

}
