/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package traceroutetest;

import java.util.Scanner;

/**
 * For parsing and representing a line from traceroute
 * @author William Perry
 */
public class TRLine {
    //the number of times each hop is pinged
    private static final int NUM_TESTS = 3;
    // the raaw line from traceroute
    private String rawLine;
    // teh parsed IP 
    private String IP;
    // the average hop time between the nodes
    private int AverageHoptime;

    /**
     * Constructor
     * @param TrLine line from traceroute
     */
    public TRLine(String TrLine) {
        rawLine = TrLine;
        IP = "";
        AverageHoptime = 0;
        //parseLine();
    }
    
    /**
     * parses a line
     * @return 0 if there was a timeout, 1 if it worked, -1 if it was a filler line
     */
    public int parseLine() {
        Scanner scan = new Scanner(rawLine);

        int tempTotal = 0;
        int numsamples = 0;
        String tempToken = "";
        // only the lines we want start with ints
        if(scan.hasNextInt())
        {
            //get rid of line number        
            scan.next();
            
            //deal with all three times the same way 
            for (int i = 0; i < NUM_TESTS; i++) {
                if (scan.hasNextInt()) {
                    //scan time
                    tempTotal += scan.nextInt();
                    numsamples++;

                    // get rid of "ms"
                    scan.next();

                }
                else if ((tempToken = scan.next()).equals("*") )
                {
                    //do nothing, no "ms" to get rid of
                }
                else if (tempToken.equals("<1"))
                {

                    tempTotal += 1;
                    numsamples++;
                    // get rid of "ms"
                    scan.next();

                }

            }
            //this should be the ip if we parsed everything else right
            if( (tempToken = scan.next()).equals("Request"))
            {
                //there was a timeout
                return 0;
            }
            else
            {
                IP = tempToken;
            }
            //System.out.println(IP);
            AverageHoptime = tempTotal / numsamples;
        }
        else
        {
            //not a desireable line
            return -1;
        }
        return 1;
    }

    /**
     * 
     * @return 
     */
    public int getAvgHopTime() {
        return AverageHoptime;
    }

    /**
     *
     * @return
     */
    public String getIpAddr() {
        return IP;
    }
}
