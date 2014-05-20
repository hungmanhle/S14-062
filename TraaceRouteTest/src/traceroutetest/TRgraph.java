
package traceroutetest;

import java.io.IOException;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;
//import org.apache.commons.io.IOUtils;

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
    private int numTimeouts;
    
    private TRgraph()
    {
        //dump = buildGraph(address);
        nodeList = new ArrayList();
        edgeList = new ArrayList();
        nodeCount = 0;
        numTimeouts = 0;
        
    }
    
//    public void buildGraph()
//    {
//        findNodeCount();
//        buildNodeList();
//        buildEdgeList();
//    }
//    
    private void addNode(String rawLine) 
    {
        int parse;
        
        TRLine tmpNode = new TRLine(rawLine);
        
        if((parse=tmpNode.parseLine())==1)//it was a good line
        {
            nodeList.add(tmpNode);
            nodeCount++;
        }
        else if (parse == 0)//timeout
        {
            numTimeouts++;
        }
                
        
        
    }
    
    public static TRgraph buildGraph(InetAddress address)
    { 
        String tmpLine = "";
        Scanner scan;
        StringWriter writer = new StringWriter();
        TRgraph tmpGraph = new TRgraph();            
        
        try {
            Process traceRt;
            //if(os.contains("win")) traceRt = Runtime.getRuntime().exec("tracert " + address.getHostAddress());
            //else 
            traceRt = Runtime.getRuntime().exec("tracert -d -4 " + address.getHostAddress());
			//System.out.println(address.getHostAddress());
			
			
            // read the output from the command
            scan = new Scanner(traceRt.getInputStream());
//            IOUtils.copy(traceRt.getInputStream(), writer, "UTF-8");
//            route = writer.toString();
            while (scan.hasNextLine())
            {
                tmpLine=scan.nextLine();
                tmpGraph.addNode(tmpLine);
                tmpGraph.dump += tmpLine + "\n";
                if(tmpGraph.numTimeouts > 5)
                {
                    //if it does timeout this many times it has probably gotten to the end node. we are making that assumption
					tmpGraph.addNode("1 4 ms 4 ms 4 ms " + address.getHostAddress()); 
                    break;
                }
                
            }
            tmpGraph.buildEdgeList();
            // read any errors from the attempted command
//            IOUtils.copy(traceRt.getErrorStream(), writer, "UTF-8");
//            String errors = writer.toString();
                        
            
           // if(errors.equals("")) System.out.println(errors);
        }
        catch (IOException e) {
            System.out.println("error while performing trace route command:"+ e);
        }

       return tmpGraph;
    }
    
//    private void findNodeCount() {
//        
//        
//        
//        Scanner scan = new Scanner(getDump());
//        int i;
//        for(i = 0; scan.hasNextLine(); i++)
//        {
//            scan.nextLine();
//        }
//        nodeCount = i - EXTRA_LINES;
//    }
    
    
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
