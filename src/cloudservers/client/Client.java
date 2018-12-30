package cloudservers.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client{

    public static void main(String[] args) throws Exception {
        try {
            Socket s = new Socket("localhost", 12346);
            

            PrintWriter w = new PrintWriter(s.getOutputStream());
            BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
            System.out.println("Connection established to the server");
            UI ui = new UI();
            String input = ui.showWelcomeMenu();
            
            switch(input){
                case "login":
                    input = ui.showLoginMenu();
                    w.print("login " + input);
                case "register":
                    input = ui.showRegisterMenu();
                    w.print("register " + input);
                case "exit":
                    System.out.println("Sucess exit");
            }
            
            

            w.flush();

            System.out.println(r.readLine());

            s.close();
        } catch (java.net.ConnectException e) {
            System.out.println("Could not establish a connection with the server. Please make sure the server is running and try again.");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
