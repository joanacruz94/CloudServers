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

    private static Scanner scanner = new Scanner(System.in);

    private static String readLine() {
        return scanner.nextLine();
    }

    public static String showWelcomeMenu() {
        List<Option> options = new ArrayList<>();
        options.add(new Option("Login", "1"));
        options.add(new Option("Register", "2"));
        options.add(new Option("Exit", "0"));
        Menu welcomeMenu = new Menu(options, "Welcome");
        welcomeMenu.show();
        String selectedOption = readLine();
        String res = null;
        switch (selectedOption) {
            case "1":
                res = "login";
                break;
            case "0":
                res = "exit";
                break;
            case "2":
                res = "register";
                break;
            default:
                System.out.println("Please select one of the available options");
                waitForEnter();
                res = showWelcomeMenu();
        }
        return res;
    }

    public static String showUserMenu() {
        List<Option> options = new ArrayList<>();
        options.add(new Option("Display servers list", "1"));
        options.add(new Option("Display my servers", "2"));
        options.add(new Option("Display current debt", "3"));
        options.add(new Option("Logout", "0"));
        Menu userMenu = new Menu(options, "User Menu");
        userMenu.show();
        String selectedOption = readLine();
        String res = null;
        switch (selectedOption) {
            case "1":
                res = "serversList";
                break;
            case "2":
                res = "myServers";
                break;
            case "3":
                res = "currentDebt";
                break;
            case "0":
                res = "logout";
                break;
            default:
                System.out.println("Please select one of the available options");
                waitForEnter();
                res = showUserMenu();
        }
        return res;
    }

    public static String showGetServersMenu() {
        List<Option> options = new ArrayList<>();
        options.add(new Option("Get server on demand", "1"));
        options.add(new Option("Get server on auction", "2"));
        options.add(new Option("Refresh servers list", "3"));
        options.add(new Option("Go Back", "0"));
        Menu getServersMenu = new Menu(options, "Get Servers Menu");
        getServersMenu.show();
        String selectedOption = readLine();
        String res = null;
        switch (selectedOption) {
            case "1":
                res = "serverDemand";
                break;
            case "2":
                res = "serverAuction";
                break;
            case "3":
                res = "refresh";
                break;
            case "0":
                res = "goBack";
                break;
            default:
                System.out.println("Please select one of the available options");
                waitForEnter();
                res = showGetServersMenu();
        }
        return res;
    }
    
    public static String showMyServersMenu() {
        List<Option> options = new ArrayList<>();
        options.add(new Option("Deallocate server", "1"));
        options.add(new Option("Refresh my servers list", "2"));
        options.add(new Option("Go Back", "0"));
        Menu myServers = new Menu(options, "My Servers Menu");
        myServers.show();
        String selectedOption = readLine();
        String res = null;
        switch (selectedOption) {
            case "1":
                res = "deallocate";
                break;
            case "2":
                res = "refresh";
                break;
            case "0":
                res = "goBack";
                break;
            default:
                System.out.println("Please select one of the available options");
                waitForEnter();
                res = showMyServersMenu();
        }
        return res;
    }

    public static String showLoginMenu() {
        showHeader("Login");
        System.out.println("Insert an email ");
        String email = scanner.nextLine();
        System.out.println("Insert a password ");
        String password = scanner.nextLine();

        //TODO : check the length of the provided fields
        return email + " " + password;
    }

    public static String showRegisterMenu() {
        showHeader("Register");
        System.out.println("Insert an email ");
        String email = scanner.nextLine();
        System.out.println("Insert a password ");
        String password = scanner.nextLine();

        //TODO : check the length of the provided fields
        return email + " " + password;
    }
    
    public static String showAuctionServerMenu() {
        System.out.println("Write the type of the server you want to allocate");
        String serverType = scanner.nextLine();
        System.out.println("How much would you like to pay");
        String price = scanner.nextLine();

        //TODO : check the length of the provided fields
        return serverType + " " + price;
    }

    public static void waitForEnter() {
        System.out.println("Press enter to continue");
        scanner.nextLine();
    }

    private static void showHeader(String name) {
        System.out.println("--------------------------------------------------------------------");
        System.out.println("                          " + name);
        System.out.println("--------------------------------------------------------------------");
    }

    public static String prompt(String s) {
        System.out.println(s);
        return scanner.nextLine();
        
    }

}
