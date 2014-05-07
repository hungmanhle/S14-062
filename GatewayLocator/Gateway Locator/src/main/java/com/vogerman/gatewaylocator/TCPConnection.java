package com.vogerman.gatewaylocator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by German on 5/1/14.
 */
public class TCPConnection {
    private Socket socket;
    private int port;
    private String ipAddress;
    private String packet;
    private static final int TIMEOUT = 1000;
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

    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Verifies IP and port are valid and creates a connection.
     * @param portNum server port to connect to.
     * @param ipAddr  server ip address to connect to.
     * @return new connection if successful; null otherwise.
     */
    public static TCPConnection create(int portNum, String ipAddr) throws IOException {
        Socket sock = new Socket();
        try{
            sock.connect(new InetSocketAddress(ipAddr, portNum), TIMEOUT);
        } catch (IOException e) {
            throw e;
        }

        return new TCPConnection(portNum, ipAddr, sock);
    }

    public String receive() throws IOException {
        String message = "";
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            message = in.readUTF();
        } catch (IOException e) {
            throw e;
        }
        return message;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    /**
     * Writes the value to the packet.
     * @param value String to append to the packet.
     */
    public void writePacket(String value)
    {
        packet = value;
    }

    public boolean sendPacket(String packet) throws IOException {
        this.packet = packet;
        return sendPacket();
    }

    /**
     * Sends the packet currently in the buffer.
     * @return true if sent successfully; false otherwise.
     */
    public boolean sendPacket() throws IOException {
        try {
            OutputStream os = socket.getOutputStream();
            DataOutputStream out = new DataOutputStream(os);
            out.writeUTF(packet);
        } catch (IOException e) {
            throw e;
        }
        return true;
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
