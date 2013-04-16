package l2r.querybased;

public interface TreeNode {

	
	public TreeNode getParent();
	
	public TreeNode getLeftChildren();
	
	public TreeNode getRightChildren();
	
	public TreeNode setLeftChildren(TreeNode node);
	
	public TreeNode setRightChildren(TreeNode node);
	

}
