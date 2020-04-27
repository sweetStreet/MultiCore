package compared;

import java.util.concurrent.TimeUnit;

public class Executor
{
	
	static class LockBasedThread extends Thread 
	{
	    LockBasedBST tree;
	    int numIterations;
	    String threadName;
	    
	    public LockBasedThread(LockBasedBST tree, int numIterations, String threadName)  
	    {
	        this.tree = tree;
	        this.numIterations = numIterations;
	        this.threadName = threadName; 
	    }
	  
	    @Override
	    public void run() 
	    {
            System.out.println("Starting " + threadName);
            if (threadName.equals("A"))
            {
            	for (int i=0; i<numIterations; i++)
            	{
            		tree.insert(i+1);
            		System.out.println("Attempt to Insert " + Integer.toString(i+1));
            	}
            	System.out.println("Over Thread A");
            }
            else if (threadName.equals("B"))
            {
            	for (int i=0; i<numIterations; i++)
            	{
            		tree.search(i+1);
            	}
            	System.out.println("Over Thread B");
            }
            else
            {
            	for (int i=0; i<numIterations; i++)
            	{
            		tree.delete(i+1);
            		System.out.println("Attempt to Delete " + Integer.toString(i+1));
            	}
            }
            return;
	    }
	}
	
	static class LockFreeThread extends Thread 
	{
	    LockFreeBST tree;
	    int numIterations;
	    String threadName;
	    
	    public LockFreeThread(LockFreeBST tree, int numIterations, String threadName)  
	    {
	        this.tree = tree;
	        this.numIterations = numIterations;
	        this.threadName = threadName; 
	    }
	  
	    @Override
	    public void run() 
	    {
            System.out.println("Starting " + threadName);
            if (threadName.equals("A"))
            {
            	for (int i=0; i<numIterations; i++)
            	{
            		tree.insert(i+1);
            	}
            	System.out.println("Over Thread A");
            }
            else if (threadName.equals("B"))
            {
            	for (int i=0; i<numIterations; i++)
            	{
            		tree.search(i+1);
            	}
            	System.out.println("Over Thread B");
            }
            else
            {
            	for (int i=0; i<numIterations; i++)
            	{
            		tree.delete(i+1);
            	}
            }
            return;
	    }
	}
	
	public static void main(String[] args) throws InterruptedException
	{
//		SequentialBST tree = new SequentialBST();
//		
//		tree.insert(50);
//        tree.insert(30);
//        tree.insert(20);
//        tree.insert(40);
//        tree.insert(70);
//        tree.insert(60);
//        tree.insert(90);
//        tree.insert(80);
//        
//        tree.inorderTraversal();
//        System.out.println();
//        
//        tree.delete(30);
//        tree.inorderTraversal();
//        System.out.println();
//        
//        tree.delete(50);
//        tree.inorderTraversal();
//        System.out.println();
//        
//        LockBasedBST tree1 = new LockBasedBST();
//        
//        tree1.insert(3);
//        tree1.insert(1);
//        tree1.insert(5);
//        tree1.insert(4);
//        tree1.insert(20);
//        tree1.insert(25);
		
		LockBasedBST tree1 = new LockBasedBST();
		LockFreeBST tree2 = new LockFreeBST();
		
//		tree2.insert(3);
//		tree2.insert(1);
//		tree2.insert(5);
//		tree2.insert(4);
//		tree2.insert(20);
//		tree2.insert(25);
//		
//		tree2.inorderTraversal();
		
        int n = 1000;
        
        // Initiating the threads
//        LockFreeThread mt1 = new LockFreeThread(tree2, n, "A");
//        LockFreeThread mt2 = new LockFreeThread(tree2, n, "B");
//        LockFreeThread mt3 = new LockFreeThread(tree2, n, "C");
//        
        // Initiating the threads
        LockBasedThread mt1 = new LockBasedThread(tree1, n, "A");
        LockBasedThread mt2 = new LockBasedThread(tree1, n, "B");
        LockBasedThread mt3 = new LockBasedThread(tree1, n, "C");
          
        // stating threads A,B and C
        mt1.start();
        mt2.start();
        mt3.start();
        
        long startTime = System.nanoTime();
        // waiting for threads A and B  
        mt1.join();
        mt2.join();
        mt3.join();
        long endTime = System.nanoTime();
        
        long durationNanoSec = endTime - startTime;
        long durationMilliSec = TimeUnit.NANOSECONDS.toMillis(durationNanoSec);
        
        tree2.inorderTraversal();
        System.out.println(durationMilliSec);
        
	}
}