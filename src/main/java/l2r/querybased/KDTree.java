package l2r.querybased;

import java.util.ArrayList;
import java.util.List;

/**
 * 对整个样本集所有的样本的特征做一个kd-tree
 * 提供搜索功能
 * 
 * @author ChenJie
 *
 */
public class KDTree {
	
	private ArrayList<TreeNode> tree;

	private ArrayList<TreeNode> total;
	
	private TreeNode getRoot(List<TreeNode> nodes, int column){
		
		double mid = getMid(nodes,column);
		
		TreeNode root = getRoot(nodes);

		List<TreeNode> left = getSubNodes(nodes,mid,column,true);

		List<TreeNode> right = getSubNodes(nodes,mid,column,false);
		
		root.setLeftChildren(getRoot(left,column));

		root.setRightChildren(getRoot(right,column));
		
	}
	
	public void addNode(TreeNode node){
		total.add(node);
	}
	
	public void build(){
		buildTree(total,0);
	}
	
	public List<TreeNode> search(ArrayList<Double> feature, int k){
		return null;
	}
	
	public List<TreeNode> search(ArrayList<Double> feature, double distance){
		return null;
	}
	
	/**
	 * @param args
	 * Mar 6, 2013
	 */
	public static void main(String[] args) {
		int[][] samples = {{1,2},{7,4},{2,1},{3,5},{5,6},{2,9},{9,1},{6,4},{3,8},{8,3}};
		KDTree tree = new KDTree();
		for(int[] sample : samples){
			tree.addNode(sample);
		}
	}
	

}
