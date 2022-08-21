package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

public class MaxArrayDequeTest {
    @Test
    public void maxComparatorTest() {
        MaxArrayDeque<Integer> maxArrayDeque = new MaxArrayDeque<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
        for (int i = 1; i < 11; i += 1) {
            maxArrayDeque.addFirst(i);
        }
        assertEquals(10, maxArrayDeque.max().intValue());
    }

    @Test
    public void minComparatorTest() {
        MaxArrayDeque<Integer> maxArrayDeque = new MaxArrayDeque<>(((o1, o2) -> o2 - o1));
        for (int i = 1; i < 11; i += 1) {
            maxArrayDeque.addLast(i);
        }
        assertEquals(1, maxArrayDeque.max().intValue());
    }

    @Test
    public void stringLengthComparatorTest() {
        MaxArrayDeque<String> maxArrayDeque = new MaxArrayDeque<>((o1, o2) -> o1.length() - o2.length());
        maxArrayDeque.addLast("hh");
        maxArrayDeque.addLast("hhh");
        maxArrayDeque.addLast("hhh");
        maxArrayDeque.addLast("hhhh");
        maxArrayDeque.addLast("hhhhh");
        assertEquals("hhhhh", maxArrayDeque.max());
    }

    @Test
    public void stringValueComparatorTest1() {
        MaxArrayDeque<String> maxArrayDeque = new MaxArrayDeque<>(((o1, o2) -> o1.compareTo(o2)));
        maxArrayDeque.addLast("hjh");
        maxArrayDeque.addLast("jzy");
        maxArrayDeque.addLast("ok");
        maxArrayDeque.addLast("nothing");
        maxArrayDeque.addLast("everything");
        maxArrayDeque.addLast("fight");
        maxArrayDeque.addLast("champion");
        assertEquals("ok", maxArrayDeque.max());
    }

    @Test
    public void stringValueComparatorTest2() {
        MaxArrayDeque<String> maxArrayDeque = new MaxArrayDeque<>(((o1, o2) -> o2.compareTo(o1)));
        maxArrayDeque.addLast("hjh");
        maxArrayDeque.addLast("jzy");
        maxArrayDeque.addLast("ok");
        maxArrayDeque.addLast("nothing");
        maxArrayDeque.addLast("everything");
        maxArrayDeque.addLast("fight");
        maxArrayDeque.addLast("champion");
        assertEquals("champion", maxArrayDeque.max((o1, o2) -> o2.compareTo(o1)));
    }
}
