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
            Scanner myScanner;
            String remoteIP = "";
            String Latitude = "";
            String Longitude = "";
            String ServerString;
            
			String dataSuccess1;
			String dataSuccess2;
			
			while(true)
            {
                try
                {
                    // Listen for connections and accept
                    System.out.println ("Listening on port: " + ListeningSocket.getLocalPort());
                    Socket NewClientSocket = ListeningSocket.accept();
                    System.out.println ("Connection from: "+ NewClientSocket.getRemoteSocketAddress());

                    // Get the client string
                    DataInputStream in = new DataInputStream (NewClientSocket.getInputStream());
                    ServerString = in.readUTF();
                    
                    
                    //IP comes with port number
                    remoteIP = (NewClientSocket.getRemoteSocketAddress().toString()); 
                    
                    //parse the serverString to get the lat and lon
                    myScanner = new Scanner(ServerString);
                    Latitude = myScanner.next();
                    Longitude = myScanner.next();
                    myScanner.close();
                    
                    //trim the fat
                    remoteIP = remoteIP.substring(remoteIP.indexOf("/")+1, remoteIP.indexOf(":"));

                    //Make a graph of the path to remoteIP
                    InetAddress placeToTrace = InetAddress.getByName(remoteIP);
                    TRgraph graphOfIP =TRgraph.buildGraph(placeToTrace);
                    

                    //dbAccess.insertNodePosition(remoteIP,Latitude,Longitude);
                    //dbAccess.insertNetgraph(graphOfIP);
                    
					dataSuccess1 = (dbAccess.insertNodePosition(remoteIP,Latitude,Longitude))? "Coordinates for "+remoteIP+" stored successfully" : "Failed to store coordinates";
					dataSuccess2 = (dbAccess.insertNetgraph(graphOfIP))? "Graph successfully stored" : "Failed to store graph";
					
					
                     // Echo it back
                    DataOutputStream out = new DataOutputStream (NewClientSocket.getOutputStream());
                    
					out.writeUTF (dataSuccess1);
					out.writeUTF (dataSuccess2);
					System.out.println("-----------------");
					System.out.println("1. " +dataSuccess1);
					System.out.println("2. " +dataSuccess2);
					System.out.println("-----------------");
					
					
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
                /* SQLExceptions are caught in their methods.
				catch(SQLException e)
                {
                        e.printStackTrace();
                        break;
                }
				*/
            }
	}
	
    public static void main (String [] args)
    {
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		
		
			Thread t = new ServerStuff (4747);
			t.start();
		} catch(IOException e){
			e.printStackTrace();
		} catch (java.lang.ClassNotFoundException e){
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		} 
		
    }
    
}
