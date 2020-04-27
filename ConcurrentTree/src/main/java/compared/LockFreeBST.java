package compared;

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeBST
{
	AtomicReference<LockFreeNode> root;
	
	public LockFreeBST()
	{
		root = new AtomicReference<LockFreeNode>(null);
	}
	
	
	public boolean search(int data)
	{
		LockFreeNode curNode = root.get();
		boolean[] marked = {false};
		
		// Tree is not empty, search the tree
		while(curNode != null)
		{
			if(curNode.data > data)
			{
				// curNode is "bigger" than the passed data, search the left subtree
				curNode = curNode.getChild("LEFT", marked);
			}
			else if(curNode.data < data)
			{
				// curNode is "smaller" than the passed data, search the right subtree
				curNode = curNode.getChild("RIGHT", marked);
			}
			else
			{
				// Found the data, make sure that it isn't an internal node and that is isn't marked
				if(curNode.isLeaf())
					return !curNode.isMarked();
				else
					curNode = curNode.getChild("RIGHT", marked);
			}
		}
		
		//Tree is empty or data is not in the tree
		return false;
	}
	
	
	private LockFreeNode createSubtree(LockFreeNode parentNode, LockFreeNode newNode, int compare)
	{
		LockFreeNode newParent;
		if(compare > 0)
		{
			newParent = new LockFreeNode(parentNode.data, newNode, parentNode);
		}
		else
		{
			newParent = new LockFreeNode(newNode.data, parentNode, newNode);
		}
		return newParent;
	}
	
	
	public boolean insert(int data)
	{

		LockFreeNode newNode = new LockFreeNode(data);
		LockFreeNode newParent = null;
		LockFreeNode curNode = null;
		LockFreeNode parentNode = null;
		LockFreeNode gparentNode = null;
		LockFreeNode ggparentNode = null;
		int compare = 0, oldCompare = 0, reallyOldCompare = 0;
		boolean[] marked = {false};

		retry: while(true)
		{
			curNode = root.get();
			if(curNode == null)
			{
				// Tree is empty, try to insert newNode as the root
				if(root.compareAndSet(null, newNode))
					return true;
				else
					continue retry;
			}
			else
			{
				// Tree is not empty, iterate into the tree
				while(curNode != null)
				{
					ggparentNode = gparentNode;
					gparentNode = parentNode;
					parentNode = curNode;
					reallyOldCompare = oldCompare;
					oldCompare = compare;
					// compare = curNode.data.compareTo(data);
					if (curNode.data > data)
						compare = 5;
					else if (curNode.data < data)
						compare = -5;
					else
						compare = 0;
					
					if(compare > 0)
					{
						// curNode is "bigger" than the passed data, iterate into the left subtree
						curNode = curNode.getChild("LEFT", marked);
					}
					else if(compare < 0)
					{
						// curNode is "smaller" than the passed data, iterate into the right subtree
						curNode = curNode.getChild("RIGHT", marked);
					}
					else
					{
						// If this is a leaf node, then the data is already in the tree
						// Otherwise, we can keep traversing
						if(curNode.isLeaf())
							return false; //TODO if its marked, can this thread
										  //try and remove it then restart the
										  //insertion?
						else
							curNode = curNode.getChild("RIGHT", marked);
					}
				}

				// Check edge cases
				if(gparentNode == null)
				{
					// Edge case 1: inserting at 1st level (tree only has 1 element)
					if(parentNode.isMarked())
					{
						// Taking the courtesy of deleting the marked node
						root.compareAndSet(parentNode, null);
						continue retry;
					}
					newParent = createSubtree(parentNode, newNode, compare);
					if(root.compareAndSet(parentNode, newParent))
						return true;
					else
						continue retry;
				}
				else if(ggparentNode == null)
				{
					// Edge case 2: Inserting at 2nd level (tree has 2 elements)
					if(parentNode.isMarked())
					{
						if(oldCompare > 0)
							parentNode = gparentNode.getChild("RIGHT", marked);
						else
							parentNode = gparentNode.getChild("LEFT", marked);
						if(!root.compareAndSet(gparentNode, parentNode))
							continue retry;
						newParent = createSubtree(parentNode, newNode, oldCompare);
						if(root.compareAndSet(parentNode, newParent))
							return true;
						else
							continue retry;
					}
				}
				else if(parentNode.isMarked())
				{
					// Edge case 3: Attempt to delete parentNode if it is marked
					if(oldCompare > 0)
						newParent = gparentNode.getChild("RIGHT", marked);
					else
						newParent = gparentNode.getChild("LEFT", marked);
					if(reallyOldCompare > 0)
					{
						if(!ggparentNode.insertChild("LEFT", gparentNode, newParent))
							continue retry;
					}
					else
					{
						if(!ggparentNode.insertChild("RIGHT", gparentNode, newParent))
							continue retry;
					}
					parentNode = newParent;
					gparentNode = ggparentNode;
					compare = oldCompare;
					oldCompare = reallyOldCompare;
				}

				// Attempt insertion
				newParent = createSubtree(parentNode, newNode, compare);
				if(oldCompare > 0)
				{
					if(gparentNode.insertChild("LEFT", parentNode, newParent))
						return true;
					else
						continue retry;
				}
				else
				{
					if(gparentNode.insertChild("RIGHT", parentNode, newParent))
						return true;
					else
						continue retry;
				}
			}
		}
	}
	
	
	public void delete(int data)
	{
		LockFreeNode newParent = null;
		LockFreeNode curNode = null;
		LockFreeNode parentNode = null;
		LockFreeNode gparentNode = null;
		int compare = 0, oldCompare = 0;
		boolean[] marked = {false};

		retry: while(true)
		{
			// Check to see if the tree is empty
			curNode = root.get();
			if(curNode == null)
				return;
			else
			{
				// The tree isn't empty, iterate into the tree
				parentNode = curNode;
				while(curNode != null)
				{
					// compare = curNode.data.compareTo(data);
					if (curNode.data > data)
						compare = 5;
					else if (curNode.data < data)
						compare = -5;
					else
						compare = 0;
					
					if(compare > 0)
					{
						// curNode is "bigger" than the passed data, iterate into the left subtree
						curNode = curNode.getChild("LEFT", marked);
					}
					else if(compare < 0)
					{
						// curNode is "smaller" than the passed data, iterate into the right subtree
						curNode = curNode.getChild("RIGHT", marked);
					}
					else
					{
						//If this is a leaf node, then the data is in the tree and can be removed
						// Otherwise, we keep traversing
						if(curNode.isLeaf())
						{
							// Attempt to mark the current node.  Note that this is the linearization point.
							// Even if we can't physically remove the node, if this call succeeds then we were successful
							if(!curNode.mark())
								continue retry;

							if(parentNode == null)
							{
								// Edge case 1: deletion of root
								root.compareAndSet(curNode, null);
							}
							else if(gparentNode == null)
							{
								// Edge case 2: deletion at 1st level
								if(compare > 0)
									newParent = parentNode.getChild("RIGHT", marked);
								else
									newParent = parentNode.getChild("LEFT", marked);
								root.compareAndSet(parentNode, newParent);
							}
							else
							{
								//Normal deletion.  Attempt to physically remove the node
								if(compare > 0)
									newParent = parentNode.getChild("RIGHT", marked);
								else
									newParent = parentNode.getChild("LEFT", marked);
								if(oldCompare > 0)
								{
									gparentNode.insertChild("LEFT", parentNode, newParent);
								}
								else
								{
									gparentNode.insertChild("RIGHT", parentNode, newParent);
								}
							}
							return;
						}
						else
							curNode = curNode.getChild("RIGHT", marked);
					}
					gparentNode = parentNode;
					parentNode = curNode;
					oldCompare = compare;
				}
				
				// The data wasn't in the tree
				return;
			}
		}
	}
	
	public void inorderRecursive(LockFreeNode root)
	{
		if (root != null)
        {
			boolean[] marked = {false};
            inorderRecursive(root.children.get(marked).left);
            System.out.print(root.data);
            System.out.print(" ");
            boolean[] marked1 = {false};
            inorderRecursive(root.children.get(marked1).right);
        }
	}
	
	public void inorderTraversal()
	{
		inorderRecursive(root.get());
	    System.out.println();
	}

}
