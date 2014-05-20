/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package traceroutetest;

import java.util.Scanner;

/**
 *
 * @author a00826347
 */
public class TRLine {

    private static final int NUM_TESTS = 3;
    private String rawLine;
    private String IP;
    private int AverageHoptime;

    public TRLine(String TrLine) {
        rawLine = TrLine;
        IP = "";
        AverageHoptime = 0;
        //parseLine();
    }
    //retruns 0 if there was a timeout, 1 if it worked, -1 if it was a filler line
    public int parseLine() {
        Scanner scan = new Scanner(rawLine);

        int tempTotal = 0;
        int numsamples = 0;
        String tempToken = "";
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

    public int getAvgHopTime() {
        return AverageHoptime;
    }

    public String getIpAddr() {
        return IP;
    }
}
