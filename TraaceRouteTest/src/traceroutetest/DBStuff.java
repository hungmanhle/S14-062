/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package traceroutetest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


/**
 *
 * @author a00826347
 */
public class DBStuff {

    public static final String URL = "jdbc:mysql://localhost:3306/predicative?user=hank&password=poop1234";
    private Connection connection;
    
    public DBStuff() throws SQLException 
    {
        connection = null;
    }
    
    public void openConnection() throws SQLException 
    {
       
       
            connection = DriverManager.getConnection(URL);
            System.out.println("MySql Connection established"); 
            
    }

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

    public boolean insertNodePosition(String IP, String Latitude, String Longitude) 
    {
		try{
			openConnection();
			Statement mysqlStatement = connection.createStatement();

			connection.setAutoCommit(false);
				
			//HUNG CHANGE NAMES HERRE
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
    
	public boolean insertNodeEdge(String node1, String node2, int hopTime){
		try{
			openConnection();
			Statement mysqlStatement = connection.createStatement();
			connection.setAutoCommit(false);
			
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
	
	
    public boolean insertNetgraph(TRgraph graph)
    {
		for (int i = 0; i < (graph.getNodeCount()-1); i++)
		{
			String node1   = graph.getEdge(i).getNode1();
			String node2   = graph.getEdge(i).getNode2();
			int    hopTime = graph.getEdge(i).getHopTime();
			//HUNG CHANGE NAMES HERRE
			if(!insertNodeEdge(node1,node2,hopTime)){
				//System.out.println("A row with PK {"+node1+" , "+node2+"} already existed...");
			}
			
		}
		return true;
    }
    
    
    
}
