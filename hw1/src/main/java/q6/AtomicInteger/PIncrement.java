package q6.AtomicInteger;

import java.util.concurrent.atomic.AtomicInteger;

public class PIncrement implements Runnable{

    public static AtomicInteger sharedVariable;
    public static int numThreads;
    public final static int target = 1200000;
    public static int[] tasks;
    public int pid;

    public PIncrement(int pid){
        this.pid = pid;
    }

    public static int parallelIncrement(int c, int numThreads){
        sharedVariable = new AtomicInteger(c);
        PIncrement.numThreads = numThreads;

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
        return sharedVariable.get();
    }

    @Override
    public void run() {
        for (int i = 0; i < tasks[pid]; ++i) {
            while(true){
                int currentValue = sharedVariable.get();
                if(sharedVariable.compareAndSet(currentValue, currentValue+1)){
                    break;
                }
            }
        }
    }
}
