package deque;

import static org.junit.Assert.*;

import org.checkerframework.checker.units.qual.A;
import org.junit.Test;

public class EqualTest {
    @Test
    public void testArrayDequeLinkedListDequeInteger() {
        LinkedListDeque<Integer> linkedListDeque = new LinkedListDeque<>();
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();

        for (int i = 0; i < 6; i += 1) {
            linkedListDeque.addFirst(i);
            arrayDeque.addFirst(i);
        }

        boolean equals = linkedListDeque.equals(arrayDeque);

        assertTrue("should be true", equals);
    }

    @Test
    public void testArrayDequeLinkedListDequeString() {
        LinkedListDeque<String> linkedListDeque = new LinkedListDeque<>();
        ArrayDeque<String> arrayDeque = new ArrayDeque<>();

        for (int i = 0; i < 6; i += 1) {
            String value1 = "value is " + i;
            String value2 = "value is " + i;
            linkedListDeque.addFirst(value1);
            arrayDeque.addFirst(value2);
        }

        boolean equals = linkedListDeque.equals(arrayDeque);

        assertTrue("should be true", equals);
    }

    @Test
    public void testArrayDequeString() {
        ArrayDeque<String> arrayDeque1 = new ArrayDeque<>();
        ArrayDeque<String> arrayDeque2 = new ArrayDeque<>();

        for (int i = 0; i < 6; i += 1) {
            String value1 = "value is " + i;
            String value2 = "value is " + i;
            arrayDeque1.addFirst(value1);
            arrayDeque2.addFirst(value2);
        }

        boolean equals = arrayDeque1.equals(arrayDeque2);

        assertTrue("should be true", equals);
    }
}
