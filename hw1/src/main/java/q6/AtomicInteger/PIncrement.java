package q6.AtomicInteger;

import java.util.concurrent.atomic.AtomicInteger;

public class PIncrement implements Runnable{

    public static AtomicInteger sharedVariable;
    public static int numThreads;
    public final static int target = 1200000;

    public static int parallelIncrement(int c, int numThreads){
        sharedVariable = new AtomicInteger(c);
        PIncrement.numThreads = numThreads;

        Thread[] threads = new Thread[numThreads];
        // distribute task to each thread
        int[] tasks = new int[numThreads];
        int base = c / numThreads;
        for (int i = 0; i < numThreads-1; i++) {
            tasks[i] = base;
        }
        tasks[numThreads - 1] = c - (base * (numThreads - 1));

        for(int i = 0; i < numThreads; i++){
            threads[i] = new Thread(new PIncrement());
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
        for (int i = 0; i < numThreads; ++i) {
            while(true){
                int currentValue = sharedVariable.get();
                if(sharedVariable.compareAndSet(currentValue, currentValue+1)){
                    break;
                }
            }
        }
    }
}
