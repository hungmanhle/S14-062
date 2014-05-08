/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package traceroutetest;

/**
 *
 * @author a00826347
 */
public class NetworkEdge {
    
    private String node1; 
    private String node2;
    private int hopTime;
    
    public NetworkEdge (TRLine one, TRLine two)
    {
        //make sure the IP that is  lexicographically is node1
        if (one.getIpAddr().compareTo(two.getIpAddr()) < 0){
            node1 = one.getIpAddr();
            node2 = two.getIpAddr();
        }else{
            node1 = two.getIpAddr();
            node2 = one.getIpAddr();
        }
        hopTime = two.getAvgHopTime();
    }
    
    
    @Override
    public String toString()
    {
        
        String printout = "    Node 1 IP: " + getNode1() + "\n    Node 2 IP: " + getNode2() 
                + "\n    Hop time: " + getHopTime() + "\n";
        return printout;
    }

    /**
     * @return the node1
     */
    public String getNode1() {
        return node1;
    }

    /**
     * @return the node2
     */
    public String getNode2() {
        return node2;
    }

    /**
     * @return the hopTime
     */
    public int getHopTime() {
        return hopTime;
    }
    
}
