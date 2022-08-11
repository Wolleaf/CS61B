package HW0;

import java.util.Scanner;

public class Exercise1a {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Please input the rows:");
        int rows = in.nextInt();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < i + 1; j++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }
}
