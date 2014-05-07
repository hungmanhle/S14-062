/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package traaceroutetest;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author a00826347
 */
public class TracerouteManip {
    private static final int EXTRA_LINES = 5;
    private String dump;
    private ArrayList<TRNode> nodeList;
    private ArrayList<TREdge> edgeList;
    private int nodeCount;
    
    public TracerouteManip(String dump)
    {
        this.dump = dump;
        nodeList = new ArrayList();
        edgeList = new ArrayList();
        nodeCount = 0;
        
    }
    
    public void buildItAll()
    {
        findNodeCount();
        buildNodeList();
        buildEdgeList();
    }
    
    private void buildNodeList() {
        Scanner scan = new Scanner(dump);
        //get past the bs at top
        scan.nextLine(); scan.nextLine(); scan.nextLine();
        
        int i;
        for(i = 0; i < nodeCount; i++)
        {
            String tmpLine = scan.nextLine();
            TRNode tmpNode = new TRNode(tmpLine);
            tmpNode.parseLine();
            nodeList.add(tmpNode);
        }
        
    }
    
    //private set
    
    private void findNodeCount() {
        
        //return nodeCount;
        
        Scanner scan = new Scanner(dump);
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
            TREdge tmp = new TREdge(nodeList.get(i-1), nodeList.get(i));
            edgeList.add(tmp);
        }
    }
    
    public TRNode getNode(int c)
    {
       return nodeList.get(c);
    }
    
     public TREdge getEdge(int c)
    {
        return edgeList.get(c);
    }
     
     public int getNodeCount()
     {
         return nodeCount;
     }
    
}
