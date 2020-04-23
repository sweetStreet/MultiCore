package bst;

public interface BST<T extends Comparable> {

    boolean contains(T val);

    boolean insert(T val);

    boolean remove(T val);
}
