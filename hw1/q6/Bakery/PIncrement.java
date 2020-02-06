package q6.Bakery;

public class PIncrement implements Runnable{

    private static int numThreads;
    private static BakeryLock bakeryLock;
    private static int c;

    public static int parallelIncrement(int c, int numThreads){
        // your implementation goes here
        PIncrement.numThreads = numThreads;
        PIncrement.bakeryLock = bakeryLock;
        PIncrement.c = c;
        return c;
    }

    @Override
    public void run() {
        for(int i = 0; i<numThreads; i++){
            bakeryLock.lock(i);
            c = c+1;
            bakeryLock.unlock(i);
        }
    }
}
