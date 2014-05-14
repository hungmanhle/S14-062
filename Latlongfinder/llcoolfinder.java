package llcoolfinder;


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

public class llcoolfinder extends Object {

    private String unknownIP;
    private String closestNodeIP;
    private String closestNodeLat;
    private String closestNodeLon;
    private ArrayList<String> IPsToCheck;

    public llcoolfinder(String ipAddress) {
        unknownIP = ipAddress;
    }

    /*
     getConnection
     */
    public Connection getConnection() throws SQLException {
        Connection conn = null;


        conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/predicative?user=hank&password=poop1234");
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

    public boolean findInTable1(String unknownIP) throws SQLException {

        Connection mysqlConn = getConnection();
        Statement mysqlStatement = mysqlConn.createStatement();

        ResultSet rs = mysqlStatement.executeQuery("SELECT * FROM test1 WHERE ipAddress = '" + unknownIP + "';");

        // Continue to worst case
        if (!rs.isBeforeFirst()) {
            System.out.println(unknownIP + " not found in T1");
            mysqlStatement.close();
            closeConnection(mysqlConn);
            return false;
        }
        // Continue with best case
        while (rs.next()) {
            //Retrieve by column name
            closestNodeIP = unknownIP;
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
    
    public boolean findInTable2(String unknownIP) throws SQLException
    {
        
        Connection mysqlConn = getConnection();
        Statement mysqlStatement = mysqlConn.createStatement();
        String queryString1 = "SELECT node1 FROM test2 WHERE node2 = '" + unknownIP + "';";
        String queryString2 = "SELECT node2 FROM test2 WHERE node1 = '" + unknownIP + "';";
        
        
        ResultSet rs = mysqlStatement.executeQuery(queryString1);
        // Continue to worst case
        if (!rs.isBeforeFirst()) {
            System.out.println(unknownIP + " not found in T1");
            mysqlStatement.close();
            closeConnection(mysqlConn);
            return false;
        }
        
        while(rs.next())
        {
            IPsToCheck.add(rs.getString("node1"));
        }
        rs =  mysqlStatement.executeQuery(queryString2);
        while(rs.next())
        {
            IPsToCheck.add(rs.getString("node2"));
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
        if(findInTable1(unknownIP))
        {
            return true;
        }
        if(findInTable2(unknownIP))
        {
            for (int i = 0; i < 3; i++)
            {
                for (int j = 0; j < IPsToCheck.size(); j++)
                {
                    findInTable1(IPsToCheck.get(j));
                    
                }
            }
        }
        return true;
    }
    
    public String getUnknownIP() {
        return unknownIP;
    }

    public boolean createIPList() {
        return true;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage error: missing input");
            System.exit(0);
        }
        llcoolfinder myObject = new llcoolfinder(args[0]);


        /*
         Best Case:
			
         Find in Table1
         */
        try {

            if (myObject.findInTable1(myObject.getUnknownIP())) {
                System.out.println("The IP was already known.");
            } else {
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }



    }
}
