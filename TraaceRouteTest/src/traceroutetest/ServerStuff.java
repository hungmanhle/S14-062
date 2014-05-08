/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package traceroutetest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author a00826347
 */
public class ServerStuff extends Thread{
    
    String ServerString;
    private ServerSocket ListeningSocket;
    private DBStuff dbAccess;

	
        
    public ServerStuff (int port) throws IOException, SQLException
    {
		ListeningSocket = new ServerSocket(port);
                dbAccess = new DBStuff();
		//ListeningSocket.setSoTimeout(20000); // set a 20 second timeout
    }
    
    public void run()
	{              
            while(true)
            {
                try
                {
                    Scanner myScanner;
                    String remoteIP = "";
                    String Latitude = "";
                    String Longitude = "";


                    // Listen for connections and accept
                    System.out.println ("Listening on port: " + ListeningSocket.getLocalPort());
                    Socket NewClientSocket = ListeningSocket.accept();
                    System.out.println ("Connection from: "+ NewClientSocket.getRemoteSocketAddress());

                    // Get the client string
                    DataInputStream in = new DataInputStream (NewClientSocket.getInputStream());
                    ServerString = in.readUTF();
                    


                    //parse the serverString to get the lat and lon
                    myScanner = new Scanner(ServerString);
                    Latitude = myScanner.next();
                    Longitude = myScanner.next();
                    myScanner.close();

                    //IP comes with port number
                    remoteIP = (NewClientSocket.getRemoteSocketAddress().toString());
                    //trim the fat
                    remoteIP = remoteIP.substring(remoteIP.indexOf("/")+1, remoteIP.indexOf(":"));

                    //Make a graph of the path to remoteIP
                    InetAddress placeToTrace = InetAddress.getByName(remoteIP);
                    TRgraph graphOfIP = new TRgraph(placeToTrace);

                    dbAccess.insertNodePosition(remoteIP,Latitude,Longitude);
                    dbAccess.insertNetgraph(graphOfIP);
                    
                    // Echo it back
                    DataOutputStream out = new DataOutputStream (NewClientSocket.getOutputStream());
                    out.writeUTF ("was successful");
                    NewClientSocket.close();
                }
                catch (SocketTimeoutException s)
                {
                        System.out.println ("Socket timed out!");
                        break;
                }
                catch(IOException e)
                {
                        e.printStackTrace();
                        break;
                }
                catch(SQLException e)
                {
                        e.printStackTrace();
                        break;
                }
            }
	}
    
}
