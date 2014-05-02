package com.vogerman.gatewaylocator;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by German on 5/1/14.
 */
public class TCPConnection {
    private Socket socket;
    private int port;
    private String ipAddress;
    //private Socket socket;
    private String packet;

    /**
     * Basic constructor.
     * @param portNum server's port number
     * @param ipAddr server's ip address
     * @param sock socket already created
     */
    private TCPConnection(int portNum, String ipAddr, Socket sock)
    {
        ipAddress = ipAddr;
        port = portNum;
        socket = sock;
    }

    /**
     * Verifies IP and port are valid and creates a connection.
     * @param portNum server port to connect to.
     * @param ipAddr  server ip address to connect to.
     * @return new connection if successful; null otherwise.
     */
    public static TCPConnection create(int portNum, String ipAddr) throws IOException {
        // Add stricter verification if needed..
        Socket sock;
        try{
            sock = new Socket(ipAddr, portNum);
        } catch (IOException e) {
            throw e;
        //} catch (NetworkOnMainThreadException e) {
        //    return null;
        }

        return new TCPConnection(portNum, ipAddr, sock);
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Appends a space and followed by the value to the packet.
     * @param value String to append to the packet.
     */
    public void appendToPacket(String value)
    {
        packet+= " " + value;
    }

    /**
     * Sends the packet currently in the buffer.
     * @return true if sent successfully; false otherwise.
     */
    public void sendPacket() throws IOException {
        try {
            OutputStream os = socket.getOutputStream();
            DataOutputStream out = new DataOutputStream(os);
            out.writeUTF(packet);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Accessor for the packet in buffer.
     * @return String value of the packet.
     */
    public String getPacket()
    {
        return packet;
    }
}
