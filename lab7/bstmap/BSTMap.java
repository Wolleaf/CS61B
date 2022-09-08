package bstmap;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class BSTNode<S, U> {
        private S key;
        private U value;
        private BSTNode<S, U> left;
        private BSTNode<S, U> right;
        private BSTNode<S, U> parent;

        public BSTNode() {
            this(null, null, null, null, null);
        }

        public BSTNode(S key, U value) {
            this(key, value, null, null, null);
        }

        public BSTNode(S key, U value, BSTNode<S, U> left, BSTNode<S, U> right, BSTNode<S, U> parent) {
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
            this.parent = parent;
        }
    }

    private BSTNode<K, V> bstNode;
    private int size;

    public BSTMap() {
        bstNode = null;
        size = 0;
    }

    public BSTMap(K key, V value) {
        this(key, value, null, null, null);
    }

    public BSTMap(K key, V value, BSTNode<K, V> left, BSTNode<K, V> right) {
        this(key, value, left, right, null);
    }

    public BSTMap(K key, V value, BSTNode<K, V> left, BSTNode<K, V> right, BSTNode<K, V> parent) {
        bstNode = new BSTNode<>(key, value, left, right, parent);
        size = 1;
    }
    @Override
    public void clear() {
        bstNode = null;
        size = 0;
    }

    private BSTNode<K, V> find(BSTNode<K, V> node, K key) {
        if (node == null) {
            return null;
        }
        BSTNode<K, V> temp;
        if (node.key.compareTo(key) == 0) {
            temp = node;
        } else if (node.key.compareTo(key) > 0) {
            temp = find(node.left, key);
        } else if (node.key.compareTo(key) < 0) {
            temp = find(node.right, key);
        } else {
            temp = null;
        }
        return temp;
    }

    @Override
    public boolean containsKey(K key) {
        return find(bstNode, key) != null;
    }

    @Override
    public V get(K key) {
        BSTNode<K, V> temp = find(bstNode, key);
        if (temp != null) {
            return temp.value;
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        bstNode = helpInsert(bstNode, key, value);
    }

    private BSTNode<K, V> helpInsert(BSTNode<K, V> node, K key, V value) {
        if (node == null) {
            node = new BSTNode<>(key, value);
            size++;
        } else if (node.key.compareTo(key) > 0) {
            node.left = helpInsert(node.left, key, value);
            node.left.parent = node;
        } else if (node.key.compareTo(key) < 0) {
            node.right = helpInsert(node.right, key, value);
            node.right.parent = node;
        }
        return node;
    }

    private void traversalByInOrder(BSTNode<K, V> node, Set<K> kSet) {
        if (node == null) {
            return;
        }
        traversalByInOrder(node.left, kSet);
        kSet.add(node.key);
        traversalByInOrder(node.right, kSet);
    }

    @Override
    public Set<K> keySet() {
        Set<K> kSet = new LinkedHashSet<>();
        traversalByInOrder(bstNode, kSet);
        return kSet;
    }

    private BSTNode<K, V> findRightChild(BSTNode<K, V> node) {
        BSTNode<K, V> temp = node;
        while (temp.right != null) {
            temp = temp.right;
        }
        return temp;
    }

    private BSTNode<K, V> findLeftChild(BSTNode<K, V> node) {
        BSTNode<K, V> temp = node;
        while (temp.left != null) {
            temp = temp.left;
        }
        return temp;
    }

    private void removeNode(BSTNode<K, V> node) {
        if (node == null) {
            return;
        }
        if (node.parent == null && node.left == null && node.right == null) {
            bstNode = null;
            size--;
        } else if (node.left == null && node.right == null) {
            if (node.parent.left != null && node.parent.left.equals(node)) {
                node.parent.left = null;
            } else {
                node.parent.right = null;
            }
            size--;
        } else if (node.left != null) {
            BSTNode<K, V> temp = findRightChild(node.left);
            node.key = temp.key;
            node.value = temp.value;
            removeNode(temp);
        } else if (node.left == null && node.right != null) {
            BSTNode<K, V> temp = findLeftChild(node.right);
            node.key = temp.key;
            node.value = temp.value;
            removeNode(temp);
        }
    }
    @Override
    public V remove(K key) {
        BSTNode<K, V> targetNode = find(bstNode, key);
        if (targetNode == null) {
            return null;
        }
        V tempValue = targetNode.value;
        removeNode(targetNode);
        return tempValue;
    }

    @Override
    public V remove(K key, V value) {
        BSTNode<K, V> targetNode = find(bstNode, key);
        if (targetNode == null) {
            return null;
        }
        if (!targetNode.value.equals(value)) {
            return null;
        }
        V tempValue = targetNode.value;
        removeNode(targetNode);
        return tempValue;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
