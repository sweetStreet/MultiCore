package lockfree;

import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicStampedReference;

public class Node<T extends Comparable>{
    T val;
    volatile AtomicStampedReference<Node> left;
    volatile AtomicStampedReference<Node> right;

    public Node(){

    }

    public Node(T val){
        this.val = val;
    }

    public Node(T val, AtomicStampedReference left, AtomicStampedReference right){
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
