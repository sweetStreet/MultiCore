package compared;

public class SequentialBST
{
	public Node root;
	
	// constructor
	public SequentialBST()
	{
		this.root = null;
	}
	
	private boolean searchRecursively(Node root, int val)
	{
		if(root == null)
			return false;
	
		if(root.key == val)
			return true;
		else if(val < root.key)
			return searchRecursively(root.left, val);
		else
			return searchRecursively(root.right, val);
	}
	
	public void search(int val)
	{
		boolean found = searchRecursively(root, val);
		if(found)
			System.out.println("Node with value " + Integer.toString(val) + " is present in BST");
		else
			System.out.println("Node with value " + Integer.toString(val) + " is not present in BST");
	}
	
	private Node insertRecursively(Node root, int val)
	{
		/*
		 * This function inserts the 'val' in the tree whose root is given by 'root' and returns the new root
		 */
		
		if(root == null)
		{
			root = new Node(val);
			return root;
		}
		// Now root is present and recur down the tree
		if(val < root.key)
			root.left = insertRecursively(root.left, val);
		else if(val > root.key)
			root.right = insertRecursively(root.right, val);
		
		return root;
	}
	
	public void insert(int key)
	{
		this.root = insertRecursively(this.root, key);
	}
	
	private int minValue(Node root)
    {
        int minv = root.key;
        while (root.left != null)
        {
            minv = root.left.key;
            root = root.left;
        }
        
        return minv;
    }
	
	private Node deleteRecursively(Node root, int val)
	{
		if (root == null)
			return root;
		
		if (val < root.key)
            root.left = deleteRecursively(root.left, val);
        else if (val > root.key)
            root.right = deleteRecursively(root.right, val);
        else
        {
        	// node with only one child or no child
            if (root.left == null)
                return root.right;
            else if (root.right == null)
                return root.left;
            
            // node with two children: Get the inorder successor (smallest in the right subtree)
            root.key = minValue(root.right);
            
            // Delete the inorder successor
            root.right = deleteRecursively(root.right, root.key);
        }
		
		return root;
	}
	
	public void delete(int key)
	{
		root = deleteRecursively(root, key);
	}
	
	// A utility function to do inorder traversal of BST
    private void inorderRecursive(Node root) 
    {
        if (root != null)
        {
            inorderRecursive(root.left);
            System.out.println(root.key);
            inorderRecursive(root.right);
        }
    }
	
    public void inorderTraversal()
    {
       inorderRecursive(root);
    }
}