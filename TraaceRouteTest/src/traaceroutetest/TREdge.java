/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package traaceroutetest;

/**
 *
 * @author a00826347
 */
public class TREdge {
    
    String node1; 
    String node2;
    int hopTime;
    
    public TREdge (TRNode one, TRNode two)
    {
        node1 = one.getIpAddr();
        node2 = two.getIpAddr();
        hopTime = two.getAvgHopTime();
    }
    //TODO getters/ setters
    
    public String toString()
    {
        String printout = "";
        printout = "    Node 1 IP: " + node1 + "\n    Node 2 IP: " + node2 
                + "\n    Hop time: " + hopTime + "\n";
        return printout;
    }
    
}
