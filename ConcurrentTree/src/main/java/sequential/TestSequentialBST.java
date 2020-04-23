package sequential;

import bst.BST;
import org.junit.Test;
import java.util.Random;

public class TestSequentialBST {
    @Test
    public void test1(){
        BST tree = new SequentialBST();
        int begin = 0;
        int end = 1000;
        int mid = (begin+end)/2;
        int left = begin;
        int right = end;
        while(left<=mid+1){
            tree.insert(left);
            tree.insert(right);
            left ++;
            right --;
        }
        printTree( ((SequentialBST) tree).root, "");
//        for(int i = 0; i<100; i++) {
//            System.out.println(tree.insert(i));
//        }

        for(int i = 0; i<10; i++) {
            Random rand = new Random();
            int n = rand.nextInt(100);
//            System.out.println(n+" "+tree.contains(n));
        }

        for(int i = 0; i<10; i++) {
            Random rand = new Random();
            int n = rand.nextInt(100);
            tree.remove(n);
//            System.out.println(n + " " + tree.remove(n));
        }

        for(int i = 0; i<100; i++) {
//            Random rand = new Random();
//            int n = rand.nextInt(100);
            System.out.println(i+" "+tree.contains(i));
        }
    }

    private void printTree(Node node, String prefix)
    {
        if(node == null) return;
        System.out.println(prefix + " + " + node.data);
        printTree(node.left , prefix + " ");
        printTree(node.right , prefix + " ");
    }

}
