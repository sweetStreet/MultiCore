package q6.ReentrantLock;

import java.util.concurrent.locks.ReentrantLock;

public class PIncrement implements Runnable{
    public static int sharedVariable;
    public final static int target = 1200000;
    public static int[] tasks;
    public int pid;
    public ReentrantLock re;

    public PIncrement(int pid, ReentrantLock re){
        this.pid = pid;
        this.re = re;
    }

    public static int parallelIncrement(int c, int numThreads) {
        sharedVariable = c;

        // distribute task to each thread
        tasks = new int[numThreads];
        int base = target / numThreads;
        int left = target % numThreads;

        for (int i = 0; i < numThreads; i++) {
            tasks[i] = base;
            if (i < left) {
                tasks[i]++;
            }
        }

        // create and start the threads
        ReentrantLock lock = new ReentrantLock();
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(new PIncrement(i, lock));
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return sharedVariable;
    }

    @Override
    public void run(){
        for (int i = 0; i < tasks[pid]; ++i) {
            re.lock();
            try {
                sharedVariable = sharedVariable + 1;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                re.unlock();
            }
        }
    }
}
