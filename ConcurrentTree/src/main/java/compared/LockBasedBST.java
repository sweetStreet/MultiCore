package compared;

import java.util.concurrent.Semaphore;

public class LockBasedBST 
{
	public LockNode root;
	private Semaphore treeLock;
	
	// constructor
	public LockBasedBST()
	{
		this.root = null;
		this.treeLock = new Semaphore(1);
	}
	
	private boolean searchRecursively(int val, LockNode root)
	{
		
		/*
		 *  1) Root should never be null in this module
		 *  2) Root as an argument to this function should already be locked before calling this module.
		 *  3) That is why we lock the root in the previous iteration itself.
		 */
		
		if(root.key == val)
		{
			root.lock.release();
			return true;
		}
		else if(val < root.key)
		{
			// no left subtree to search
			if (root.left == null)
			{
				root.lock.release();
				return false;
			}
			
			// search the left subtree recursively
			try
			{
				root.left.lock.acquire();
				root.lock.release();
				return searchRecursively(val, root.left);
			}
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			// no right subtree to search
			if (root.right == null)
			{
				root.lock.release();
				return false;
			}
			
			// search the right subtree recursively
			try
			{
				root.right.lock.acquire();
				root.lock.release();
				return searchRecursively(val, root.right);
			} 
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	public void search(int val)
	{
		boolean found = false;
		
		// First acquire the treeLock before deciding whether the root is null or not
		try
		{
			treeLock.acquire();
		}
		catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}
		
		if (root != null)
		{
			try
			{
				// First acquire the lock on the root
				root.lock.acquire();
				
				// Now release the lock on the treeLock
				treeLock.release();
				
				// Now search for the value in the tree
				found = searchRecursively(val, root);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			treeLock.release();
		
		if(found)
			System.out.println("Node with value " + Integer.toString(val) + " is present in BST");
		else
			System.out.println("Node with value " + Integer.toString(val) + " is not present in BST");
	}
	
	
	private void insertRecursively(LockNode root, int val)
	{
		/*
		 * This function inserts the 'val' in the tree whose root is given by 'root' and returns the new root
		 * It does nothing if the value to be inserted is already present in the BST
		 * The root has to be locked before calling this module.
		 * Also root cannot be null in this module
		 */
		
		if (val < root.key)
		{
			if (root.left == null)
			{
				// create a new node and make that as root.left
				root.left = new LockNode(val);
				root.lock.release();
			}
			else
			{
				try
				{
					root.left.lock.acquire();
					root.lock.release();
					insertRecursively(root.left, val);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else if (val > root.key)
		{
			if (root.right == null)
			{
				// create a new node and make that as root.right
				root.right = new LockNode(val);
				root.lock.release();
			}
			else
			{
				try
				{
					root.right.lock.acquire();
					root.lock.release();
					insertRecursively(root.right, val);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else
		{
			// value to be inserted is already present in the tree
			// Just release the lock
			root.lock.release();
		}
	}
	
	public void insert(int val)
	{
		// First acquire the treeLock before deciding whether the root is null or not
		try
		{
			treeLock.acquire();
		}
		catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}
		
		// Now check if the root is null
		if (root == null)
		{
			root = new LockNode(val);
		}
		
		// Get lock on the root
		try
		{
			root.lock.acquire();
			treeLock.release();
			insertRecursively(root, val);
			return;
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	
	public int getSuccessorValue(LockNode node, LockNode parent)
	{
		/*
		 * This function gets the successor value in the subtree whose root is node and also 
		 * deletes the successor from the right subtree.
		 *  
		 * Assumption for this module is that both the node and parent which are arguments are 
		 * already locked before calling this module
		 */
		
		int minv = node.key;
	
        while (node.left != null)
        {
        	// Get the lock of left child
        	try
        	{
				node.left.lock.acquire();
			}
        	catch (InterruptedException e)
        	{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	parent.lock.release();
            minv = node.left.key;
            parent = node;
            node = node.left;
        }
        
        if (node.right == null)
        {
        	if (parent.left == node)
        		parent.left = null;
        	else
        		parent.right = null;
        }
        else
        {
        	// Get the lock on the right child of successor
        	try
        	{
				node.right.lock.acquire();
			}
        	catch (InterruptedException e)
        	{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	if (parent.left == node)
        		parent.left = node.right;
        	else
        		parent.right = node.right;
        	
        	node.right.lock.release();
        }
        
        // Physical deletion
        node.right = null;
        
        // Release the locks
        node.lock.release();
        if (parent != this.root)
        	parent.lock.release();
        
        return minv;
	}
	
	public void deleteRecursively(LockNode node, LockNode parent, int val)
	{
		/* 
		 * The node and parent has to be locked before calling this module.
		 * Also root cannot be null in this module
		 */
		
		if (val < node.key)
		{
			if (node.left == null)
			{
				// Means that the value is not present in the BST. Just return if that is the case
				node.lock.release();
				if (parent != null)
					parent.lock.release();
				return;
			}
			else
			{
				try
				{
					node.left.lock.acquire();
					if (parent != null)
						parent.lock.release();
					deleteRecursively(node.left, node, val);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else if (val > node.key)
		{
			if (node.right == null)
			{
				// Means that the value is not present in the BST. Just return if that is the case
				node.lock.release();
				if (parent != null)
					parent.lock.release();
				return;
			}
			else
			{
				try
				{
					node.right.lock.acquire();
					if (parent != null)
						parent.lock.release();
					deleteRecursively(node.right, node, val);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else
		{
			// The value to be deleted is found now
			
			// Node has no child at all
			if ((node.left == null) && (node.right == null))
			{
				if (parent == null)
				{
					// Means that this node is the root
					this.root = null;
				}
				else if (parent.left == node)
				{
					parent.left = null;
				}
				else
				{
					parent.right = null;
				}
				
				// Releasing the locks
				node.lock.release();
				if (parent != null)
					parent.lock.release();
			}
			
			// Only the left child is null
			else if (node.left == null)
			{
				// Lock the right child of root
				try
				{
					node.right.lock.acquire();
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (parent == null)
				{
					// Means that this node is the root
					this.root = node.right;
				}
				else if (parent.left == node)
				{
					parent.left = node.right;
				}
				else
				{
					parent.right = node.right;
				}
				
				// Release the locks
				node.right.lock.release();
				// Physical Deletion
				node.right = null;
				node.lock.release();
				if (parent != null)
					parent.lock.release();
			}
			
			// Only the right child is null
			else if (node.right == null)
			{
				// Lock the left child of root
				try
				{
					node.left.lock.acquire();
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (parent == null)
				{
					// Means that this node is the root
					this.root = node.left;
				}
				else if (parent.left == node)
				{
					parent.left = node.left;
				}
				else
				{
					parent.right = node.left;
				}
				
				// Release the locks
				node.left.lock.release();
				// Physical Deletion
				node.left = null;
				node.lock.release();
				if (parent != null)
					parent.lock.release();
			}
			
			// Both the children are not null
			else
			{
				// Get the successor key value of the node in the right subtree
				// Acquire the lock on the right child
				try
				{
					node.right.lock.acquire();
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int minv = getSuccessorValue(node.right, node);
				node.key = minv;
				
				if (node.right != null)
					node.right.lock.release();
				node.lock.release();
				if (parent != null)
					parent.lock.release();
			}
			
		}
		
	}
	
	public void delete(int val)
	{
		// First acquire the treeLock before deciding whether the root is null or not
		try
		{
			treeLock.acquire();
		}
		catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}
		
		// Root is not yet created
		if (root == null)
		{
			treeLock.release();
			return;
		}
		
		// Get lock on the root
		try
		{
			root.lock.acquire();
			if (root.key == val)
			{
				deleteRecursively(root, null, val);
				treeLock.release();
			}
			else
			{
				treeLock.release();
				deleteRecursively(root, null, val);
			}
			
			return;
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	
	// A utility function to do inorder traversal of BST
    private void inorderRecursive(LockNode root)
    {
        if (root != null)
        {
            inorderRecursive(root.left);
            System.out.print(root.key);
            System.out.print(" ");
            inorderRecursive(root.right);
        }
    }
	
    public void inorderTraversal()
    {
       inorderRecursive(root);
       System.out.println();
    }
}