package finegrained;

import bst.BSTInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        TreeNode prev;
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
                prev = current;
                current = current.left;
            }else{
                if(current.right==null){
                    current.right = newNode;
                    newNode.parent = current;
                    current.lock.unlock();
                    return true;
                }
                prev = current;
                current = current.right;
            }
            current.lock.lock();
            prev.unlock();
        }
    }

    public boolean delete(T val) {
        if(root == null){
            return false;
        }
        TreeNode current = root;
        TreeNode prev;
        boolean isLeftChild = false;

        current.setLock();
        while(current.val != val){
            if(current.val.compareTo(val) > 0){
                isLeftChild = true;
                if(current.left == null){
                    current.unlock();
                    return false;
                }
                prev = current;
                current = current.left;
            }else{
                isLeftChild = false;
                if(current.right == null){
                    current.unlock();
                    return false;
                }
                prev = current;
                current = current.right;
            }
            current.setLock();
            prev.unlock();
        }

        //case 1: node to be deleted has no children
        if(current.left == null && current.right == null){
            if(isLeftChild){
                current.parent.left = null;
            }else{
                current.parent.right = null;
            }
            current.unlock();
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
            current.unlock();
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
            current.unlock();
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

    public void printNode() {
//        printhelper(root);
        int maxLevel = maxLevel(root);
        printNodeInternal(Collections.singletonList(root), 1, maxLevel);
    }

    public void printhelper(TreeNode node){
        if(node == null) {
            System.out.print("null"+" ");
            return;
        }
        System.out.print(node.val+" ");
        printhelper(node.left);
        printhelper(node.right);
    }

    @Override
    public int getHeight() {
        return maxLevel(root);
    }

    @Override
    public int getNum() {
        return getNumhelper(root);
    }

    public int getNumhelper(TreeNode node){
        if(node == null){
            return 0;
        }
        return 1 + getNumhelper(node.left) + getNumhelper(node.right);
    }


    private void printNodeInternal(List<TreeNode> nodes, int level, int maxLevel) {
        if (nodes.isEmpty() || isAllElementsNull(nodes))
            return;

        int floor = maxLevel - level;
        int endgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
        int firstSpaces = (int) Math.pow(2, (floor)) - 1;
        int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;

        printWhitespaces(firstSpaces);

        List<TreeNode> newNodes = new ArrayList<>();
        for (TreeNode<T> node : nodes) {
            if (node != null && node.val != null) {
                System.out.print(node.val);
                newNodes.add(node.left);
                newNodes.add(node.right);
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

                if (nodes.get(j).left == null)
                    printWhitespaces(1);
//                else
//                    System.out.print("/");

                printWhitespaces(i + i - 1);

                if (nodes.get(j).right == null)
                    printWhitespaces(1);
//                else
//                    System.out.print("\\");

                printWhitespaces(endgeLines + endgeLines - i);
            }
        }
        System.out.println("");
        printNodeInternal(newNodes, level + 1, maxLevel);
    }

    private void printWhitespaces(int count) {
        for (int i = 0; i < count; i++)
            System.out.print(" ");
    }

    private int maxLevel(TreeNode<T> node) {
        if (node == null)
            return 0;
        return Math.max(maxLevel(node.left), maxLevel(node.right)) + 1;
    }


    private <T> boolean isAllElementsNull(List<T> list) {
        for (Object object : list) {
            if (object != null)
                return false;
        }

        return true;
    }
}
