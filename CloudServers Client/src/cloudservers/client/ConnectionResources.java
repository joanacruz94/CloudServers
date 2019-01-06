/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservers.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author joanacruz
 */
public class ConnectionResources {
    private Socket s;
    private PrintWriter w;
    private BufferedReader r;
    
    public ConnectionResources() throws IOException{
        this.s = new Socket("localhost", 12346);
        this.w = new PrintWriter(s.getOutputStream());
        this.r = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }
    
    public BufferedReader getBufferedReader(){
        return this.r;
    }
    
    public void writeToServer(String s){
        this.w.println(s);
        this.w.flush();
    }
    
    public String readFromServer() throws IOException{
        return this.r.readLine();
    }

    public void closeSocket() throws IOException {
        w.close();
        r.close();
        s.close();
    }
}
