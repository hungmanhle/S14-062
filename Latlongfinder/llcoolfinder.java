

/*---------------------------------------------------------------------------------------
 llcoolfinder.java - This application will give a Lat/Long for given IP if possible.

 Classes:			llcoolfinder.java - Main class
				
 Methods:
 getRemoteSocketAddress 	(Socket Class)
 getLocalSocketAddress  	(Socket Class)
 getInputStream		(Socket Class)
 getOutputStream		(Socket Class)
 getLocalPort		(ServerSocket Class)
 setSoTimeout		(ServerSocket Class)
 accept			(ServerSocket Class)
				

 Date:			May 12, 2014

 By:		Hung Le


 Notes:
 The program illustrates the use of the java.net package to implement a basic
 echo server.The server is multi-threaded so every new client connection is 
 handled by a separate thread.
	
 The application receives a string from an echo client and simply sends back after 
 displaying it. 

 Generate the class file and run it as follows:
 javac llcoolfinder
 java  llcoolfinder <IP Address>
 ---------------------------------------------------------------------------------------*/
//import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Llcoolfinder extends Object {

    
    private String closestNodeIP;
    private String closestNodeLat;
    private String closestNodeLon;
    private ArrayList<String> IPsToCheck;
    private int numHops;

    public Llcoolfinder(String ipAddress) {
        IPsToCheck  = new ArrayList();
        IPsToCheck.add(ipAddress);
    }

    /*
     getConnection
     */
    public Connection getConnection() throws SQLException {
        Connection conn = null;


        conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:4747/test?user=root&password=test");
        System.out.println("MySql Connection established");

        return conn;
    }

    /*
     closeConnection
     */
    public void closeConnection(Connection connArg) {
        System.out.println("Releasing all open resources ...");
        try {
            if (connArg != null) {
                connArg.close();
                connArg = null;
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public boolean findInTable1(int index) throws SQLException {

        Connection mysqlConn = getConnection();
        Statement mysqlStatement = mysqlConn.createStatement();

        ResultSet rs = mysqlStatement.executeQuery("SELECT * FROM test1 WHERE ipAddress = '" + IPsToCheck.get(index) + "';");

        // Continue to worst case
        if (!rs.isBeforeFirst()) {
            System.out.println(IPsToCheck.get(index) + " not found in T1");
            mysqlStatement.close();
            closeConnection(mysqlConn);
            return false;
        }
        // Continue with best case
        while (rs.next()) {
            //Retrieve by column name
            closestNodeIP = IPsToCheck.get(index);
            closestNodeLat = rs.getString("latCoord");
            closestNodeLon = rs.getString("longCoord");

            //Display values
            System.out.println("The closest node has IP: " + closestNodeIP 
                        + "and is located near Latitude: " + closestNodeLat 
                                         + ", Longitude: " + closestNodeLon);

        }
        // Clean-up environment
        rs.close();
        mysqlStatement.close();
        closeConnection(mysqlConn);

        return true;

    }
    
    public boolean findInTable2(int index) throws SQLException
    {
        
        Connection mysqlConn = getConnection();
        Statement mysqlStatement = mysqlConn.createStatement();
        String queryString1 = "SELECT nodeA FROM nodeList WHERE nodeB = '" + IPsToCheck.get(index) + "';";
        String queryString2 = "SELECT nodeB FROM nodeList WHERE nodeA = '" + IPsToCheck.get(index) + "';";
        
        
        ResultSet rs = mysqlStatement.executeQuery(queryString1);
        // Continue to worst case
        if (!rs.isBeforeFirst()) {
            System.out.println(IPsToCheck.get(index) + " not found in T2");
            mysqlStatement.close();
            closeConnection(mysqlConn);
            return false;
        }
        
        while(rs.next())
        {
            if(IPsToCheck.indexOf(rs.getString("nodeA"))== -1)
            {
                IPsToCheck.add(rs.getString("nodeA"));
            }
        }
        rs =  mysqlStatement.executeQuery(queryString2);
        while(rs.next())
        {
            if(IPsToCheck.indexOf(rs.getString("nodeB"))== -1)
            {
                IPsToCheck.add(rs.getString("nodeB"));
            }
        }
//        for(String ip : IPsToCheck)
//        {
//            if(findInTable1(ip))
//            {
//                return true;
//            }
//        }
        
        return true;
    }
    
    public boolean findClosestNode() throws SQLException
    {
        int index = 0;
        while(index < IPsToCheck.size()){
            
            if(findInTable1(index))
            {
                return true;
            }
            if(!findInTable2(index))
            {
                
            }
            index++;
        }
        return false;
    }
    
  

    public boolean createIPList() {
        return true;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage error: missing input");
            System.exit(0);
        }
        Llcoolfinder myObject = new Llcoolfinder(args[0]);


        /*
         Best Case:
			
         Find in Table1
         */
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            if (!myObject.findClosestNode()) {
                System.out.println("The IP cannot be found.");
            } else {
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Llcoolfinder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Llcoolfinder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Llcoolfinder.class.getName()).log(Level.SEVERE, null, ex);
        }



    }
}
