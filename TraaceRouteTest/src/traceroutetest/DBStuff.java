/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package traceroutetest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 *
 * @author a00826347
 */
public class DBStuff {

    public static final String URL = "jdbc:mysql://localhost:3306/predicative?user=hank&password=password";
    private Connection connection;
    
    public DBStuff() throws SQLException {
        connection = openConnection();
    }
    
    public Connection openConnection() throws SQLException {
//		Properties connProps = new Properties();
//		
//		connProps.put("username", "root");
//		connProps.put("password", "poop");

        Connection c;
        try {
            c = DriverManager.getConnection(URL);
            System.out.println("MySql Connection established"); 
            return c;
        } catch(SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void closeConnection() {
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

    public void insertTable1(String IP, String Latitude, String Longitude) throws SQLException {

        openConnection();
        Statement mysqlStatement = connection.createStatement();

        connection.setAutoCommit(false);

        mysqlStatement.executeUpdate("INSERT INTO test1 (ipAddress, latCoord, longCoord) VALUES ('" + IP + "','" + Latitude + "','" + Longitude + "')");
        connection.commit();

        connection.setAutoCommit(true);
        closeConnection();

    }
    
    public void insertTable2(String node1, String node2, int hopTime) throws SQLException {

        openConnection();
        Statement mysqlStatement = connection.createStatement();

        connection.setAutoCommit(false);
        //assumed table structure
        mysqlStatement.executeUpdate("INSERT INTO test2 (node1, node2, hopTime) VALUES ('" + node1 + "','" + node2 + "','" + hopTime + "')");
        connection.commit();

        connection.setAutoCommit(true);
        closeConnection();

    }
    
    
    
}
