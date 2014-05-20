
package traceroutetest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


/*---------------------------------------------------------------------------------------
 DBStuff.java - This Class handles SQL Connections and Transactions.

 Classes:			DBStuff.java
				
				

 Date:			May 12, 2014

 By:		    Hung Le


 Notes:
 This class is not to be run alone. It is to be used within other applications.

 Generate the class file and run it as follows:
 javac DBStuff.java
	
 ---------------------------------------------------------------------------------------*/
public class DBStuff {

	// Replace this string with a string leading to database.
    public static final String URL = "jdbc:mysql://localhost:3306/predicative?user=hank&password=poop1234";
    private Connection connection;
    
    public DBStuff() throws SQLException 
    {
        connection = null;
    }
    
	// Open SQL Connection
    public void openConnection() throws SQLException 
    {
        connection = DriverManager.getConnection(URL);
		System.out.println("MySql Connection established"); 
    }

	// Close SQL Connection
    public void closeConnection() 
    {
        System.out.println("Releasing all open resources ...");
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

	/*
		insertNodePosition.
		This method will insert a node given by the android application "gateway locator"
		
		It inserts this data into Table 1. AKA the GPS coordinates Table.
	*/
    public boolean insertNodePosition(String IP, String Latitude, String Longitude) 
    {
		try{
			openConnection();
			Statement mysqlStatement = connection.createStatement();

			connection.setAutoCommit(false);
				
			// Change insert statement to reflect database structure.
			mysqlStatement.executeUpdate("INSERT INTO test1 (ipAddress, latCoord, longCoord) VALUES ('" + IP + "','" + Latitude + "','" + Longitude + "')");
			connection.commit();

			connection.setAutoCommit(true);
			closeConnection();
			return true;
		} catch (SQLException e){
			System.out.println("SQLException: "+e);
			//e.printStackTrace();
			return false;
		}
		
    }
    
	/*
		insertNodeEdge.
		
		This method will insert a pair of nodes (an edge) into Table 2, the nodelist.
		This also stores average hopTime between nodes, however this information is not
		currently in use.
	*/
	public boolean insertNodeEdge(String node1, String node2, int hopTime){
		try{
			openConnection();
			Statement mysqlStatement = connection.createStatement();
			connection.setAutoCommit(false);
			
			// Change this to reflect database structure.
			mysqlStatement.executeUpdate("INSERT INTO nodelist (nodeA, nodeB, hopTime) VALUES ('" + node1 + "','" + node2 + "','" + hopTime + "')");
			connection.commit();
			connection.setAutoCommit(true);
			closeConnection();
			return true;
		} catch (SQLException e){
			System.out.println("SQLException: " + e);
			return false;
		}
	}
	
	/*
		insertNetgraph.
		
		This method inserts a whole TRgraph of nodes.
	*/
    public boolean insertNetgraph(TRgraph graph)
    {
		for (int i = 0; i < (graph.getNodeCount()-1); i++)
		{
			String node1   = graph.getEdge(i).getNode1();
			String node2   = graph.getEdge(i).getNode2();
			int    hopTime = graph.getEdge(i).getHopTime();
			
			// insertNodeEdge for all the edges.
			if(!insertNodeEdge(node1,node2,hopTime)){
				//System.out.println("A row with PK {"+node1+" , "+node2+"} already existed...");
			}
			
		}
		return true;
    }
    
    
    
}
