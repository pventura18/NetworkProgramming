package com.ruptela;

import java.io.OutputStream;
import java.net.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


public class Main {
    public static void main(String[] args) {
        String serverHost = "81.47.170.140"; // Change this to your server's hostname or IP address
        int serverPort = 5046; // Change this to your server's port


        try {
            sendTcpPacket(serverHost, serverPort);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sendTcpPacket(String serverHost, int serverPort) throws InterruptedException {
        try {
            // Connect to the server
            Socket socket = new Socket(serverHost, serverPort);

            // Get the output stream of the socket
            OutputStream outputStream = socket.getOutputStream();

            String msg = "01a4000315bc70f9b69244000458068f4a0030000d11398a1c0c19fd056524040b000c0a00090c0005010031f40032fd0033f200ce47002400002500001c010199000195010196010086000900aa0000001e0ff000d3ffff0043ffff01930000019200000194000002220000022300000200300000000200af000e872401008e000000000000000058068f4a0031000d11398a1c0c19fd056524040b000c0a00090400870000880000a90000820010008b0002021e0000021f0000021d0000021c0000022400000225000000890000008505f00220000002210000008300000084000002260000022700000228000003008a00000000008d00000000008c000000000058068f4a0032000d11398a1c0c19fd056524040b000c0a000905019f01005800001b1f00ad0000cfb10b02290000022a0000022b0000022c0000022d00000012000000130000001d367400c52f8000740055023e0502060097000000000096000058520041007746cb00d0000003f1005c0007c21b0072001864880058068f4a0033000d11398a1c0c19fd056524040b000c0a000900000001008e0000000000000000e815";

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


