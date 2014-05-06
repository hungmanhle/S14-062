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
    private static final int EXTRA_LINES = 4;
    private String dump;
    private ArrayList<TRNode> nodeList;
    private ArrayList<TREdge> edgeList;
    private int nodeCount;
    
    public TracerouteManip(String dump)
    {
        this.dump = dump; 
        findNodeCount();
        buildNodeList();
    }
    
    private void buildNodeList() {
        Scanner scan = new Scanner(dump);
        //get past the bs at top
        scan.nextLine(); scan.nextLine();
        
        int i;
        for(i = 0; i < nodeCount; i++)
        {
            String tmpLine = scan.nextLine();
            nodeList.add(new TRNode(tmpLine));
        }
        
    }
    
    //private set
    
    private int findNodeCount() {
        
        //return nodeCount;
        
        Scanner scan = new Scanner(dump);
        int i;
        for(i = 0; scan.hasNextLine(); i++)
        {
            scan.nextLine();
        }
        return i - EXTRA_LINES;
    }
    
    
    private void buildEdgeList()
    {
        //TODO 
        //for(int i = 0; )
    }
    
    public TRNode getNode(int c)
    {
        TRNode node = nodeList.get(c);
        
        
        return node;
    }
    
    //public ArrayList parseHop()
    {
        
        
    }
    
}
