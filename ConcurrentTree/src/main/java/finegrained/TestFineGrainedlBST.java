package finegrained;

import bst.BST;
import org.junit.Test;

import java.util.Random;

public class TestFineGrainedlBST {
    @Test
    public void test1(){
        BST tree = new FineGrainedBST();
        makeThread(tree);
//        for(int i = 0; i<100; i++) {
//            System.out.println(tree.insert(i));
//        }
//        for(int i = 0; i<10; i++) {
//            Random rand = new Random();
//            int n = rand.nextInt(100);
//            System.out.println(n+" "+tree.contains(n));
//        }
//
//        for(int i = 0; i<10; i++) {
//            Random rand = new Random();
//            int n = rand.nextInt(100);
//            tree.remove(n);
//            System.out.println(n + " " + tree.remove(n));
//        }

        for(int i = 0; i<100; i++) {
//            Random rand = new Random();
//            int n = rand.nextInt(100);
            System.out.println(i+" "+tree.contains(i));
        }

//        System.out.println(((FineGrainedBST) tree).root.left.left.val);
//        System.out.println(((FineGrainedBST) tree).root.left.right.val);
        printTree(((FineGrainedBST) tree).root, "");
    }

    private void makeThread(BST bst) {
        Thread[] threads = new Thread[4];
        threads[0] = new Thread(new MyThread(20, 40, bst));
        threads[1] = new Thread(new MyThread(0, 20, bst));
        threads[2] = new Thread(new MyThread(60, 80, bst));
        threads[3] = new Thread(new MyThread(40, 60, bst));
        threads[1].start(); threads[0].start(); threads[2].start();threads[3].start();

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private int COUNT = 1;
    private void printTree(TreeNode node, String prefix)
    {
        if(node == null) return;

        System.out.println(prefix + " + " + node.val);
        printTree(node.left , prefix + " ");
        printTree(node.right , prefix + " ");
    }

    private void checkNode(int start, int end, BST bst) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i <= end; ++i) {
            sb.append(i).append(",");
        }
//        System.out.println(list.toString());
//        Assert.assertEquals(list.toString(), sb.toString());
    }

    private class MyThread implements Runnable {

        int begin;
        int end;
        BST bst;

        MyThread(int begin, int end, BST bst) {
            this.begin = begin;
            this.end = end;
            this.bst = bst;
        }

        @Override
        public void run() {
            int mid = (begin+end)/2;
            int left = begin;
            int right = end;
            while(left<=mid+1){
                bst.insert(left);
                bst.insert(right);
                left ++;
                right --;
            }
//            for (int i = begin; i <= end; ++i) {
//                bst.insert(i);
//            }
        }
    }
}
