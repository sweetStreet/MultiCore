package finegrained;

import bst.BST;

public class FineGrainedBST<T extends Comparable> implements BST<T> {
    TreeNode<T> root;


    public boolean contains(T val) {
        TreeNode current = root;
        while(current!=null){
            if(current.val.compareTo(val) == 0){
                current.unlock();
                return true;
            }else {
                current.parent.unlock();
                if (current.val.compareTo(val) > 0) {
                    current = current.left;
                } else {
                    current = current.right;
                }
                current.setLock();
            }
        }
        current.unlock();
        return false;
    }

    public boolean insert(T val) {
        //todo
        TreeNode newNode = new TreeNode(val);
        if(root==null){
            root = newNode;
            return true;
        }
        TreeNode current = root;
        TreeNode parent = current;
        parent.setLock();
        while(true){
            if(val.compareTo(current.val)==0){
                parent.unlock();
                return false;
            }
            if(val.compareTo(current.val)<0){
                current = current.left;
                current.setLock();

                if(current==null){
                    parent.left = newNode;
                    newNode.parent = parent;

                    parent.unlock();
                    current.unlock();
                    return true;
                }
            }else{
                current = current.right;
                current.setLock();

                if(current==null){
                    parent.right = newNode;
                    newNode.parent = parent;
                    return true;
                }
            }
            parent.unlock();
            parent = current;
        }
    }

    public boolean remove(T val) {
        TreeNode parent = root;
        TreeNode current = root;
        boolean isLeftChild = false;
        while(current.val != val){
            parent = current;
            if(current.val.compareTo(val) > 0){
                isLeftChild = true;
                current = current.left;
            }else{
                isLeftChild = false;
                current = current.right;
            }
            if(current == null){
                return false;
            }
        }
        //case 1: node to be deleted has no children
        if(current.left == null && current.right == null){
            if(current == root) {
                root = null;
            }
            if(isLeftChild){
                parent.left = current.left;
            }else{
                parent.right = current.right;
            }
        }

        //case 2: if the node to be deleted has only one child
        else if(current.right == null){
            if(current==root){
                root = current.left;
            }else if(isLeftChild){
                parent.left = current.left;
            }else{
                parent.right = current.left;
            }
        }else if(current.left == null){
            if(current==root){
                root = current.right;
            }else if(isLeftChild){
                parent.left = current.right;
            }else{
                parent.right = current.right;
            }
        }

        //case 3: the node has 2 children
        else if(current.left != null && current.right != null){
            //now we have found the minimum element in the right sub tree
            TreeNode successor	 = getSuccessor(current);
            if(current==root){
                root = successor;
            }else if(isLeftChild){
                parent.left = successor;
            }else{
                parent.right = successor;
            }
            successor.left = current.left;
        }
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
