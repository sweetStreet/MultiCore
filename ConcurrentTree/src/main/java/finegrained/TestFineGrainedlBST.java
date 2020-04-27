package finegrained;

import bst.BSTInterface;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TestFineGrainedlBST{
    int totalnum = 10000;
    int[] threadnums = {1, 2, 4, 8};
//    int[] threadnums = {4};
    List times;

//    @Test
    public void test(){
        BSTInterface bst = new FineGrainedBST();
        makeThread(4, bst, 0 ,20);
    }

    @Test
    public void test1(){
        //inset node in sequence, no duplicate
        times = new ArrayList<Long>();
        for(int i = 0; i<threadnums.length; i++){
            BSTInterface tree = new FineGrainedBST();
            makeThread1(tree, threadnums[i]);
//            tree.printNode();
            System.out.println( "When there are "+threadnums[i] + "threads, there are "+tree.getNum() + " nodes");

            long startTime=System.currentTimeMillis();
            for(int k = 0; k<100; k++)
                tree.search(totalnum/2);
            long endTime=System.currentTimeMillis();
            System.out.println( "When there are "+threadnums[i] + "threads, the search takes "+ (endTime-startTime) + " ms");

        }
        for(int i = 0; i<threadnums.length; i++){
            System.out.println("When there are "+threadnums[i]+" threads, "+"it takes "+times.get(i)+" ms");
        }

    }

    @Test
    public void test2(){
        //each thread insert the same number
        times = new ArrayList<Long>();
        for(int i = 0; i<threadnums.length; i++){
            BSTInterface tree = new FineGrainedBST();
            makeThread2(tree, threadnums[i]);
            System.out.println( "When there are "+threadnums[i] + "threads, there are "+tree.getNum() + " nodes");

            long startTime=System.currentTimeMillis();
            for(int k = 0; k<100; k++)
                tree.search(totalnum/2);
            long endTime=System.currentTimeMillis();
            System.out.println( "When there are "+threadnums[i] + "threads, the search takes "+ (endTime-startTime) + " ms");
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
            BSTInterface tree = new FineGrainedBST();
            makeThread3(tree, threadnums[i]);
            System.out.println( "When there are "+threadnums[i] + "threads, there are "+tree.getNum() + " nodes");

            long startTime=System.currentTimeMillis();
            for(int k = 0; k<100; k++)
                tree.search(totalnum/2);
            long endTime=System.currentTimeMillis();
            System.out.println( "When there are "+threadnums[i] + "threads, the search takes "+ (endTime-startTime) + " ms");
        }
        for(int i = 0; i<threadnums.length; i++){
            System.out.println("When there are "+threadnums[i]+" threads, "+"it takes "+times.get(i)+" ms");
        }
    }

    private void makeThread(int threadnum, BSTInterface bst, int begin, int end) {
        Thread[] threads = new Thread[threadnum];

        for(int i = 0; i<threadnum; i++){
            threads[i] = new Thread(new MyThread(begin, end, bst));
            threads[i].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private class MyThread implements Runnable {
        int begin;
        int end;
        BSTInterface bstInterface;

        MyThread(int begin, int end, BSTInterface bstInterface) {
            this.begin = begin;
            this.end = end;
            this.bstInterface = bstInterface;
        }

        public void run() {
            for(int i = begin; i<=end; i++){
                bstInterface.insert(i);
//                System.out.println(i);
            }

            for(int i = begin; i<=end; i++){
                bstInterface.delete(i);
            }
            for(int i = begin; i<=end; i++){
                bstInterface.search(i);
            }
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
            threads[i] = new Thread(new MyThread1(0, totalnum-1, bstInterface));
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
            for(int i = begin; i<=end && i<list.size(); i++){
                bstInterface.insert((Comparable) list.get(i));
            }
        }
    }
}
