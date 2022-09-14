package hashmap;

import java.awt.event.MouseAdapter;
import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Wolleaf Lee
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int size;
    private int capacity;
    private double maxLoad;
    private double loadFactor = 2d;

    /** Constructors */
    public MyHashMap() {
        this(16, 0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = new Collection[initialSize];
        capacity = initialSize;
        this.maxLoad = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedHashSet<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below

    @Override
    public void clear() {
        buckets = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return !(find(key) == null);
    }

    @Override
    public V get(K key) {
        Node temp = find(key);
        if (temp == null) {
            return null;
        }
        return temp.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        int pos = Math.floorMod(key.hashCode(), capacity);
        if (buckets[pos] == null) {
            buckets[pos] = createBucket();
        }
        Node temp = new Node(key, value);
        for (Node node : buckets[pos]) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }
        buckets[pos].add(temp);
        size++;
        if (size / (double) capacity >= maxLoad) {
            resize((int) (capacity * loadFactor));
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (int i = 0; i < capacity; i++) {
            if (buckets[i] == null) {
                continue;
            }
            for (Node node : buckets[i]) {
                set.add(node.key);
            }
        }
        return set;
    }

    @Override
    public V remove(K key) {
        int pos = Math.floorMod(key.hashCode(), capacity);
        if (buckets == null || buckets[pos] == null) {
            return null;
        }
        for (Node node : buckets[pos]) {
            if (node.key.equals(key)) {
                buckets[pos].remove(node);
                return node.value;
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        int pos = Math.floorMod(key.hashCode(), capacity);
        if (buckets == null || buckets[pos] == null) {
            return null;
        }
        for (Node node : buckets[pos]) {
            if (node.key.equals(key) && node.value.equals(value)) {
                buckets[pos].remove(node);
                return node.value;
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    private void resize(int newCapacity) {
        int tempCapacity = capacity;
        capacity = newCapacity;
        Collection<Node>[] tempBuckets = buckets;
        buckets = createTable(newCapacity);
        size = 0;
        for (int i = 0; i < tempCapacity; i++) {
            if (tempBuckets[i] == null) {
                continue;
            }
            for (Node node : tempBuckets[i]) {
                put(node.key, node.value);
            }
        }
    }

    private Node find(K key) {
        int pos = Math.floorMod(key.hashCode(), capacity);
        if (buckets == null || buckets[pos] == null) {
            return null;
        }
        for (Node node : buckets[pos]) {
            if (node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }
    // Your code won't compile until you do so!

}
