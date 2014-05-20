package traceroutetest;


/*---------------------------------------------------------------------------------------
 llcoolfinder.java - This application will give a Lat/Long for given IP if possible.

 Classes:			llcoolfinder.java - Main class
				
				

 Date:			May 12, 2014

 By:		Hung Le


 Notes:
 This application attempts to return GPS coordinates of the closest node to a given IP
 address.

 Generate the class file and run it as follows:
 javac llcoolfinder
 java  llcoolfinder <IP Address>
 ---------------------------------------------------------------------------------------*/

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Program to find the location of a given IP address
 * @author William
 */
public class Llcoolfinder extends Object {

    // these will be printed once the closest node is located
    private String closestNodeIP;
    private String closestNodeLat;
    private String closestNodeLon;
    // the list of IPs which need to be looked up
    // starts with just the original IP axpands as the search progresses
    private ArrayList<String> IPsToCheck;
    
	
	private DBStuff dbAccess;

    /**
     *  Constructor
     * @param ipAddress the IP address to be located
     */
    public Llcoolfinder(String ipAddress) {
        IPsToCheck  = new ArrayList();
        IPsToCheck.add(ipAddress);
		
    }

    
    /**
     * Opens a connection to the MySQL DB
     * @return the SQLConnection
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        Connection conn = null;


        conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/predicative?user=hank&password=poop1234");
        System.out.println("MySql Connection established");

        return conn;
    }

    
    /**
     * Closes connection tothe MySQL DB
     * @param connArg
     */
    public void closeConnection(Connection connArg) {
       // System.out.println("Releasing all open resources ...");
        try {
            if (connArg != null) {
                connArg.close();
                connArg = null;
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * Looks for the IP in table1
     * @param index the index of the IP in IPsToCheck
     * @return true if found false if not
     * @throws SQLException
     */
    public boolean findInTable1(int index) throws SQLException {

        Connection mysqlConn = getConnection();
        Statement mysqlStatement = mysqlConn.createStatement();

        ResultSet rs = mysqlStatement.executeQuery("SELECT * FROM test1 WHERE ipAddress = '" + IPsToCheck.get(index) + "';");

        // Continue to worst case: IP is not found
        if (!rs.isBeforeFirst()) {
            System.out.println(IPsToCheck.get(index) + " not found in T1");
            mysqlStatement.close();
            closeConnection(mysqlConn);
            return false;
        }
        // Continue with best case: IP is found
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
    
    /**
     *  Looks for the IP in table 2
     * @param index the index in IPsToCheck
     * @return true if found false if not
     * @throws SQLException
     * @throws UnknownHostException
     */
    public boolean findInTable2(int index) throws SQLException, UnknownHostException
    {
        
        Connection mysqlConn = getConnection();
        Statement mysqlStatement = mysqlConn.createStatement();
        //find all the the instances in which the IP being serched for is nodeB
        String queryString1 = "SELECT nodeA FROM nodeList WHERE nodeB = '" + IPsToCheck.get(index) + "';";
        //find all the the instances in which the IP being serched for is nodeA
        String queryString2 = "SELECT nodeB FROM nodeList WHERE nodeA = '" + IPsToCheck.get(index) + "';";
        
        
        ResultSet rs = mysqlStatement.executeQuery(queryString1);
        // Continue to worst case: IP is not found
        if (!rs.isBeforeFirst()) {
            System.out.println(IPsToCheck.get(index) + " not found in T2, running Traceroute");
            //run traceroute and store in table 2
            InetAddress placeToTrace = InetAddress.getByName(IPsToCheck.get(index));
            TRgraph graphOfIP =TRgraph.buildGraph(placeToTrace);
            DBStuff dbAccess = new  DBStuff();
            if(dbAccess.insertNetgraph(graphOfIP))
            {
                System.out.println("Traceroute complete");
            }
            else 
            {
                System.out.println("Traceroute failed");
            }
            mysqlStatement.close();
            closeConnection(mysqlConn);
            return false;
        }
        // Continue to best case: IP is found 
        // add all adjacent nodes to the IPsToCheck
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
//        
        return true;
    }
    
    /**
     * Finds the location and IP of the closest known node to the given IP
     * @return true if a node was found false if not
     * @throws SQLException
     * @throws UnknownHostException
     */
    public boolean findClosestNode() throws SQLException, UnknownHostException
    {
        int index = 0;
        // Search in table1 then table2 for each IP in IPsToCheck
        // this is a BFS
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
    
  

   

    /**
     * Main access pint to the program
     * @param args the IP that you would like to locate
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage error: missing input");
            System.exit(0);
        }
        Llcoolfinder myObject = new Llcoolfinder(args[0]);


        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            if (!myObject.findClosestNode()) {
                System.out.println("The IP cannot be found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Llcoolfinder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Llcoolfinder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Llcoolfinder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Llcoolfinder.class.getName()).log(Level.SEVERE, null, ex);
        }



    }
}
