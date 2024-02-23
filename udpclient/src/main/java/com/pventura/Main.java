package com.pventura;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        try{
            InetAddress address = InetAddress.getLocalHost(); // Or specify the server address
            DatagramSocket socket = new DatagramSocket();

            int frameNumber = 3469;
            int frameType = 0;
            int deviceType = 20;
            long deviceId = 710000;
            int payloadLength = 121; // Adjust payload length according to the added GPS block
            int payloadCrc = 5;

            ByteBuffer buffer = ByteBuffer.allocate(256);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.putShort((short) frameNumber);
            buffer.put((byte) frameType);
            buffer.put((byte) deviceType);
            buffer.putInt((int) deviceId);
            buffer.putShort((short) payloadLength);
            buffer.putInt((int) payloadCrc);

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

            byte[] buf = buffer.array();

            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
            socket.send(packet);

            byte[] ackBuf = new byte[256];
            DatagramPacket ackPacket = new DatagramPacket(ackBuf, ackBuf.length);

            // Set the socket timeout to avoid blocking indefinitely
            socket.setSoTimeout(5000);
            socket.receive(ackPacket);

            // Parse the ACK packet
            ByteBuffer ackBuffer = ByteBuffer.wrap(ackPacket.getData());
            ackBuffer.order(ByteOrder.LITTLE_ENDIAN);

            int ackFrameNumber = ackBuffer.getShort() & 0xffff;
            int ackFrameType = ackBuffer.get() & 0xff;
            int ackDeviceType = ackBuffer.get() & 0xff;
            long ackDeviceId = ackBuffer.getInt() & 0xffffffffL;
            int ackPayloadLength = ackBuffer.getShort() & 0xffff;
            long ackPayloadCrc = ackBuffer.getInt() & 0xffffffffL;

            // Now you can use these variables
            System.out.println("ACK Frame Number: " + ackFrameNumber);
            System.out.println("ACK Frame Type: " + ackFrameType);
            System.out.println("ACK Device Type: " + ackDeviceType);
            System.out.println("ACK Device ID: " + ackDeviceId);
            System.out.println("ACK Payload Length: " + ackPayloadLength);
            System.out.println("ACK Payload CRC: " + ackPayloadCrc);

            socket.close();

        } catch(SocketTimeoutException e){
            System.out.println("The socket timed out");
        } catch(IOException e){
            System.out.println("Client exception " + e.getMessage());
        }
    }
}