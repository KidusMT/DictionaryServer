/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dictionaryserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author KidusMT
 */
public class ClientRequestHandler extends Thread{

    
    InputStream inp ;
    BufferedReader brinp;
    DataOutputStream dos;
    DataInputStream dis;
    List<String> definitions;
    
    protected Socket socket;
    protected Dictionary dictionary;
    
    public ClientRequestHandler(Socket socket, Dictionary dictionary){
        this.socket = socket;
        this.dictionary = dictionary;
        inp = null;
        brinp = null;
        dos = null;
        dis = null;
    }    
    // Create data input and output streams

    @Override
    public void run() {
        System.out.println("client request processing launched");
        try {
            inp = socket.getInputStream();
            brinp = new BufferedReader(new InputStreamReader(inp));
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String line;
        while (true) {
            try {
                System.out.println("Line about to be read by server");
                line = dis.readUTF();
                System.out.println("Line read " + line);
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    System.out.println("request processing is getting closed");
                    socket.close();
                    return;
                } else {
                    String searchWord = line;
                    List<String> meanings = new ArrayList<>();
                    
                    if(dictionary.hasWord(searchWord)){
                        System.out.println("search word " + searchWord + " found");
                        meanings = dictionary.getDefinitions(searchWord);
                    }else{
                        System.out.println("search word " + searchWord + " not found");
                        meanings.add("word not found");
                    }
                    definitions = meanings;
                    System.out.println("meanings found " + meanings);
                    for(String meaning : meanings){
                        dos.writeUTF(meaning);
                    }
                    dos.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
