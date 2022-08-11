package HW0;

public class Exercise2 {
    /** Returns the maximum value from m. */
    public static int max(int[] m) {
        int maxNumber = 0;
        int i = 0;
        while (i < m.length){
            if (m[i] > maxNumber) maxNumber = m[i];
            i++;
        }
        return maxNumber;
    }
    public static void main(String[] args) {
        int[] numbers = new int[]{9, 2, 15, 2, 22, 10, 6};
        System.out.println(max(numbers));
    }
}
