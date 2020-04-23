package lockfree;

import bst.BST;

public class LockFreeBST<T> implements BST {
    @Override
    public boolean contains(Comparable val) {
        return false;
    }

    @Override
    public boolean insert(Comparable val) {
        return false;
    }

    @Override
    public boolean remove(Comparable val) {
        return false;
    }
}
