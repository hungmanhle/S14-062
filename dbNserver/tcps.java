
/*---------------------------------------------------------------------------------------
	tcps.java - Our Server/DB

	Classes:			tcps - public class
				ServerSocket - java.net
				Socket	     - java.net
				
	Methods:
				getRemoteSocketAddress 	(Socket Class)
				getLocalSocketAddress  	(Socket Class)
				getInputStream		(Socket Class)
				getOutputStream		(Socket Class)
				getLocalPort		(ServerSocket Class)
				setSoTimeout		(ServerSocket Class)
				accept			(ServerSocket Class)
				

	Date:			February 8, 2014

	By:		Hung Le


	Notes:
	The program illustrates the use of the java.net package to implement a basic
 	echo server.The server is multi-threaded so every new client connection is 
	handled by a separate thread.
	
	The application receives a string from an echo client and simply sends back after 
	displaying it. 

	Generate the class file and run it as follows:
			javac tcps
			java tcps 
---------------------------------------------------------------------------------------*/

import java.net.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

public class tcps extends Thread
{
    String ServerString;
    private ServerSocket ListeningSocket;
	Connection mysqlConn;
	Scanner myScanner;

    public tcps (int port) throws IOException
    {
		ListeningSocket = new ServerSocket(port);
		//ListeningSocket.setSoTimeout(20000); // set a 20 second timeout
    }
	
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		Properties connProps = new Properties();
		
		connProps.put("username", "root");
		connProps.put("password", "poop");
		
		conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/predicative?user=hank&password=password");
		System.out.println("MySql Connection established");
		
		return conn;
	}
	
	public void closeConnection(Connection connArg) {
		System.out.println("Releasing all open resources ...");
		try 
		{
			if (connArg != null) 
			{
				connArg.close();
				connArg = null;
			}
		} 
		catch (SQLException sqle) 
		{
			sqle.printStackTrace();
		}
	}
	
	public void insertNewRow(String IP, String Latitude, String Longitude) throws SQLException{
		
			Connection mysqlConn = getConnection();
			Statement mysqlStatement = mysqlConn.createStatement();
			
			mysqlConn.setAutoCommit(false);
			
			mysqlStatement.executeUpdate("INSERT INTO test1 (ipAddress, latCoord, longCoord) VALUES ('"+IP+"','"+Latitude+"','"+Longitude+"')");
			mysqlConn.commit();
			
			mysqlConn.setAutoCommit(true);
			closeConnection(mysqlConn);

	}

	public void run()
	{
		while(true)
		{
			try
			{
				String remoteIP = "";
				String Latitude = "";
				String Longitude = "";
				
				/**    THIS SHIT OLD
				// Set up mysql Connection
				mysqlConn = getConnection();

				Statement mysqlStatement = mysqlConn.createStatement();
				mysqlConn.setAutoCommit(false);
				
				mysqlStatement.executeUpdate("INSERT INTO test1 (ipAddress,latCoord,longCoord) VALUES ('test','test','test')");
				mysqlConn.commit();
				
				mysqlConn.setAutoCommit(true);
				String query = "Select * FROM test1";
				mysqlStatement = mysqlConn.getConnection();
				
				ResultSet mysqlRs = mysqlStatement.executeQuery(query);
				
				while(mysqlRs.next()){
					System.out.println("ID: "+mysqlRs.getInt("id"));
					System.out.println("IP: "+mysqlRs.getString("ipAddress"));
					System.out.println("LAT: " + mysqlRs.getString("latCoord"));
					System.out.println("LONG: " + mysqlRs.getString("longCoord"));
				}
				mysqlStatement.close();
				// Closing mysql Connection
				closeConnection(mysqlConn);
			
				*/
			
			
			
				// Listen for connections and accept
				System.out.println ("Listening on port: " + ListeningSocket.getLocalPort());
				Socket NewClientSocket = ListeningSocket.accept();
				System.out.println ("Connection from: "+ NewClientSocket.getRemoteSocketAddress());

				// Get the client string
				DataInputStream in = new DataInputStream (NewClientSocket.getInputStream());
				System.out.println (ServerString = in.readUTF());
				System.out.println("THE REMOTE ADDRESS IS: " + NewClientSocket.getRemoteSocketAddress());

				

				myScanner = new Scanner(ServerString);
				Latitude = myScanner.next();
				Longitude = myScanner.next();
				myScanner.close();
				
				remoteIP = (NewClientSocket.getRemoteSocketAddress().toString());
				remoteIP = remoteIP.substring(remoteIP.indexOf("/")+1, remoteIP.indexOf(":"));

				
				
				insertNewRow(remoteIP,Latitude,Longitude);
				
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


    public static void main (String [] args)
    {
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		
		
			Thread t = new tcps (4747);
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