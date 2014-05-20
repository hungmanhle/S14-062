
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

/*---------------------------------------------------------------------------------------
 ServerStuff.java - This application acts as a server. It recieves requests and adds
					IPs and their GPS locations to the first table of our database.
					It then runs a tracert and adds the two nodes of every edge to our
					second table.

 Classes:			ServerStuff.java
					DBStuff.java
					TRgraph.java
				
				

 Date:			May 12, 2014

 By:			Hung Le


 Notes:
 

 Generate the class file and run it as follows:
 javac ServerStuff.java
	Note: I reccommend compiling everything at once with javac *.java
 cd ..
 java  -cp .;traceroutetest/mysql-connector-java-5.1.29-bin.jar 
		traceroutetest/ServerStuff
 ---------------------------------------------------------------------------------------*/
public class ServerStuff extends Thread{
    
	// dbAccess handles database access
    private DBStuff dbAccess;

    private Socket connectedSocket;
	
    public ServerStuff(Socket sock) throws IOException, SQLException{
        connectedSocket = sock;
        dbAccess = new DBStuff();
    }
        
	/*
		This is the threadproc.
		
		It occurs when a connection is received...
		The input is taken in, delimited and then added to the first table.
		
		Afterwards a TRgraph is created and adds all the nodes to the second table.
		
	*/
    public void run()
	{           
            Scanner myScanner;
            String remoteIP = "";
            String Latitude = "";
            String Longitude = "";
            String ServerString;
            
			String dataSuccess1;
			String dataSuccess2;
			
            try
            {
                // Get the client string
				System.out.println("Received Connection... " );
                DataInputStream in = new DataInputStream (connectedSocket.getInputStream());
                ServerString = in.readUTF();
                
                //IP comes with port number
                remoteIP = (connectedSocket.getRemoteSocketAddress().toString()); 
                
                //parse the serverString to get the lat and lon
                myScanner = new Scanner(ServerString);
                Latitude = myScanner.next();
                Longitude = myScanner.next();
                myScanner.close();
                
                //trim the fat
                remoteIP = remoteIP.substring(remoteIP.indexOf("/")+1, remoteIP.indexOf(":"));

                //dbAccess.insertNodePosition(remoteIP,Latitude,Longitude);
                //dbAccess.insertNetgraph(graphOfIP);
                
                dataSuccess1 = (dbAccess.insertNodePosition(remoteIP,Latitude,Longitude))? "Coordinates for "+remoteIP+" stored successfully" : "Failed to store coordinates";
                
                 // Echo it back
                DataOutputStream out = new DataOutputStream (connectedSocket.getOutputStream());
                
                out.writeUTF (dataSuccess1);
                
                System.out.println("-----------------");
                System.out.println("1. " +dataSuccess1);
                System.out.println("-----------------");
                
                connectedSocket.close();     
                
                
                //Make a graph of the path to remoteIP
                InetAddress placeToTrace = InetAddress.getByName(remoteIP);
                TRgraph graphOfIP =TRgraph.buildGraph(placeToTrace);
                
                dataSuccess2 = (dbAccess.insertNetgraph(graphOfIP))? "Graph successfully stored" : "Failed to store graph";

            }
            catch (SocketTimeoutException s)
            {
                    System.out.println ("Socket timed out!");
                    return;
            }
            catch(IOException e)
            {
                    e.printStackTrace();
                    return;
            }
            /* SQLExceptions are caught in their methods.
            catch(SQLException e)
            {
                    e.printStackTrace();
                    break;
            }   
            */
	}
	
	/*
		The Main Thread.
		
		Server waits for a connection.
	*/
    public static void main(String [] args) { 

		ServerSocket sock = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			sock = new ServerSocket(4747);
		} catch(IOException e) {
			System.out.println("Error with port");
			e.printStackTrace();
			System.exit(1);
		} catch (java.lang.ClassNotFoundException e){
			e.printStackTrace();
			System.exit(2);
		} catch (Exception e) {
		   e.printStackTrace();
			System.exit(3);
		}
		
		
		//switch condition
		while(true) {
			try { 
				
				Socket tmp = sock.accept();
				
				new ServerStuff(tmp).start();
				//System.out.println ("Connection from: "+ tmp.getRemoteSocketAddress());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		//TODO cleanup
	}

    
}
