package lab1;

import java.util.Scanner;

public class Collatz {

    /**
     * If n is even, the next number is n/2. If n is odd, the next number is 3n + 1.
     * @param n the number
     * @return the next number of Collatz Sequence
     */
    public static int nextNumber(int n) {
        if ((n & 1) == 0) return n / 2;
        else return n * 3 + 1;
    }

    /**
     * Type in a number and print the Collatz Sequence
     * @param args none
     */
    public static void main(String[] args) {
        int n = 5;
        while (n != 1) {
            System.out.print(n + " ");
            n = nextNumber(n);
        }
        System.out.println(n);
    }
}
