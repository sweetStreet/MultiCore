package finegrained;

import bst.BSTInterface;

public class FineGrainedBST<T extends Comparable> implements BSTInterface<T> {
    TreeNode<T> root;

    public boolean search(T val) {
        if(root == null){
            return false;
        }
        TreeNode current = root;
        while(current!=null){
            if(current.val.compareTo(val) == 0){
                return true;
            }else {
                if (current.val.compareTo(val) > 0) {
                    current = current.left;
                } else {
                    current = current.right;
                }
            }
        }
        return false;
    }

    public boolean insert(T val) {
        TreeNode newNode = new TreeNode(val);
        if(root==null){
            root = newNode;
            return true;
        }
        TreeNode current = root;
        current.lock.lock();
        while(true){
            if(val.compareTo(current.val)==0){
                current.lock.unlock();
                return false;
            }
            if(val.compareTo(current.val)<0){
                if(current.left==null){
                    current.left = newNode;
                    newNode.parent = current;
                    current.lock.unlock();
                    return true;
                }
                current = current.left;
                current.parent.lock.unlock();
                current.lock.lock();
            }else{
                if(current.right==null){
                    current.right = newNode;
                    newNode.parent = current;
                    current.lock.unlock();
                    return true;
                }
                current = current.right;
                current.parent.lock.unlock();
                current.lock.lock();
            }
        }
    }

    public boolean delete(T val) {
        if(root == null){
            return false;
        }
        TreeNode current = root;
        boolean isLeftChild = false;

        current.setLock();
        while(current.val != val){
            if(current.val.compareTo(val) > 0){
                isLeftChild = true;
                if(current.left == null){
                    current.unlock();
                    return false;
                }
                current = current.left;
            }else{
                isLeftChild = false;
                if(current.right == null){
                    current.unlock();
                    return false;
                }
                current = current.right;
            }
            current.parent.parent.unlock();
            current.setLock();
        }

        //case 1: node to be deleted has no children
        if(current.left == null && current.right == null){
            if(isLeftChild){
                current.parent.left = null;
            }else{
                current.parent.right = null;
            }
        }

        //case 2: if the node to be deleted has only one child
        else if(current.right == null){
            if(current==root){
                root = current.left;
            }else if(isLeftChild){
                current.parent.left = current.left;
            }else{
                current.parent.right = current.left;
            }
        }else if(current.left == null){
            if(current==root){
                root = current.right;
            }else if(isLeftChild){
                current.parent.left = current.right;
            }else{
                current.parent.right = current.right;
            }
        }

        //case 3: the node has 2 children
        else if(current.left != null && current.right != null){
            //now we have found the minimum element in the right sub tree
            TreeNode successor	 = getSuccessor(current);
            successor.setLock();
            if(current==root){
                root = successor;
            }else if(isLeftChild){
                current.parent.left = successor;
            }else{
                current.parent.right = successor;
            }
            successor.left = current.left;
            successor.unlock();
        }

//        //case 1: node to be deleted has no children
//        if(current.left == null && current.right == null){
//            if((current.val.compareTo(root.val))==0) {
//                current.unlock();
//                root = null;
//                return true;
//            }
//            if(isLeftChild){
//                current.parent.left = current.left;
//            }else{
//                current.parent.right = current.right;
//            }
//        }
//
//        //case 2: if the node to be deleted has only one child
//        else if(current.right == null){
//            if(current==root){
//                root = current.left;
//            }else if(isLeftChild){
//                current.parent.left = current.left;
//            }else{
//                current.parent.right = current.left;
//            }
//        }else if(current.left == null){
//            if(current==root){
//                root = current.right;
//            }else if(isLeftChild){
//                current.parent.left = current.right;
//            }else{
//                current.parent.right = current.right;
//            }
//        }
//
//        //case 3: the node has 2 children
//        else if(current.left != null && current.right != null){
//            //now we have found the minimum element in the right sub tree
//            TreeNode successor	 = getSuccessor(current);
//            successor.setLock();
//            if(current==root){
//                root = successor;
//            }else if(isLeftChild){
//                current.parent.left = successor;
//            }else{
//                current.parent.right = successor;
//            }
//            successor.left = current.left;
//            successor.unlock();
//        }
//        current.unlock();
        return true;
    }


    public TreeNode getSuccessor(TreeNode deleteNode){
        TreeNode successsor =null;
        TreeNode successsorParent =null;
        TreeNode current = deleteNode.right;
        while(current!=null){
            successsorParent = successsor;
            successsor = current;
            current = current.left;
        }
        //check if successor has the right child, it cannot have left child for sure
        // if it does have the right child, add it to the left of successorParent.
//		successsorParent
        if(successsor!=deleteNode.right){
            successsorParent.left = successsor.right;
            successsor.right = deleteNode.right;
        }
        return successsor;
    }
}
