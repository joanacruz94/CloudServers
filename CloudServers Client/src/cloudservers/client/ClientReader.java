/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservers.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author joanacruz
 */
class ClientReader extends Thread {

    private BufferedReader in;
    private ArrayList<String> answers;

    public ClientReader(BufferedReader in) {
        this.in = in;
        answers = new ArrayList<>();
    }

    public void run() {
        String s;
        try {
            while (true) {
                while ((s = in.readLine()) != null) {
                    if (s.charAt(0) == ':') {
                        System.out.println("------------------------------------");
                        System.out.println("Notification " + s);
                        System.out.println("------------------------------------");
                    } else {
                        synchronized (answers) {
                            answers.add(s);
                            answers.notify();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readNext() throws InterruptedException {
        synchronized (answers) {
            while (answers.isEmpty()) {
                answers.wait();
            }
            String next = answers.get(0);
            answers.remove(0);
            return next;
        }
    }
}
