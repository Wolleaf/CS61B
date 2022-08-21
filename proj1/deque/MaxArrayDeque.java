package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque() {
        super();
    }

    public MaxArrayDeque(Comparator<T> comparator) {
        super();
        this.comparator = comparator;
    }

    public T max() {
        if (size == 0) { return null; }
        int p = (head + 1) % items.length;
        T temp = items[p];
        for (int i = 0; i < size - 1; i += 1) {
            p = (p + 1) % items.length;
            if (comparator.compare(temp, items[p]) < 0) temp = items[p];
        }
        return temp;
    }

    public T max(Comparator<T> comparator) {
        if (size == 0) { return null; }
        int p = (head + 1) % items.length;
        T temp = items[p];
        for (int i = 0; i < size - 1; i += 1) {
            p = (p + 1) % items.length;
            if (comparator.compare(temp, items[p]) < 0) temp = items[p];
        }
        return temp;
    }
}
