package sequential;

import bst.BSTInterface;

public class SequentialBST<T extends Comparable> implements BSTInterface<T> {
    Node root;

    public SequentialBST(){
        this.root = null;
    }
    public boolean search(T val) {
        Node current = root;
        while(current!=null){
            if(current.data.compareTo(val) == 0){
                return true;
            }else if(current.data.compareTo(val)>0){
                current = current.left;
            }else{
                current = current.right;
            }
        }
        return false;
    }


    public boolean insert(T val) {
        Node newNode = new Node(val);
        if(root==null){
            root = newNode;
            return true;
        }
        Node current = root;
        Node parent = null;
        while(true){
            parent = current;
            if(val.compareTo(current.data)==0){
                return false;
            }
            if(val.compareTo(current.data)<0){
                current = current.left;
                if(current==null){
                    parent.left = newNode;
                    return true;
                }
            }else{
                current = current.right;
                if(current==null){
                    parent.right = newNode;
                    return true;
                }
            }
        }
    }

    public boolean delete(T val) {
        Node parent = root;
        Node current = root;
        boolean isLeftChild = false;
        while(current.data != val){
            parent = current;
            if(current.data.compareTo(val) > 0){
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
            Node successor	 = getSuccessor(current);
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

    public Node getSuccessor(Node deleteNode){
        Node successsor =null;
        Node successsorParent =null;
        Node current = deleteNode.right;
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
