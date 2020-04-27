package compared;

import java.util.concurrent.Semaphore;

public class LockNode 
{
	int key;
	LockNode left;
	LockNode right;
	Semaphore lock;
	
	// constructor
	public LockNode(int val)
	{
		this.key = val;
		left = null;
		right = null;
		lock = new Semaphore(1);
	}
}
