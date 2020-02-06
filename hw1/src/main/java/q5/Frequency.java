package q5;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Frequency implements Callable<Integer> {
    private int left;    //left index
    private int right;   //right index
    private int[] nums;  //nums array
    private int target;  //target number

    public Frequency(int l, int r, int[] n, int t) {
        left = l;
        right = r;
        nums = n;
        target = t;
    }

    public static int parallelFreq(int x, int[] A, int numThreads) {
        //invalid input
        if (A == null || A.length == 0) return -1;

        //update number of threads
        numThreads = Math.min(A.length, numThreads);
        //create thread pool
        ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
        //create list of Futures
        List<Future<Integer>> futures = new ArrayList<>(numThreads);
        //create list of tasks
        List<Frequency> tasks = new ArrayList<>(numThreads);

        //Assign tasks by splitting the array
        splitArray(x, A, numThreads, tasks);

        //submit the tasks, multi-thread task starts
        for (int i = 0; i < numThreads; i++) {
            futures.add(threadPool.submit(tasks.get(i)));
        }

        //compute the result
        int res = 0;
        for (Future<Integer> f : futures) {
            try {
                res += f.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        threadPool.shutdown();
        return res;
    }

    public static void splitArray(int x, int[] A, int numThreads, List<Frequency> tasks) {
        int left = 0;
        int remainingLength = A.length;
        int remainingThreads = numThreads;
        for (int i = 0; i < numThreads; i++) {
            //calculate length of subarray
            int length = remainingLength / remainingThreads;

            //add a new task
            tasks.add(new Frequency(left, left + length - 1, A, x));
            //update parameters
            left += length;
            remainingLength -= length;
            remainingThreads--;
        }
    }




    @Override
    public Integer call() throws Exception {
        try {
            int count = 0;
            for (int i = left; i <= right; i++) {
                if (nums[i] == target) count++;
            }
            return count;
        } catch (Exception e) {
            System.err.println(e);
            return -1;
        }
    }
}
