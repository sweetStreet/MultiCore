package sequential;

public class Node<T extends Comparable> {
    T data;
    Node left;
    Node right;
    public Node(T data){
        this.data = data;
        left = null;
        right = null;
    }
}
