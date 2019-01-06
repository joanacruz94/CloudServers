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
import cloudservers.data.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


class Notificator implements Runnable{
    Socket cs;
    private String idUser;
    private UserDAO users = UserDAO.getInstance();
    
    Notificator(Socket cs, String idUser){
        this.cs=cs;
        this.idUser = idUser;
    }
    
    public void run(){
        try{
            PrintWriter out= new PrintWriter(cs.getOutputStream(),true);
            List<String> userNotifications = users.notificationsUsers.get(idUser);
            
            UserDAO.lock.lock();
            if(userNotifications==null){
                users.notificationsUsers.put(idUser, new ArrayList<>());
                userNotifications = users.notificationsUsers.get(idUser);
            }
            try {
                while(true){
                    while(userNotifications.isEmpty()){
                        UserDAO.hasNotifications.await();
                    }
                    
                    String notification = users.notificationsUsers.get(idUser).remove(0);
                    out.println(notification);
                }
            } finally {
                UserDAO.lock.unlock();
            }
        }catch(IOException | InterruptedException e){e.printStackTrace();}
    }
}
