package com.pventura;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(5000)) {
            while(true) {
                // Socket socket = nerverSocket.accept();
                // Echoer echoer = new Echoer(socket);
                // echoer.start();
                new Echoer(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.out.println("Server exception " + e.getMessage());

        }
    }
}