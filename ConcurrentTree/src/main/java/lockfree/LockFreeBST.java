package lockfree;

import bst.BSTInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicStampedReference;

public class LockFreeBST <T extends Comparable> implements BSTInterface<T> {
    Node<T> root;

    @Override
    public boolean search(T val) {
        if(root == null){
            return false;
        }

        Node cur = root;

        //loop until a leaf is reached
        while(cur.left != null){
            if(val.compareTo(cur.val) < 0){
                cur = (Node) cur.left.getReference();
            }else{
                cur = (Node) cur.right.getReference();
            }
        }

        if(val.compareTo(cur.val) == 0){
            return true;
        }else{
            return false;
        }

    }

    @Override
    public boolean insert(T val) {
        if(root == null){
            root =  new Node(val);
        }
        Node cur = root;
        Node prev = root;
        boolean isLeft = false;

        while(true){
            while(cur.left != null){
                //loop until a leaf node is reached
                if(val.compareTo(cur.val) < 0){
                    prev = cur;
                    cur = (Node) cur.left.getReference();
                }else{
                    prev = cur;
                    cur = (Node) cur.right.getReference();
                }
            }

            Node oldChild = cur;

            if (val.compareTo(prev.val) < 0){
                isLeft = true;
            }

            //key is already present in tree, return false
            if(val.compareTo(cur.val) == 0){
                return false;
            }

            Node internalNode, lLeafNode,rLeafNode;
            if(val.compareTo(cur.val)>0){
                rLeafNode = new Node(val);
                internalNode = new Node(cur.val, new AtomicStampedReference<Node>(cur,0),new AtomicStampedReference<Node>(rLeafNode,0));
            }
            else{
                lLeafNode = new Node(val);
                internalNode = new Node(cur.val, new AtomicStampedReference<Node>(lLeafNode,0),new AtomicStampedReference<Node>(cur,0));
            }

            if(isLeft){
                //if(lUpdate.compareAndSet(pnode, oldChild, cr))
                if(prev.left.compareAndSet(oldChild, internalNode, 0, 0))
                {
                    //System.out.println("I3 " + insertKey);
                    return true;
                }
                else
                {
                    //insert failed; help the conflicting delete operation
                    insert(val);
                }
            }else{
                //if(rUpdate.compareAndSet(pnode, oldChild, cr))
                 if(prev.right.compareAndSet(oldChild, internalNode, 0, 0))
                {
                    //System.out.println("I4 " + insertKey);
                    return true;
                }
                else
                {
                    //insert failed; help the conflicting delete operation
                    insert(val);
                }
            }
        }

//        return false;
    }

    @Override
    public boolean delete(T val) {
        return false;
    }

    public void printNode() {
        printhelper(root);
//        int maxLevel = maxLevel(root);
//        printNodeInternal(Collections.singletonList(root), 1, maxLevel);
    }



    public void printhelper(Node node){
        if(node == null)
            return;
        System.out.println(node.val+" ");
        printhelper((Node) node.left.getReference());
        printhelper((Node) node.right.getReference());
    }

    @Override
    public int getHeight() {
        return maxLevel(root);
    }

    @Override
    public int getNum() {
        return 0;
    }

    private void printNodeInternal(List<Node> nodes, int level, int maxLevel) {
        if (nodes.isEmpty() || isAllElementsNull(nodes))
            return;

        int floor = maxLevel - level;
        int endgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
        int firstSpaces = (int) Math.pow(2, (floor)) - 1;
        int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;

        printWhitespaces(firstSpaces);

        List<Node> newNodes = new ArrayList<>();
        for (Node<T> node : nodes) {
            if (node != null && node.val != null) {
                System.out.print(node.val);
                newNodes.add(node.left.getReference());
                newNodes.add(node.right.getReference());
            } else {
                newNodes.add(null);
                newNodes.add(null);
                System.out.print(" ");
            }

            printWhitespaces(betweenSpaces);
        }
        System.out.println("");

        for (int i = 1; i <= endgeLines; i++) {
            for (int j = 0; j < nodes.size(); j++) {
                printWhitespaces(firstSpaces - i);
                if (nodes.get(j) == null) {
                    printWhitespaces(endgeLines + endgeLines + i + 1);
                    continue;
                }

                if (nodes.get(j).left != null && nodes.get(j).left != null)
                    System.out.print("/");
                else
                    printWhitespaces(1);

                printWhitespaces(i + i - 1);

                if (nodes.get(j).right != null && nodes.get(j).right != null)
                    System.out.print("\\");
                else
                    printWhitespaces(1);

                printWhitespaces(endgeLines + endgeLines - i);
            }

            System.out.println("");
        }

        printNodeInternal(newNodes, level + 1, maxLevel);
    }

    private void printWhitespaces(int count) {
        for (int i = 0; i < count; i++)
            System.out.print(" ");
    }

    private int maxLevel(Node<T> node) {
        if (node == null)
            return 0;
        return Math.max(maxLevel(node.left.getReference()), maxLevel(node.right.getReference())) + 1;
    }



    private <T> boolean isAllElementsNull(List<T> list) {
        for (Object object : list) {
            if (object != null)
                return false;
        }

        return true;
    }
}
