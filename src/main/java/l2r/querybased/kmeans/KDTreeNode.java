package l2r.querybased.kmeans;

import java.util.ArrayList;
import java.util.List;


public class KDTreeNode {
	private List<Double> data=new ArrayList<Double>();
	private int clusterIndex;
	private double distance;
	private KDTreeNode left;
	private KDTreeNode right;
	private KDTreeNode parent;
	private boolean isRoot;
	private boolean isCenter;
	private int split;
	
	public KDTreeNode(KmeansNode node){
		this.clusterIndex=node.getClusterIndex();
		this.data=node.getData();
	}
	
	//判断是否为叶子节点
	public boolean isLeaf(){
		return(left==null&&right==null);
	}
	
	public List<Double> getData() {
		return data;
	}
	public void setData(List<Double> data) {
		this.data = data;
	}
	public int getClusterIndex() {
		return clusterIndex;
	}
	public void setClusterIndex(int clusterIndex) {
		this.clusterIndex = clusterIndex;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public KDTreeNode getLeft() {
		return left;
	}
	public void setLeft(KDTreeNode left) {
		this.left = left;
	}
	public KDTreeNode getRight() {
		return right;
	}
	public void setRight(KDTreeNode right) {
		this.right = right;
	}
	public KDTreeNode getParent() {
		return parent;
	}
	public void setParent(KDTreeNode parent) {
		this.parent = parent;
	}
	public boolean isRoot() {
		return isRoot;
	}
	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}
	public int getSplit() {
		return split;
	}
	public void setSplit(int split) {
		this.split = split;
	}

	public boolean isCenter() {
		return isCenter;
	}

	public void setCenter(boolean isCenter) {
		this.isCenter = isCenter;
	}

	
}
