
package traceroutetest;

import java.io.IOException;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;
//import org.apache.commons.io.IOUtils;

/**
 * 
 * @author William Perry
 */
public class TRgraph {
    // this is what is returned by teh traceroute
    private String dump;
    // a list of the the parsed lines from the traceroute
    private ArrayList<TRLine> lineList;
    // a list of all the edges in the network traversal, ready to be stored
    private ArrayList<NetworkEdge> edgeList;
    // the number of parsed lines
    private int lineCount;
    // number of timeouts that have occured in the traceroute
    private int numTimeouts;
    
    private TRgraph()
    {
        //dump = buildGraph(address);
        lineList = new ArrayList();
        edgeList = new ArrayList();
        lineCount = 0;
        numTimeouts = 0;
        
    }
    
    /**
     * Parses an adds a line to lineList. Used in the buildGraph method
     * @param rawLine the raw line returned form running traceroute
     */
    private void addLine(String rawLine) 
    {
        int parse;
        
        TRLine tmpNode = new TRLine(rawLine);
        
        if((parse=tmpNode.parseLine()) == 1)//it was a good line
        {
            lineList.add(tmpNode);
            lineCount++;
        }
        else if (parse == 0)//timeout
        {
            numTimeouts++;
        }
        // otherwise the line wasn't a hop line
                
        
        
    }
    
    /**
     * builds a graph of the network traversal
     * @param address the IP that is being traced
     * @return the graph of the network traversal
     */
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
                tmpGraph.addLine(tmpLine);
                tmpGraph.dump += tmpLine + "\n";
                if(tmpGraph.numTimeouts > 5)
                {
                    //if it does timeout this many times it has probably gotten to the end node. we are making that assumption
					tmpGraph.addLine("1 4 ms 4 ms 4 ms " + address.getHostAddress()); 
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
    
    /**
     * builds edgeList from lineList
     */
    private void buildEdgeList()
    {
        
        for(int i = 1; i < lineCount; i++ )
        {
            NetworkEdge tmp = new NetworkEdge(lineList.get(i-1), lineList.get(i));
            edgeList.add(tmp);
        }
    }
    
    /**
     * Returns line with the index c
     * @param c the index of the desired line
     * @return the desired line
     */
    public TRLine getLine(int c)
    {
       return lineList.get(c);
    }
    
     /**
     * Returns edge with the index c
     * @param c the index of the desired edge
     * @return the desired edge
     */
    public NetworkEdge getEdge(int c)
    {
        return edgeList.get(c);
    }
     
     /**
     * getter...
     * @return the number of parsed lines
     */
    public int getNodeCount()
     {
         return lineCount;
     }
     
    /**
     * used in testing 
     * @return a nice printout of the graph
     */
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
     * getter
     * @return the dump
     */
    public String getDump() {
        return dump;
    }
    
}
