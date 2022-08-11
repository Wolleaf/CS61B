package HW0;

public class Exercise3 {

    /** Returns the maximum value from m. */
    public static int max(int[] m) {
        int maxNumber = 0;
        for (int i = 0; i < m.length; i++) {
            if (m[i] > maxNumber) maxNumber = m[i];
        }
        /*for (int i : m) {
            if (i > maxNumber) maxNumber = i;
        }*/
        return maxNumber;
    }
    public static void main(String[] args) {
        int[] numbers = new int[]{9, 2, 15, 2, 22, 10, 6};
        System.out.println(max(numbers));
    }
}
