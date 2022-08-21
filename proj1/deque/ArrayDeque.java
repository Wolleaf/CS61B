package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
    protected T[] items;
    protected int size;
    protected int head;
    protected int tail;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        head = 0;
        tail = 1;
    }

    @Override
    public void addFirst(T value) {
        if (size == items.length - 2) { resize(items.length + (items.length >> 1)); }
        items[head] = value;
        if (head == 0) { head = items.length - 1; }
        else { head -= 1; }
        size += 1;
    }

    @Override
    public void addLast(T value) {
        if (size == items.length - 2) { resize(items.length + (items.length >> 1)); }
        items[tail] = value;
        tail = (tail + 1) % items.length;
        size += 1;
    }

    @Override
    public T removeFirst() {
        if (size == 0) { return null; }
        if (size < items.length / 4 && size > 4) { resize(items.length / 4); }
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

    @Override
    public T removeLast() {
        if (size == 0) { return null; }
        if (size < items.length / 4 && size > 4) { resize(items.length / 4); }
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

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int p = head;
        for (int i = 0; i < size; i += 1) {
            p = (p + 1) % items.length;
            System.out.print(items[p].toString() + " ");
        }
        System.out.println();
    }

    @Override
    public T get(int index) {
        int p = (head + 1) % items.length;
        while (index != 0) {
            p = (p + 1) % items.length;
            index -= 1;
        }
        return items[p];
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
        if (!(o instanceof ArrayDeque)) { return false; }
        if (o == this) { return true; }
        if (((Deque<T>) o).size() != this.size) { return false; }
        for (int i = 0; i < size; i += 1) {
            if (((Deque<T>) o).get(i) != this.get(i)) { return false; }
        }
        return true;
    }
}
