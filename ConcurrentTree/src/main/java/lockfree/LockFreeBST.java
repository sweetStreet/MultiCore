package lockfree;

import bst.BSTInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicStampedReference;

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeBST<T extends Comparable> implements BSTInterface<T>{
    AtomicReference<LockFreeNode> root;

    public LockFreeBST()
    {
        root = new AtomicReference<LockFreeNode>(null);
    }

    @Override
    public boolean search(T data)
    {
        LockFreeNode curNode = root.get();
        boolean[] marked = {false};

        // Tree is not empty, search the tree
        while(curNode != null){
            if(curNode.data.compareTo(data)>0){
                // curNode is "bigger" than the passed data, search the left subtree
                curNode = curNode.getChild("LEFT", marked);
            }
            else if(curNode.data.compareTo(data)<0){
                // curNode is "smaller" than the passed data, search the right subtree
                curNode = curNode.getChild("RIGHT", marked);
            }
            else{
                // Found the data, make sure that it isn't an internal node and that is isn't marked
                if(curNode.isLeaf())
                    return !curNode.isMarked();
                else
                    curNode = curNode.getChild("RIGHT", marked);
            }
        }

        //Tree is empty or data is not in the tree
        return false;
    }


    private LockFreeNode createSubtree(LockFreeNode parentNode, LockFreeNode newNode, int compare)
    {
        LockFreeNode newParent;
        if(compare > 0)
        {
            newParent = new LockFreeNode(parentNode.data, newNode, parentNode);
        }
        else
        {
            newParent = new LockFreeNode(newNode.data, parentNode, newNode);
        }
        return newParent;
    }


    public boolean insert(T data)
    {

        LockFreeNode newNode = new LockFreeNode(data);
        LockFreeNode newParent = null;
        LockFreeNode curNode = null;
        LockFreeNode parentNode = null;
        LockFreeNode gparentNode = null;
        LockFreeNode ggparentNode = null;
        int compare = 0, oldCompare = 0, reallyOldCompare = 0;
        boolean[] marked = {false};

        retry: while(true)
        {
            curNode = root.get();
            if(curNode == null)
            {
                // Tree is empty, try to insert newNode as the root
                if(root.compareAndSet(null, newNode))
                    return true;
                else
                    continue retry;
            }
            else
            {
                // Tree is not empty, iterate into the tree
                while(curNode != null)
                {
                    ggparentNode = gparentNode;
                    gparentNode = parentNode;
                    parentNode = curNode;
                    reallyOldCompare = oldCompare;
                    oldCompare = compare;
                    // compare = curNode.data.compareTo(data);
                    if (curNode.data.compareTo(data)>0)
                        compare = 5;
                    else if (curNode.data.compareTo(data)<0)
                        compare = -5;
                    else
                        compare = 0;

                    if(compare > 0)
                    {
                        // curNode is "bigger" than the passed data, iterate into the left subtree
                        curNode = curNode.getChild("LEFT", marked);
                    }
                    else if(compare < 0)
                    {
                        // curNode is "smaller" than the passed data, iterate into the right subtree
                        curNode = curNode.getChild("RIGHT", marked);
                    }
                    else
                    {
                        // If this is a leaf node, then the data is already in the tree
                        // Otherwise, we can keep traversing
                        if(curNode.isLeaf())
                            return false; //TODO if its marked, can this thread
                            //try and remove it then restart the
                            //insertion?
                        else
                            curNode = curNode.getChild("RIGHT", marked);
                    }
                }

                // Check edge cases
                if(gparentNode == null)
                {
                    // Edge case 1: inserting at 1st level (tree only has 1 element)
                    if(parentNode.isMarked())
                    {
                        // Taking the courtesy of deleting the marked node
                        root.compareAndSet(parentNode, null);
                        continue retry;
                    }
                    newParent = createSubtree(parentNode, newNode, compare);
                    if(root.compareAndSet(parentNode, newParent))
                        return true;
                    else
                        continue retry;
                }
                else if(ggparentNode == null)
                {
                    // Edge case 2: Inserting at 2nd level (tree has 2 elements)
                    if(parentNode.isMarked())
                    {
                        if(oldCompare > 0)
                            parentNode = gparentNode.getChild("RIGHT", marked);
                        else
                            parentNode = gparentNode.getChild("LEFT", marked);
                        if(!root.compareAndSet(gparentNode, parentNode))
                            continue retry;
                        newParent = createSubtree(parentNode, newNode, oldCompare);
                        if(root.compareAndSet(parentNode, newParent))
                            return true;
                        else
                            continue retry;
                    }
                }
                else if(parentNode.isMarked())
                {
                    // Edge case 3: Attempt to delete parentNode if it is marked
                    if(oldCompare > 0)
                        newParent = gparentNode.getChild("RIGHT", marked);
                    else
                        newParent = gparentNode.getChild("LEFT", marked);
                    if(reallyOldCompare > 0)
                    {
                        if(!ggparentNode.insertChild("LEFT", gparentNode, newParent))
                            continue retry;
                    }
                    else
                    {
                        if(!ggparentNode.insertChild("RIGHT", gparentNode, newParent))
                            continue retry;
                    }
                    parentNode = newParent;
                    gparentNode = ggparentNode;
                    compare = oldCompare;
                    oldCompare = reallyOldCompare;
                }

                // Attempt insertion
                newParent = createSubtree(parentNode, newNode, compare);
                if(oldCompare > 0)
                {
                    if(gparentNode.insertChild("LEFT", parentNode, newParent))
                        return true;
                    else
                        continue retry;
                }
                else
                {
                    if(gparentNode.insertChild("RIGHT", parentNode, newParent))
                        return true;
                    else
                        continue retry;
                }
            }
        }
    }


    @Override
    public void printNode() {

    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getNum() {
        return 0;
    }


    public boolean delete(T data)
    {
        LockFreeNode newParent = null;
        LockFreeNode curNode = null;
        LockFreeNode parentNode = null;
        LockFreeNode gparentNode = null;
        int compare = 0, oldCompare = 0;
        boolean[] marked = {false};

        retry: while(true)
        {
            // Check to see if the tree is empty
            curNode = root.get();
            if(curNode == null)
                return false;
            else
            {
                // The tree isn't empty, iterate into the tree
                parentNode = curNode;
                while(curNode != null)
                {
                    // compare = curNode.data.compareTo(data);
                    if (curNode.data.compareTo(data)>0)
                        compare = 5;
                    else if (curNode.data.compareTo(data)<0)
                        compare = -5;
                    else
                        compare = 0;

                    if(compare > 0)
                    {
                        // curNode is "bigger" than the passed data, iterate into the left subtree
                        curNode = curNode.getChild("LEFT", marked);
                    }
                    else if(compare < 0)
                    {
                        // curNode is "smaller" than the passed data, iterate into the right subtree
                        curNode = curNode.getChild("RIGHT", marked);
                    }
                    else
                    {
                        //If this is a leaf node, then the data is in the tree and can be removed
                        // Otherwise, we keep traversing
                        if(curNode.isLeaf())
                        {
                            // Attempt to mark the current node.  Note that this is the linearization point.
                            // Even if we can't physically remove the node, if this call succeeds then we were successful
                            if(!curNode.mark())
                                continue retry;

                            if(parentNode == null)
                            {
                                // Edge case 1: deletion of root
                                root.compareAndSet(curNode, null);
                            }
                            else if(gparentNode == null)
                            {
                                // Edge case 2: deletion at 1st level
                                if(compare > 0)
                                    newParent = parentNode.getChild("RIGHT", marked);
                                else
                                    newParent = parentNode.getChild("LEFT", marked);
                                root.compareAndSet(parentNode, newParent);
                            }
                            else
                            {
                                //Normal deletion.  Attempt to physically remove the node
                                if(compare > 0)
                                    newParent = parentNode.getChild("RIGHT", marked);
                                else
                                    newParent = parentNode.getChild("LEFT", marked);
                                if(oldCompare > 0)
                                {
                                    gparentNode.insertChild("LEFT", parentNode, newParent);
                                }
                                else
                                {
                                    gparentNode.insertChild("RIGHT", parentNode, newParent);
                                }
                            }
                            return true;
                        }
                        else
                            curNode = curNode.getChild("RIGHT", marked);
                    }
                    gparentNode = parentNode;
                    parentNode = curNode;
                    oldCompare = compare;
                }

                // The data wasn't in the tree
                return false;
            }
        }
    }

}

//public class LockFreeBST <T extends Comparable> implements BSTInterface<T> {
//    AtomicStampedReference<Node<T>> root;
//
//    public void LockFreeBST(){
//        root = new AtomicStampedReference<Node<T>>(null, 0);
//    }
//
//    @Override
//    public boolean search(T val) {
//        if(root == null){
//            return false;
//        }
//
//        Node cur = root;
//
//        //loop until a leaf is reached
//        while(cur.left != null){
//            if(val.compareTo(cur.val) < 0){
//                cur = (Node) cur.left.getReference();
//            }else{
//                cur = (Node) cur.right.getReference();
//            }
//        }
//
//        if(val.compareTo(cur.val) == 0){
//            return true;
//        }else{
//            return false;
//        }
//
//    }
//
//    @Override
//    public boolean insert(T val) {
//        if(root == null){
//            root =  new Node(val);
//        }
//        Node cur = root;
//        Node prev = root;
//        boolean isLeft = false;
//
//        while(true){
//            while(cur.left != null){
//                //loop until a leaf node is reached
//                if(val.compareTo(cur.val) < 0){
//                    prev = cur;
//                    cur = (Node) cur.left.getReference();
//                }else{
//                    prev = cur;
//                    cur = (Node) cur.right.getReference();
//                }
//            }
//
//            Node oldChild = cur;
//
//            if (val.compareTo(prev.val) < 0){
//                isLeft = true;
//            }
//
//            //key is already present in tree, return false
//            if(val.compareTo(cur.val) == 0){
//                return false;
//            }
//
//            Node internalNode, lLeafNode,rLeafNode;
//            if(val.compareTo(cur.val)>0){
//                rLeafNode = new Node(val);
//                internalNode = new Node(cur.val, new AtomicStampedReference<Node>(cur,0),new AtomicStampedReference<Node>(rLeafNode,0));
//            }
//            else{
//                lLeafNode = new Node(val);
//                internalNode = new Node(cur.val, new AtomicStampedReference<Node>(lLeafNode,0),new AtomicStampedReference<Node>(cur,0));
//            }
//
//            if(isLeft){
//                //if(lUpdate.compareAndSet(pnode, oldChild, cr))
//                if(prev.left.compareAndSet(oldChild, internalNode, 0, 0))
//                {
//                    //System.out.println("I3 " + insertKey);
//                    return true;
//                }
//                else
//                {
//                    //insert failed; help the conflicting delete operation
//                    insert(val);
//                }
//            }else{
//                //if(rUpdate.compareAndSet(pnode, oldChild, cr))
//                 if(prev.right.compareAndSet(oldChild, internalNode, 0, 0))
//                {
//                    //System.out.println("I4 " + insertKey);
//                    return true;
//                }
//                else
//                {
//                    //insert failed; help the conflicting delete operation
//                    insert(val);
//                }
//            }
//        }
//
////        return false;
//    }
//
//    @Override
//    public boolean delete(T val) {
//        return false;
//    }
//
//    public void printNode() {
//        printhelper(root);
////        int maxLevel = maxLevel(root);
////        printNodeInternal(Collections.singletonList(root), 1, maxLevel);
//    }
//
//
//
//    public void printhelper(Node node){
//        if(node == null)
//            return;
//        System.out.println(node.val+" ");
//        printhelper((Node) node.left.getReference());
//        printhelper((Node) node.right.getReference());
//    }
//
//    @Override
//    public int getHeight() {
//        return maxLevel(root);
//    }
//
//    @Override
//    public int getNum() {
//        return 0;
//    }
//
//
//
//    public int getNumhelper(Node node){
//        if(node == null){
//            return 0;
//        }
//        return 1 + getNumhelper((Node) node.left.getReference()) + getNumhelper((Node) node.right.getReference());
//    }
//
//    private void printNodeInternal(List<Node> nodes, int level, int maxLevel) {
//        if (nodes.isEmpty() || isAllElementsNull(nodes))
//            return;
//
//        int floor = maxLevel - level;
//        int endgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
//        int firstSpaces = (int) Math.pow(2, (floor)) - 1;
//        int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;
//
//        printWhitespaces(firstSpaces);
//
//        List<Node> newNodes = new ArrayList<>();
//        for (Node<T> node : nodes) {
//            if (node != null && node.val != null) {
//                System.out.print(node.val);
//                newNodes.add(node.left.getReference());
//                newNodes.add(node.right.getReference());
//            } else {
//                newNodes.add(null);
//                newNodes.add(null);
//                System.out.print(" ");
//            }
//
//            printWhitespaces(betweenSpaces);
//        }
//        System.out.println("");
//
//        for (int i = 1; i <= endgeLines; i++) {
//            for (int j = 0; j < nodes.size(); j++) {
//                printWhitespaces(firstSpaces - i);
//                if (nodes.get(j) == null) {
//                    printWhitespaces(endgeLines + endgeLines + i + 1);
//                    continue;
//                }
//
//                if (nodes.get(j).left != null && nodes.get(j).left != null)
//                    System.out.print("/");
//                else
//                    printWhitespaces(1);
//
//                printWhitespaces(i + i - 1);
//
//                if (nodes.get(j).right != null && nodes.get(j).right != null)
//                    System.out.print("\\");
//                else
//                    printWhitespaces(1);
//
//                printWhitespaces(endgeLines + endgeLines - i);
//            }
//
//            System.out.println("");
//        }
//
//        printNodeInternal(newNodes, level + 1, maxLevel);
//    }
//
//    private void printWhitespaces(int count) {
//        for (int i = 0; i < count; i++)
//            System.out.print(" ");
//    }
//
//    private int maxLevel(Node<T> node) {
//        if (node == null)
//            return 0;
//        return Math.max(maxLevel(node.left.getReference()), maxLevel(node.right.getReference())) + 1;
//    }
//
//
//
//    private <T> boolean isAllElementsNull(List<T> list) {
//        for (Object object : list) {
//            if (object != null)
//                return false;
//        }
//
//        return true;
//    }
//}
