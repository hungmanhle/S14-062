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
    
    public void parseLine() {
        Scanner scan = new Scanner(rawLine);

        int tempTotal = 0;
        int numsamples = 0;
        String tempToken = "";
        
        //get rid of line number        
        scan.next();
        
        
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
                //do nothing
            }
            else if (tempToken.equals("<1"))
            {
                tempTotal += 1;
                numsamples++;
                // get rid of "ms"
                scan.next();
                
            }
            
        }
        //this should be the ip if weparsed everything else right
        IP = scan.next();
        //System.out.println(IP);
        AverageHoptime = tempTotal / numsamples;
    }

    public int getAvgHopTime() {
        return AverageHoptime;
    }

    public String getIpAddr() {
        return IP;
    }
}
