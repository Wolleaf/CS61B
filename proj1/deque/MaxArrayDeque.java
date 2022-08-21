package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> comparator) {
        super();
        this.comparator = comparator;
    }

    public T max() {
        if (this.size() == 0) {
            return null;
        }
        T temp = this.get(0);
        int length = this.size();
        for (int i = 1; i < length; i++) {
            if (comparator.compare(temp, this.get(i)) < 0) {
                temp = this.get(i);
            }
        }
        return temp;
    }

    public T max(Comparator<T> myComparator) {
        if (this.size() == 0) {
            return null;
        }
        T temp = this.get(0);
        int length = this.size();
        for (int i = 1; i < length; i++) {
            if (myComparator.compare(temp, this.get(i)) < 0) {
                temp = this.get(i);
            }
        }
        return temp;
    }
}
