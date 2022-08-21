package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    public class ItemNode {
        private ItemNode prev;
        private T item;
        private ItemNode next;

        public ItemNode(T item, ItemNode prev, ItemNode next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    private ItemNode sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new ItemNode(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T value) {
        sentinel.next = new ItemNode(value, sentinel, sentinel.next);
        sentinel.next.next.prev = sentinel.next;
        size += 1;
    }

    @Override
    public void addLast(T value) {
        sentinel.prev = new ItemNode(value, sentinel.prev, sentinel);
        sentinel.prev.prev.next = sentinel.prev;
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        ItemNode p = sentinel.next;
        for (int i = 0; i < size; i += 1) {
            System.out.print(p.item.toString() + " ");
            p = p.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) return null;
        ItemNode temp = sentinel.next;
        sentinel.next.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        size -= 1;
        return temp.item;
    }

    @Override
    public T removeLast() {
        if (size == 0) return null;
        ItemNode temp = sentinel.prev;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        size -= 1;
        return temp.item;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index > size - 1) return null;
        ItemNode p = sentinel.next;
        for (int i = 0; i < index; i += 1) {
            p = p.next;
        }
        return p.item;
    }

    public T getRecursive(int index) {
        if (index == 0) return sentinel.next.item;
        LinkedListDeque<T> temp = new LinkedListDeque<>();
        temp.sentinel.next = sentinel.next.next;
        return temp.getRecursive(index - 1);
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private int cursor = 0;
        private ItemNode point = sentinel.next;
        @Override
        public boolean hasNext() {
            return cursor < size - 1;
        }

        @Override
        public T next() {
            ItemNode temp = point;
            cursor += 1;
            point = point.next;
            return temp.item;
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof LinkedListDeque)) return false;
        if (o == this) return true;
        if (((LinkedListDeque<T>) o).size != this.size) return false;
        ItemNode p = sentinel.next;
        LinkedListDeque<T>.ItemNode q = ((LinkedListDeque<T>) o).sentinel.next;
        for (int i = 0; i < size; i += 1) {
            if (!p.item.equals(q.item)) return false;
            p = p.next;
            q = q.next;
        }
        return true;
    }
}
