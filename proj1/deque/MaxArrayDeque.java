package deque;

import java.util.Comparator;
import java.util.Iterator;

public class MaxArrayDeque<T> implements Iterable<T> {
    private T[] items;
    private int size;
    private int head;
    private int tail;
    private Comparator<T> comparator;

    public MaxArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        head = 0;
        tail = 1;
    }

    public MaxArrayDeque(Comparator<T> comparator) {
        this();
        this.comparator = comparator;
    }

    public void addFirst(T value) {
        if (size == items.length - 2) resize(items.length + (items.length >> 1));
        items[head] = value;
        if (head == 0) head = items.length - 1;
        else head -= 1;
        size += 1;
    }

    public void addLast(T value) {
        if (size == items.length - 2) resize(items.length + (items.length >> 1));
        items[tail] = value;
        tail = (tail + 1) % items.length;
        size += 1;
    }

    public T removeFirst() {
        if (size == 0) return null;
        if (size < items.length / 4 && size > 4) resize(items.length / 4);
        T temp;
        if (head == items.length - 1) {
            temp = items[0];
            items[0] = null;
            head = 0;
        } else {
            temp = items[head + 1];
            items[head + 1] = null;
            head += 1;
        }
        size -= 1;
        return temp;
    }

    public T removeLast() {
        if (size == 0) return null;
        if (size < items.length / 4 && size > 4) resize(items.length / 4);
        T temp;
        if (tail == 0) {
            temp = items[items.length - 1];
            items[items.length - 1] = null;
            tail = items.length - 1;
        } else {
            temp = items[tail - 1];
            items[tail - 1] = null;
            tail -= 1;
        }
        size -= 1;
        return temp;
    }

    public void resize(int capacity) {
        T[] newQueue = (T[]) new Object[capacity];
        for (int i = 0; i < size; i += 1) {
            head = (head + 1) % items.length;
            newQueue[i] = items[head];
        }
        head = newQueue.length - 1;
        tail = size;
        items = newQueue;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int p = head;
        for (int i = 0; i < size; i += 1) {
            p = (p + 1) % items.length;
            System.out.print(items[p].toString() + " ");
        }
        System.out.println();
    }

    public T get(int index) {
        int p = (head + 1) % items.length;
        while (index != 0) {
            p = (p + 1) % items.length;
            index -= 1;
        }
        return items[p];
    }

    public T max() {
        if (size == 0) return null;
        int p = (head + 1) % items.length;
        T temp = items[p];
        for (int i = 0; i < size - 1; i += 1) {
            p = (p + 1) % items.length;
            if (comparator.compare(temp, items[p]) < 0) temp = items[p];
        }
        return temp;
    }

    public T max(Comparator<T> comparator) {
        if (size == 0) return null;
        int p = (head + 1) % items.length;
        T temp = items[p];
        for (int i = 0; i < size - 1; i += 1) {
            p = (p + 1) % items.length;
            if (comparator.compare(temp, items[p]) < 0) temp = items[p];
        }
        return temp;
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {

        private int cursor = head;
        @Override
        public boolean hasNext() {
            return (cursor + 1) % items.length != tail;
        }

        @Override
        public T next() {
            cursor = (cursor + 1) % items.length;
            return items[cursor];
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof MaxArrayDeque)) return false;
        if (o == this) return true;
        if (((MaxArrayDeque<T>) o).size != this.size) return false;
        int p = head;
        int q = ((MaxArrayDeque<?>) o).head;
        for (int i = 0; i < size; i += 1) {
            p = (p + 1) % items.length;
            q = (q + 1) % ((MaxArrayDeque<?>) o).items.length;
            if (!(items[p].equals(((MaxArrayDeque<?>) o).items[q]))) return false;
        }
        return true;
    }
}
