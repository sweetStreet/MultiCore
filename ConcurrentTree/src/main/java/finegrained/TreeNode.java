package finegrained;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TreeNode<T extends Comparable>{
    T val;
    volatile TreeNode parent;
    volatile TreeNode left;
    volatile TreeNode right;
    Lock lock;

    public TreeNode(T val){
        this.val = val;
        lock = new ReentrantLock();
    }

    public void setLock(){
        try {
            if (lock != null)
                lock.lock();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void unlock(){
        try {
            if (lock != null)
                lock.unlock();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
