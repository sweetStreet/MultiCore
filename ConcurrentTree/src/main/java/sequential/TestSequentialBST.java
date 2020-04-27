package sequential;

import bst.BSTInterface;
import org.junit.Test;
import java.util.Random;

public class TestSequentialBST {
    @Test
    public void test2(){
        BSTInterface tree = new SequentialBST();
        tree.insert(1);
        for(int i = 0; i<1; i++)
            System.out.println( tree.delete(1) );
    }
    public void test1(){
        BSTInterface tree = new SequentialBST();
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
            tree.delete(n);
//            System.out.println(n + " " + tree.remove(n));
        }

        for(int i = 0; i<100; i++) {
//            Random rand = new Random();
//            int n = rand.nextInt(100);
            System.out.println(i+" "+tree.search(i));
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
