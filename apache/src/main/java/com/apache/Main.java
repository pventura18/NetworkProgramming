package com.apache;

import java.net.*;

public class Main {
    public static void main(String[] args) {
        String serverHost = "localhost"; // Change this to your server's hostname or IP address
        int serverPort = 5902; // Change this to your server's port

        String imei = "710415";

        sendPackets(serverPort);
    }


    private static void sendPackets(int port) {
        try {
            InetAddress address = InetAddress.getLocalHost(); // or specify the server address
            DatagramSocket socket = new DatagramSocket();
    
            String packetString = "610200140FD70A00A000DB792D2F004F004088DA10010001F801F8F900FA0001F801F803F803F803F803F802F802F85E3880000000000000000000000000000000000000000000000000000000000000000000000000000000000000000124004088DA100000000000000000000000000000000000000000000000000000000000032A004088DA10F7467B839A32FC7B024388DA10000000000000000000000000002900000000390A0002000000000F333536333037303432343431303133";
            byte[] packetBytes = new byte[packetString.length()];
    
            for (int i = 0; i < packetString.length(); i++) {
                char c = packetString.charAt(i);
                byte byteVal = (byte) c;
                packetBytes[i] = byteVal;
            }
    
            DatagramPacket packet = new DatagramPacket(packetBytes, packetBytes.length, address, port);
    
            // Send packet
            socket.send(packet);
    
            // Receive response packet (if needed)
            // byte[] receiveData = new byte[1024];
            // DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            // socket.receive(receivePacket);
            // String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
            // System.out.println("Received packet from server: " + receivedMessage);
    
            socket.close();
        } catch (Exception e) {
            System.out.println("Client exception " + e.getMessage());
        }
    }
    
    

   
}


