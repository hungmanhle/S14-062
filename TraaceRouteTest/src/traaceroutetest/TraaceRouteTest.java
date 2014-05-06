/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package traaceroutetest;

import java.io.IOException;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author a00826347
 */
public class TraaceRouteTest {

    /**
     * @param args the command line arguments
     */
    
    private final static String os = System.getProperty("os.name");
    
    public static void main(String[] args) {
        /*String result = "";
        try {
            InetAddress placeToTrace = InetAddress.getByName(args[0]);
            
            result = traceRoute(placeToTrace);
            System.out.println(result);
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(TraaceRouteTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
        }
        * */
        TRNode testNode = new TRNode("1     2 ms     2 ms     4 ms  142.232.246.62");
        
        System.out.println("IP: " + testNode.getIpAddr() + "\nAvgHop: " + testNode.getAvgHopTime());
    }
    
    public static String traceRoute(InetAddress address){
        String route = "";
        
        StringWriter writer = new StringWriter();
                    // IOUtils.copy(inputStream, writer, encoding);
        
        try {
            Process traceRt;
            //if(os.contains("win")) traceRt = Runtime.getRuntime().exec("tracert " + address.getHostAddress());
            //else 
            traceRt = Runtime.getRuntime().exec("tracert -d -4 " + address.getHostAddress());

            // read the output from the command
            IOUtils.copy(traceRt.getInputStream(), writer, "UTF-8");
            route = writer.toString();
                        // route = convertStreamToString(traceRt.getInputStream());

            // read any errors from the attempted command
            IOUtils.copy(traceRt.getErrorStream(), writer, "UTF-8");
            String errors = writer.toString();
                        //String errors = convertStreamToString(traceRt.getErrorStream());
            
           // if(errors.equals("")) System.out.println(errors);
        }
        catch (IOException e) {
            System.out.println("error while performing trace route command:"+ e);
        }

        return route;
    }
}
    
    


