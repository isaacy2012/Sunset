package com.example.horizon_lite;

import java.util.Date;
import java.util.Scanner;

public class InnerHealth {
    /**
     * Takes a string and returns the nth word
     * @param line the string
     * @param n the index
     * @return the nth word
     */
    public static String nWord( String line, int n ) {
        Scanner scan = new Scanner( line );
        for (int i_scanner = 0; i_scanner < n; i_scanner++) {
            scan.next();
        }
        if (scan.hasNext()) {
            return scan.next();
        } else {
            return null;
        }
    }

    /**
     * Takes a string and returns how many words it has
     * @param line the line
     * @return how many words it has as an int
     */
    public static int nWords( String line ) {
        Scanner scan = new Scanner( line );
        int counter = 0;
        while (scan.hasNext()) {
            counter = counter+1;
            scan.next();
        }
        return counter;
    }

}
