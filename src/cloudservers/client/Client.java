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
            String input1 = null;
            String serverAnswer = null;
            
            switch(input){
                case "login":
                    input1 = ui.showLoginMenu();
                    w.println(input + " " + input1);
                    w.flush();
                    serverAnswer = r.readLine();
                    System.out.println(serverAnswer);
                    break;
                case "register":
                    input1 = ui.showRegisterMenu();
                    w.println(input + " " + input1);
                    w.flush();
                    serverAnswer = r.readLine();
                    System.out.println(serverAnswer);
                    break;
                case "exit":
                    System.out.println("Sucess exit");
                    break;
            }
            w.println("exit");
            w.flush();
            s.close();
        } catch (java.net.ConnectException e) {
            System.out.println("Could not establish a connection with the server. Please make sure the server is running and try again.");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
