/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dictionaryserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author KidusMT
 */
public class MainThread extends Thread {

    public Dictionary dictionary;
    private ServerSocket serverSocket;

    public MainThread() {
    }

    public MainThread(String fileName) {
        this.dictionary = new Dictionary(fileName);

        try {
            // Create a server socket
            this.serverSocket = new ServerSocket(8000);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void run() {
        while (true) {
            
            try {
                // Listen for a connection request
                Socket socket = serverSocket.accept();
                Thread thread = new ClientRequestHandler(socket, this.dictionary);
                thread.start();
                System.out.println("specific client thread started");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
             
        }
    }

}
