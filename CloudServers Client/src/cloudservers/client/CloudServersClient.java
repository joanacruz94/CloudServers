package cloudservers.client;

import java.io.IOException;

public class CloudServersClient {

    public static void main(String[] args) throws Exception {
        try {
            ConnectionResources cr = new ConnectionResources();
            
            System.out.println("Connection established to the server");      
            String input, serverAnswer;
            do {
                input = UI.showWelcomeMenu();
                switch (input) {
                    case "login":
                        serverAnswer = enterLoginStage(cr);
                        if (serverAnswer.startsWith("Success")) {
                            enterUserStage(cr);
                        } else {
                            System.out.println(serverAnswer);
                        }
                        break;
                    case "register":
                        enterRegisterStage(cr);
                        break;
                    case "exit":
                        System.out.println("Bye");
                        break;
                }
            } while (!input.equals("exit"));
            cr.writeToServer("exit");
            cr.closeSocket();
        } catch (java.net.ConnectException e) {
            System.out.println("Could not establish a connection with the server. Please make sure the server is running and try again.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String enterLoginStage(ConnectionResources cr) throws IOException, InterruptedException {
        String serverAnswer;
        String input = UI.showLoginMenu();
        cr.writeToServer("login " + input);
        serverAnswer = cr.readFromServer();
        return serverAnswer;
    }

    private static void enterRegisterStage(ConnectionResources cr) throws IOException, InterruptedException {
        String serverAnswer;
        String input = UI.showRegisterMenu();
        cr.writeToServer("register " + input);
        serverAnswer = cr.readFromServer();
        System.out.println(serverAnswer);
    }

    private static void enterUserStage(ConnectionResources cr) throws IOException, InterruptedException {
        String input, serverAnswer;
        do {
            input = UI.showUserMenu();
            switch (input) {
                case "serversList":
                    enterServersListStage(cr);
                    break;
                case "myServers":
                    enterMyServersStage(cr);
                    break;
                case "bidsList":
                    enterMyBidsStage(cr);
                    break;
                case "currentDebt":
                    cr.writeToServer(input);
                    serverAnswer = cr.readFromServer();
                    System.out.println(serverAnswer);
                    break;
                case "logout":
                    cr.writeToServer(input);
                    serverAnswer = cr.readFromServer();
                    System.out.println(serverAnswer);
                    break;
            }
        } while (!input.equals("logout"));
    }

    private static void enterServersListStage(ConnectionResources cr) throws IOException, InterruptedException {
        String input, serverAnswer;
        cr.writeToServer("serversList");
        serverAnswer = cr.readFromServer();
        serverAnswer = serverAnswer.replace(";", "\n");
        System.out.println(serverAnswer);
        do {
            input = UI.showGetServersMenu();
            switch (input) {
                case "serverDemand":
                    input = UI.prompt("Write the type of the server you want to allocate");
                    cr.writeToServer("serverDemand " + input);
                    serverAnswer = cr.readFromServer();
                    if (!serverAnswer.startsWith("Error")) {
                        System.out.println("Your reservation number is " + serverAnswer);
                        UI.waitForEnter();
                    } else {
                        System.out.println(serverAnswer);
                        UI.waitForEnter();
                    }
                    break;
                case "serverAuction":
                    input = UI.showAuctionServerMenu();
                    cr.writeToServer("serverAuction " + input);
                    serverAnswer = cr.readFromServer();
                     if (!serverAnswer.startsWith("Error")) {
                        System.out.println("Your reservation number is " + serverAnswer);
                        UI.waitForEnter();
                    } else {
                        System.out.println(serverAnswer);
                        UI.waitForEnter();
                    }
                    break;
                case "refresh":
                    cr.writeToServer("serversList");
                    serverAnswer = cr.readFromServer();
                    serverAnswer = serverAnswer.replace(";", "\n");
                    System.out.println(serverAnswer);
                    break;
                case "goBack":
                    break;
            }
        } while (!input.equals("goBack"));

    }

    private static void enterMyServersStage(ConnectionResources cr) throws IOException, InterruptedException {
        String input, serverAnswer;
        cr.writeToServer("myServers");
        serverAnswer = cr.readFromServer();
        serverAnswer = serverAnswer.replace(";", "\n");
        System.out.println(serverAnswer);
        do {
            input = UI.showMyServersMenu();
            switch (input) {
                case "deallocate":
                    input = UI.prompt("Insert the ID of the reservation of the server you want to deallocate");
                    cr.writeToServer("deallocate " + input);
                    serverAnswer = cr.readFromServer();
                    if (serverAnswer.startsWith("Success")) {
                        System.out.println("You deallocated the server successfully");
                        UI.waitForEnter();
                    } else {
                        System.out.println(serverAnswer);
                        UI.waitForEnter();
                    }
                    break;
                case "cancel":
                    input = UI.prompt("Insert the ID of the reservation of the server you want to cancel");
                    cr.writeToServer("cancel " + input);
                    serverAnswer = cr.readFromServer();
                    System.out.println(serverAnswer);
                    break;
                case "refresh":
                    cr.writeToServer("myServers");
                    serverAnswer = cr.readFromServer();
                    serverAnswer = serverAnswer.replace(";", "\n");
                    System.out.println(serverAnswer);
                    break;
                case "goBack":
                    break;
            }
        } while (!input.equals("goBack"));
    }
    
    private static void enterMyBidsStage(ConnectionResources cr) throws IOException, InterruptedException {
        String input, serverAnswer;
        cr.writeToServer("bidsList");
        serverAnswer = cr.readFromServer();
        serverAnswer = serverAnswer.replace(";", "\n");
        System.out.println(serverAnswer);
        do {
            input = UI.showMyBidsMenu();
            switch (input) {
                case "cancelBid":
                    input = UI.prompt("Insert the ID of the bid reservation of the server you want to cancel");
                    cr.writeToServer("cancelBid " + input);
                    serverAnswer = cr.readFromServer();
                    System.out.println(serverAnswer);
                    break;
                case "refresh":
                    cr.writeToServer("bidsList");
                    serverAnswer = cr.readFromServer();
                    serverAnswer = serverAnswer.replace(";", "\n");
                    System.out.println(serverAnswer);
                    break;
                case "goBack":
                    break;
            }
        } while (!input.equals("goBack"));
    }

}
