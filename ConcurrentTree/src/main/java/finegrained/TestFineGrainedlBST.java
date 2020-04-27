package finegrained;

import bst.BSTInterface;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TestFineGrainedlBST{
    int totalnum = 10000;
    int[] threadnums = {1, 2, 4, 8};
    List times;

    @Test
    public void test(){
        TreeNode node = new TreeNode(1);
        makeThread(8, node);
    }

    private void makeThread(int threadnum, TreeNode node) {
        Thread[] threads = new Thread[threadnum];

        for(int i = 0; i<threadnum; i++){
            threads[i] = new Thread(new MyThread(node));
            threads[i].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long endTime=System.currentTimeMillis();
    }

    private class MyThread implements Runnable {
        TreeNode node;

        MyThread(TreeNode node) {
            this.node = node;
        }

        public void run() {
            node.setLock();
            node.setLock();
            node.setLock();
            node.unlock();
            node.unlock();
        }
    }

//    @Test
    public void test1(){
        //inset node in sequence, no duplicate
        times = new ArrayList<Long>();
        for(int i = 0; i<threadnums.length; i++){
            BSTInterface tree = new FineGrainedBST();
            makeThread1(tree, threadnums[i]);
        }
        for(int i = 0; i<threadnums.length; i++){
            System.out.println("When there are "+threadnums[i]+" threads, "+"it takes "+times.get(i)+" ms");
        }
    }

//    @Test
    public void test2(){
        //inset node in sequence, with duplicate
        times = new ArrayList<Long>();
        for(int i = 0; i<threadnums.length; i++){
            BSTInterface tree = new FineGrainedBST();
            makeThread2(tree, threadnums[i]);
        }
        for(int i = 0; i<threadnums.length; i++){
            System.out.println("When there are "+threadnums[i]+" threads, "+"it takes "+times.get(i)+" ms");
        }
    }

//    @Test
    public void test3(){
        //random generate
        times = new ArrayList<Long>();
        for(int i = 0; i<threadnums.length; i++){
            BSTInterface tree = new FineGrainedBST();
            makeThread3(tree, threadnums[i]);
        }
        for(int i = 0; i<threadnums.length; i++){
            System.out.println("When there are "+threadnums[i]+" threads, "+"it takes "+times.get(i)+" ms");
        }
    }

    private void makeThread1(BSTInterface bstInterface, int threadnum) {
        Thread[] threads = new Thread[threadnum];
        long startTime=System.currentTimeMillis();
        int eachthread = totalnum/threadnum;
        for(int i = 0; i<threadnum; i++){
            threads[i] = new Thread(new MyThread1(i*eachthread, (i+1)*eachthread-1, bstInterface));
            threads[i].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long endTime=System.currentTimeMillis();
        times.add(endTime-startTime);
    }

    private class MyThread1 implements Runnable {
        int begin;
        int end;
        BSTInterface bstInterface;

        MyThread1(int begin, int end, BSTInterface bstInterface) {
            this.begin = begin;
            this.end = end;
            this.bstInterface = bstInterface;
        }

        public void run() {
            for(int i = begin; i<=end; i++){
                bstInterface.insert(i);
//                System.out.println(i);
            }
        }
    }

    private void makeThread2(BSTInterface bstInterface, int threadnum) {
        Thread[] threads = new Thread[threadnum];
        long startTime=System.currentTimeMillis();
        int eachthread = totalnum/threadnum;
        for(int i = 0; i<threadnum; i++){
            threads[i] = new Thread(new MyThread1(i*eachthread, (i+2)*eachthread, bstInterface));
            threads[i].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long endTime=System.currentTimeMillis();
        times.add(endTime-startTime);
    }

    private void makeThread3(BSTInterface bstInterface, int threadnum) {
        Thread[] threads = new Thread[threadnum];
        long startTime=System.currentTimeMillis();
        List list = new ArrayList();
        for(int i = 0; i<totalnum; i++){
            list.add(i);
        }
        Collections.shuffle(list);
        int eachthread = totalnum/threadnum;
        for(int i = 0; i<threadnum; i++){
            threads[i] = new Thread(new MyThread3(i*eachthread, (i+2)*eachthread, list, bstInterface));
            threads[i].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long endTime=System.currentTimeMillis();
        times.add(endTime-startTime);
    }

    private class MyThread3 implements Runnable {
        List list;
        int begin;
        int end;
        BSTInterface bstInterface;

        MyThread3(int begin, int end, List list, BSTInterface bstInterface) {
            this.begin = begin;
            this.end = end;
            this.list = list;
            this.bstInterface = bstInterface;
        }

        public void run() {
            for(int i = begin; i<=end; i++){
                bstInterface.insert(i);
            }
        }
    }
}
