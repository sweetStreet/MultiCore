package compared;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeNode
{
	
	/*
	 * Private class that allows coupling both child pointers to a single
	 * reference. This is useful when testing for logical removal because
	 * we can represent both child pointers with a single
	 * AtomicMarkableReference, ensuring atomicity when checking/setting a node
	 * as logically deleted.
	 */
	public class ChildNodes
	{
		
		LockFreeNode left;
		LockFreeNode right;
		
		// Creates a ChildNodes object with no child pointers.
		public ChildNodes()
		{
			left = null;
			right = null;
		}
		
		// Creates a ChildNodes object with the passed child nodes.
		public ChildNodes(LockFreeNode left, LockFreeNode right)
		{
			this.left = left;
			this.right = right;
		}
	}
	
	public int data;
	public AtomicMarkableReference<ChildNodes> children;
	
	public LockFreeNode(int data)
	{
		this.data = data;
		children = new AtomicMarkableReference<ChildNodes>(new ChildNodes(), false);
	}
	
	public LockFreeNode(int data, LockFreeNode leftChild, LockFreeNode rightChild)
	{
		this.data = data;
		children = new AtomicMarkableReference<ChildNodes>(new ChildNodes(leftChild, rightChild), false);
	}
	
	public boolean insertChild(String position, LockFreeNode oldChild, LockFreeNode newChild)
	{	
		// Create a new child node object to try and replace the current one
		ChildNodes curCN = children.getReference();
		ChildNodes newCN;
		switch(position)
		{
			case "RIGHT":
				if(curCN.right != oldChild)
					return false;
				newCN = new ChildNodes(curCN.left, newChild);
				break;
				
			case "LEFT":
				if(curCN.left != oldChild)
					return false;
				newCN = new ChildNodes(newChild, curCN.right);
				break;
				
			default:
				return false;
		}
		
		//Attempt to replace the old childNodes object with the new one
		return children.compareAndSet(curCN, newCN, false, false);
	}
	
	public LockFreeNode getChild(String position, boolean[] marked)
	{
		/**
		 * Getter shorthand method to grab a child pointer.
		 * @param position Which child pointer to access
		 * @return A pointer to the child node, or null if no child exists for that subtree
		 */
		
		switch(position)
		{
			case "LEFT":
				return this.children.get(marked).left;
				
			case "RIGHT":
				return this.children.get(marked).right;
				
			default:
				return null;
		}
	}
	
	
	public boolean mark()
	{
		/**
		 * Attempts to mark the node as logically deleted.
		 * @return True if the node was marked, false otherwise.
		 */
		
		return children.attemptMark(children.getReference(), true);
	}
	
	
	public boolean isMarked()
	{
		/**
		 * Getter method that returns whether or not the current node is marked.
		 * @return True if the node is marked for deletion, false otherwise
		 */
		
		return children.isMarked();
	}
	
	public boolean isLeaf()
	{
		/**
		 * Returns whether or not the current node is a leaf node by checking the
		 * child references.
		 * @return True if the node is a leaf node, false otherwise
		 */
		 
		return (children.getReference().left == null && children.getReference().right == null);
	}
	
}
