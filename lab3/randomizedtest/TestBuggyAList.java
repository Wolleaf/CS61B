package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> aListNoResizing = new AListNoResizing<>();
        BuggyAList<Integer> buggyAList = new BuggyAList<>();
        for (int i = 4; i < 7; i += 1) {
            aListNoResizing.addLast(i);
            buggyAList.addLast(i);
        }
        assertEquals(aListNoResizing.size(), buggyAList.size());
        assertEquals(aListNoResizing.removeLast(), buggyAList.removeLast());
        assertEquals(aListNoResizing.removeLast(), buggyAList.removeLast());
        assertEquals(aListNoResizing.removeLast(), buggyAList.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> buggyAList = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                buggyAList.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                assertEquals(L.size(), buggyAList.size());
            } else if (operationNumber == 2) {
                assertEquals(L.size(), buggyAList.size());
                if (L.size() == 0) continue;
                assertEquals(L.getLast(), buggyAList.getLast());
            } else if (operationNumber == 3) {
                assertEquals(L.size(), buggyAList.size());
                if (L.size() == 0) continue;
                assertEquals(L.removeLast(), buggyAList.removeLast());
            }
        }
    }
}
