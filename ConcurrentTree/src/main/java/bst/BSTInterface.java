package bst;

public interface BSTInterface<T extends Comparable> {

    boolean search(T val);

    boolean insert(T val);

    boolean delete(T val);
}
