package com.pventura;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // Create and start the first thread for port 4445
        Thread thread1 = new Thread(() -> sendPackets(4445));
        thread1.start();

        // Create and start the second thread for port 4446
        Thread thread2 = new Thread(() -> sendPackets(4446));
        thread2.start();

        // Wait for 5 seconds
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Interrupt both threads to stop packet sending
        thread1.interrupt();
        thread2.interrupt();
    }

    private static void sendPackets(int port) {
        try {
            InetAddress address = InetAddress.getLocalHost(); // Or specify the server address
            DatagramSocket socket = new DatagramSocket();

            long startTime = System.currentTimeMillis();
            while (!Thread.currentThread().isInterrupted() && System.currentTimeMillis() - startTime < 5000) {
                // Packet sending logic
                int frameNumber = 3469;
                int frameType = 0;
                int deviceType = 20;
                long deviceId = (port == 4445) ? 710000 : 2000;
                int payloadLength = 121; // Adjust payload length according to the added GPS block
                int payloadCrc = 5;

                // Construct packet
                byte[] buf = constructPacket(frameNumber, frameType, deviceType, deviceId, payloadLength, payloadCrc);
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);

                // Send packet
                socket.send(packet);

                // Sleep for a short duration before sending the next packet
                Thread.sleep(100);
            }

            socket.close();
        } catch (IOException e) {
            System.out.println("Client exception " + e.getMessage());
        } catch (InterruptedException e) {
            // Thread interrupted, stop sending packets
        }
    }

    private static byte[] constructPacket(int frameNumber, int frameType, int deviceType, long deviceId,
                                      int payloadLength, int payloadCrc) {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort((short) frameNumber);
        buffer.put((byte) frameType);
        buffer.put((byte) deviceType);
        buffer.putInt((int) deviceId);
        buffer.putShort((short) payloadLength);
        buffer.putInt(payloadCrc);

        // Main block
        buffer.put((byte) 0); // Block type
        buffer.putShort((short) 79); // Block length
        buffer.putInt((int) (System.currentTimeMillis() / 1000)); // Timestamp
        buffer.position(buffer.position() + 6); // Skip reserved bytes
        buffer.putShort((short) 100); // Probe 1 value
        buffer.putShort((short) 200); // Probe 2 value
        buffer.putShort((short) 300); // Probe 3 value
        buffer.putShort((short) 400); // Probe 4 value
        buffer.position(buffer.position() + 12); // Skip reserved bytes
        buffer.putShort((short) 5000); // Voltage
        boolean flag1 = false;
        boolean flag2 = true;
        boolean flag3 = false;
        boolean flag4 = true;

        // Create integer to represent flags
        int status = 0;

        // Set flags using bitwise operations
        status |= (flag1 ? 1 : 0) << 0; // Set bit 0 if flag1 is true
        status |= (flag2 ? 1 : 0) << 1; // Set bit 1 if flag2 is true
        status |= (flag3 ? 1 : 0) << 2; // Set bit 2 if flag3 is true
        status |= (flag4 ? 1 : 0) << 3; // Set bit 3 if flag4 is true
        buffer.putInt(status); // Status
        // Company VAT
        String companyVatString = "AAAAAAAAA";
        byte[] companyVatBytes = Arrays.copyOf(companyVatString.getBytes(StandardCharsets.UTF_8), 16);
        buffer.put(companyVatBytes);

        // Vehicle plate
        String vehiclePlateString = "BBBBBBBBB";
        byte[] vehiclePlateBytes = Arrays.copyOf(vehiclePlateString.getBytes(StandardCharsets.UTF_8), 16);
        buffer.put(vehiclePlateBytes);
        buffer.putInt(1000); // Hour count 
        buffer.position(buffer.position() + 4);// Skip reserved bytes

        // GPS block
        buffer.put((byte) 3); // Block type
        buffer.putShort((short) 42); // Block length
        buffer.putInt((int) (System.currentTimeMillis() / 1000)); // Timestamp
        buffer.putLong(123456789012345678L); // SIM Card ICCID
        buffer.put((byte) 0b00000001); // GPRS/GPS status
        buffer.putInt((int) (System.currentTimeMillis() / 1000)); // GPS timestamp
        buffer.putInt(123456789); // Latitude
        buffer.putInt(987654321); // Longitude
        buffer.putInt(1000); // Altitude
        buffer.putInt(50000); // Odometer
        buffer.putShort((short) 200); // GPS Speed
        buffer.putShort((short) 180); // GPS Course
        buffer.put((byte) 10); // GPS HDOP
        buffer.put((byte) 8); // GPS Satellites

        // Trim the buffer to the actual size of the packet
        buffer.flip();
        byte[] packetBytes = new byte[buffer.remaining()];
        buffer.get(packetBytes);
        return packetBytes;
    }
}


