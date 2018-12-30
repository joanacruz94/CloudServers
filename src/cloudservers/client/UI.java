/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservers.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author joanacruz
 */
public class UI {
    private Scanner scanner;
    
    public UI(){
        this.scanner = new Scanner(System.in);
    }
    
    private String readLine(){
        return scanner.nextLine();
    }
    
    public String showWelcomeMenu(){
        List<Option> options = new ArrayList<>();
        options.add(new Option("Login", "1"));
        options.add(new Option("Register", "2"));
        options.add(new Option("Exit", "0"));
        Menu welcomeMenu = new Menu(options, "Welcome");
        welcomeMenu.show();
        String selectedOption = readLine();
        String res = null;
        switch(selectedOption){
            case "1":
                System.out.println("yyayyy, login :D");
                res = "login";
                break;
            case "0":
                System.out.println("ooooohhh :(");
                res = "exit";
                break;
            case "2":
                System.out.println("registo");
                res = "register";
                break;
            default:
                System.out.println("Please select one of the available options");
                
                waitForEnter();
                showWelcomeMenu();
        }
        return res;
        
    }
    
    public String showLoginMenu(){
        showHeader("Login");
        System.out.println("Insert an email ");
        String email = scanner.nextLine();
        System.out.println("Insert a password ");
        String password = scanner.nextLine();
        
        // TODO : check the length of the provided fields
        return email + " " + password;
    }
    
    public String showRegisterMenu(){
        showHeader("Register");
        System.out.println("Insert an email ");
        String email = scanner.nextLine();
        System.out.println("Insert a password ");
        String password = scanner.nextLine();
        
        // TODO : check the length of the provided fields
        return email + " " + password;
    }

    private void waitForEnter() {
        System.out.println("Press enter to continue");
        scanner.nextLine();
    }
    
    private void showHeader(String name){
        System.out.println("--------------------------------------------------------------------");
        System.out.println("                          " + name);
        System.out.println("--------------------------------------------------------------------");
    }
    
}
