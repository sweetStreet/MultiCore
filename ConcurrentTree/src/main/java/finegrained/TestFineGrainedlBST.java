package finegrained;

import bst.BST;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class TestFineGrainedlBST{
    int totalnum = 10000;
    int[] threadnums = {1, 2, 4, 8};
    List times;

    @Test
    public void test1(){
        //inset node in sequence, no duplicate
        times = new ArrayList<Long>();
        for(int i = 0; i<threadnums.length; i++){
            BST tree = new FineGrainedBST();
            makeThread1(tree, threadnums[i]);
        }
        for(int i = 0; i<threadnums.length; i++){
            System.out.println("When there are "+threadnums[i]+" threads, "+"it takes "+times.get(i)+" ms");
        }
    }

    @Test
    public void test2(){
        //inset node in sequence, with duplicate
        times = new ArrayList<Long>();
        for(int i = 0; i<threadnums.length; i++){
            BST tree = new FineGrainedBST();
            makeThread2(tree, threadnums[i]);
        }
        for(int i = 0; i<threadnums.length; i++){
            System.out.println("When there are "+threadnums[i]+" threads, "+"it takes "+times.get(i)+" ms");
        }
    }

    @Test
    public void test3(){
        //random generate
        times = new ArrayList<Long>();
        for(int i = 0; i<threadnums.length; i++){
            BST tree = new FineGrainedBST();
            makeThread3(tree, threadnums[i]);
        }
        for(int i = 0; i<threadnums.length; i++){
            System.out.println("When there are "+threadnums[i]+" threads, "+"it takes "+times.get(i)+" ms");
        }
    }

    private void makeThread1(BST bst, int threadnum) {
        Thread[] threads = new Thread[threadnum];
        long startTime=System.currentTimeMillis();
        int eachthread = totalnum/threadnum;
        for(int i = 0; i<threadnum; i++){
            threads[i] = new Thread(new MyThread1(i*eachthread, (i+1)*eachthread-1, bst));
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
        BST bst;

        MyThread1(int begin, int end, BST bst) {
            this.begin = begin;
            this.end = end;
            this.bst = bst;
        }

        public void run() {
            for(int i = begin; i<=end; i++){
                bst.insert(i);
//                System.out.println(i);
            }
        }
    }

    private void makeThread2(BST bst, int threadnum) {
        Thread[] threads = new Thread[threadnum];
        long startTime=System.currentTimeMillis();
        int eachthread = totalnum/threadnum;
        for(int i = 0; i<threadnum; i++){
            threads[i] = new Thread(new MyThread1(i*eachthread, (i+2)*eachthread, bst));
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

    private void makeThread3(BST bst, int threadnum) {
        Thread[] threads = new Thread[threadnum];
        long startTime=System.currentTimeMillis();
        List list = new ArrayList();
        for(int i = 0; i<totalnum; i++){
            list.add(i);
        }
        Collections.shuffle(list);
        int eachthread = totalnum/threadnum;
        for(int i = 0; i<threadnum; i++){
            threads[i] = new Thread(new MyThread3(i*eachthread, (i+2)*eachthread, list, bst));
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
        BST bst;

        MyThread3(int begin, int end, List list, BST bst) {
            this.begin = begin;
            this.end = end;
            this.list = list;
            this.bst = bst;
        }

        public void run() {
            for(int i = begin; i<=end; i++){
                bst.insert(i);
            }
        }
    }
}
