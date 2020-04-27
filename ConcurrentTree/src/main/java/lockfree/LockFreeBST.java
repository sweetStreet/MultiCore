package lockfree;

import bst.BSTInterface;

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
            while(cur.left != null || cur.right != null){
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
                internalNode = new Node(val, new AtomicStampedReference<Node>(cur,0),new AtomicStampedReference<Node>(rLeafNode,0));
            }
            else{
                lLeafNode = new Node(val);
                internalNode = new Node(cur.val, new AtomicStampedReference<Node>(lLeafNode,0),new AtomicStampedReference<Node>(cur,0));
            }

            if(isLeft){
                //if(lUpdate.compareAndSet(pnode, oldChild, cr))
                while(prev.left.compareAndSet(oldChild, internalNode, 0, 0))
                {
                    //System.out.println("I3 " + insertKey);
                    return true;
                }
//                else
//                {
//                    //insert failed; help the conflicting delete operation
//                    if(cur == prev.left.getReference()) // address has not changed. So CAS would have failed coz of flag/mark only
//                    {
//                        //help other thread with cleanup
//                        s = seek(insertKey);
//                        cleanUp(insertKey,s);
//                    }
//                }
            }else{
                //if(rUpdate.compareAndSet(pnode, oldChild, cr))
                 while(prev.right.compareAndSet(oldChild, internalNode, 0, 0))
                {
                    //System.out.println("I4 " + insertKey);
                    return true;
                }
//                else
//                {
//                    //insert failed; help the conflicting delete operation
//                    if(cur== prev.right.getReference()) // address has not changed. So CAS would have failed coz of flag/mark only
//                    {
//                        //help other thread with cleanup
//                        s = seek(insertKey);
//                        cleanUp(insertKey,s);
//                    }
//                }
            }
        }

//        return false;
    }

    @Override
    public boolean delete(T val) {
        return false;
    }
}
