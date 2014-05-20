/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package traceroutetest;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author a00826347
 */
public class NetworkgraphTest {

    /**
     * @param args the command line arguments
     */
    
    private final static String os = System.getProperty("os.name");
    
    public static void main(String[] args) {
       // String result = "";
        try {
            InetAddress placeToTrace = InetAddress.getByName(args[0]);
            
           // result = traceRoute(placeToTrace);
            //System.out.println(result + "\n");
            TRgraph trm = TRgraph.buildGraph(placeToTrace);
            
           // for(int i = 0; i < (trm.getNodeCount()-1); i++)
            //{
                //System.out.println("Edge " + (i+1) + ": \n" + trm.getEdge(i).toString());
                
           // }
            System.out.println(trm.getDump());
            System.out.println(trm.toString());
        } catch (UnknownHostException ex) {
            Logger.getLogger(NetworkgraphTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
        }
        
        /*TRNode testNode = new TRNode("1     2 ms     2 ms     4 ms  142.232.246.62");
        
        System.out.println("IP: " + testNode.getIpAddr() + "\nAvgHop: " + testNode.getAvgHopTime());
        * */
    }
    
    
}
    
    


