package q6.Synchronized;

import java.util.concurrent.atomic.AtomicInteger;

public class PIncrement implements Runnable{

    public static int sharedVariable;
    public final static int target = 1200000;
    public static int[] tasks;
    public int pid;

    public PIncrement(int pid){
        this.pid = pid;
    }

    public static int parallelIncrement(int c, int numThreads){
        sharedVariable = c;

        // distribute task to each thread
        tasks = new int[numThreads];
        int base = target / numThreads;
        int left = target % numThreads;

        for (int i = 0; i < numThreads; i++) {
            tasks[i] = base;
            if(i<left){
                tasks[i]++;
            }
        }

        // create and start the threads
        Thread[] threads = new Thread[numThreads];
        for(int i = 0; i < numThreads; i++){
            threads[i] = new Thread(new PIncrement(i));
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
    public void run() {
        synchronized (this){
            for (int i = 0; i < tasks[pid]; ++i) {
                sharedVariable = sharedVariable + 1;
            }
        }
    }
}
