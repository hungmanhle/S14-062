
package traceroutetest;

import java.io.IOException;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author a00826347
 */
public class TRgraph {
    private static final int EXTRA_LINES = 5;
    private String dump;
    private ArrayList<TRLine> nodeList;
    private ArrayList<NetworkEdge> edgeList;
    private int nodeCount;
    
    public TRgraph(InetAddress address)
    {
        dump = traceRoute(address);
        nodeList = new ArrayList();
        edgeList = new ArrayList();
        nodeCount = 0;
        
    }
    
    public void buildGraph()
    {
        findNodeCount();
        buildNodeList();
        buildEdgeList();
    }
    
    private void buildNodeList() {
        
        Scanner scan = new Scanner(getDump());
        //get past the bs at top
        scan.nextLine(); scan.nextLine(); scan.nextLine();
        
        int i;
        for(i = 0; i < nodeCount; i++)
        {
            String tmpLine = scan.nextLine();
            TRLine tmpNode = new TRLine(tmpLine);
            tmpNode.parseLine();
            nodeList.add(tmpNode);
        }
        
    }
    
    private static String traceRoute(InetAddress address){
        String route = "";
        
        StringWriter writer = new StringWriter();
                    
        
        try {
            Process traceRt;
            //if(os.contains("win")) traceRt = Runtime.getRuntime().exec("tracert " + address.getHostAddress());
            //else 
            traceRt = Runtime.getRuntime().exec("tracert -d -4 " + address.getHostAddress());

            // read the output from the command
            IOUtils.copy(traceRt.getInputStream(), writer, "UTF-8");
            route = writer.toString();
                        

            // read any errors from the attempted command
            IOUtils.copy(traceRt.getErrorStream(), writer, "UTF-8");
            String errors = writer.toString();
                        
            
           // if(errors.equals("")) System.out.println(errors);
        }
        catch (IOException e) {
            System.out.println("error while performing trace route command:"+ e);
        }

       return route;
    }
    
    private void findNodeCount() {
        
        
        
        Scanner scan = new Scanner(getDump());
        int i;
        for(i = 0; scan.hasNextLine(); i++)
        {
            scan.nextLine();
        }
        nodeCount = i - EXTRA_LINES;
    }
    
    
    private void buildEdgeList()
    {
        
        for(int i = 1; i < nodeCount; i++ )
        {
            NetworkEdge tmp = new NetworkEdge(nodeList.get(i-1), nodeList.get(i));
            edgeList.add(tmp);
        }
    }
    
    public TRLine getNode(int c)
    {
       return nodeList.get(c);
    }
    
     public NetworkEdge getEdge(int c)
    {
        return edgeList.get(c);
    }
     
     public int getNodeCount()
     {
         return nodeCount;
     }
     
     @Override
     public String toString()
     {
         int i = 1;
         String tmp = "";
         for(NetworkEdge e: edgeList)
            {
                tmp+=("Edge " + (i++) + ": \n" + e.toString());
            }
         return tmp;
     }

    /**
     * @return the dump
     */
    public String getDump() {
        return dump;
    }
    
}
