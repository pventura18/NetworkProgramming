package com.teltonika;

import java.io.OutputStream;
import java.net.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Main {
    public static void main(String[] args) {
        String serverHost = "192.168.0.116"; // Change this to your server's hostname or IP address
        int serverPort = 5027; // Change this to your server's port

        String imei = "868324023580306";

        try {
            sendTcpPacket(serverHost, serverPort, imei);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sendTcpPacket(String serverHost, int serverPort, String imei) throws InterruptedException {
        try {
            // Connect to the server
            Socket socket = new Socket(serverHost, serverPort);

            // Get the output stream of the socket
            OutputStream outputStream = socket.getOutputStream();

            // Convert the IMEI string to bytes
            byte[] imeiBytes = imei.getBytes();

            // Get the length of the IMEI string
            int imeiLength = imeiBytes.length;

            // Create a byte array to hold the message (length + IMEI bytes)
            byte[] message = new byte[imeiLength + 2];

            // Set the first two bytes of the message to represent the length of the IMEI string
            message[0] = (byte) ((imeiLength >> 8) & 0xFF);
            message[1] = (byte) (imeiLength & 0xFF);

            // Copy the IMEI bytes to the message
            System.arraycopy(imeiBytes, 0, message, 2, imeiLength);

            // Send the message to the server
            outputStream.write(message);

            //Receive response
            byte[] response = new byte[1024];
            int bytesRead = socket.getInputStream().read(response);
            System.out.println("Response: " + new String(response, 0, bytesRead));

            String msg = "00000000000000728e010000018b23dd796300fbf7263c24f9e11a0000000000000002240001000000000000000000010224004501210001e50110cde39f7e42bb55aa788e4a29ed650055020ab70a8f264c6000ffff6b210001b00110f89b907e42bb55aaa3463b29ed650055020ab708bb2600ae0500096c01000051d4";

            outputStream.write(binary(msg).array());

            while(true) {
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ByteBuf binary(String... data) {
        return Unpooled.wrappedBuffer(DataConverter.parseHex(concatenateStrings(data)));
    }

    private static String concatenateStrings(String... strings) {
        StringBuilder builder = new StringBuilder();
        for (String s : strings) {
            builder.append(s);
        }
        return builder.toString();
    }

    private static String prepareData(String imei) {
        // Convert IMEI length to hexadecimal representation
        String imeiLengthHex = String.format("%04X", imei.length());

        // Convert IMEI to hexadecimal representation
        StringBuilder imeiHex = new StringBuilder();
        for (int i = 0; i < imei.length(); i++) {
            imeiHex.append(String.format("%02X", Character.digit(imei.charAt(i), 10)));
        }

        // Concatenate length and IMEI in hexadecimal
        String message = imeiLengthHex + imeiHex.toString();
        System.out.println("Message: " + message);
        return message;
    }

    private static void sendPackets(int port) {
        try {
            InetAddress address = InetAddress.getLocalHost(); // or specify the server address
            DatagramSocket socket = new DatagramSocket();
    
            String packetString = "343456565698459023904098590485958945484237845723847893748923747213213213214342342545345345";
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


