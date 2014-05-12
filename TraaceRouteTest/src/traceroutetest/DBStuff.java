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

    public static final String URL = "jdbc:mysql://localhost:3306/predicative?user=hank&password=password";
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

    public void insertNodePosition(String IP, String Latitude, String Longitude) throws SQLException 
    {

        openConnection();
        Statement mysqlStatement = connection.createStatement();

        connection.setAutoCommit(false);
            
        //HUNG CHANGE NAMES HERRE
        mysqlStatement.executeUpdate("INSERT INTO test1 (ipAddress, latCoord, longCoord) VALUES ('" + IP + "','" + Latitude + "','" + Longitude + "')");
        connection.commit();

        connection.setAutoCommit(true);
        closeConnection();

    }
    
    public void insertNetgraph(TRgraph graph) throws SQLException 
    {
        openConnection();
        Statement mysqlStatement = connection.createStatement();
        
        connection.setAutoCommit(false);
        //-1 so as not to go out of range
        for (int i = 0; i < (graph.getNodeCount()-1); i++)
        {
            String node1   = graph.getEdge(i).getNode1();
            String node2   = graph.getEdge(i).getNode2();
            int    hopTime = graph.getEdge(i).getHopTime();
            //HUNG CHANGE NAMES HERRE
            mysqlStatement.executeUpdate("INSERT INTO test2 (node1, node2, hopTime) VALUES ('" + node1 + "','" + node2 + "','" + hopTime + "')");
            connection.commit();
        }
        connection.setAutoCommit(true);
        closeConnection();

    }
    
    
    
}
