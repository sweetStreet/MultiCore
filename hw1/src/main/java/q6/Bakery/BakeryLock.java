package q6.Bakery;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BakeryLock implements Lock {
    AtomicBoolean[] flag;
    AtomicInteger[] label;
    int n;

    public BakeryLock(int numThreads){
        this.n = numThreads;
        flag = new AtomicBoolean[n];
        label = new AtomicInteger[n];
        for(int i = 0; i<n; i++){
            flag[i] = new AtomicBoolean(false);
            label[i] = new AtomicInteger(0);
        }
    }

    @Override
    public void lock(int pid) {
        // doorway: choose a number
        flag[pid].set(true);
        int max = 0;
        for(int i = 0; i<n; i++){
            max = Math.max(max, label[i].get());
        }
        label[pid].set(max+1);
        flag[pid].set(true);

        // check if the my number is the smallest
        for(int i = 0; i<n; i++){
            if(i == pid) continue;
            //busy wait
            while(flag[i].get()){

            }
            while(label[i].get()!=0 && ((label[i].get()<label[pid].get()) || (label[i].get() == label[pid].get() && i < pid))){

            }
        }
    }

    @Override
    public void unlock(int pid) {
        label[pid].set(0);
    }
}
