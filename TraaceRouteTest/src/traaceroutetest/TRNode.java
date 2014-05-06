/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package traaceroutetest;

import java.util.Scanner;

/**
 *
 * @author a00826347
 */
public class TRNode {

    private static final int NUM_TESTS = 3;
    private String rawLine;
    private String IP;
    private int AverageHoptime;

    public TRNode(String TrLine) {
        rawLine = TrLine;
        parseLine();
    }
    
    private void parseLine() {
        Scanner scan = new Scanner(rawLine);

        int tempTotal = 0;

        //get rid of line number
        scan.next();
        for (int i = 0; i < NUM_TESTS; i++) {
            if (scan.hasNextInt()) {
                //scan time
                tempTotal += scan.nextInt();
            }
            //else if ()
            // get rid of "ms"
            scan.next();
        }
        //this should be the ip if weparsed everything else right
        IP = scan.next();
        AverageHoptime = tempTotal / NUM_TESTS;
    }

    public int getAvgHopTime() {
        return AverageHoptime;
    }

    public String getIpAddr() {
        return IP;
    }
}
