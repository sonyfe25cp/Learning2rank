package l2r.querybased.kmeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class KDTree {
	private KDTreeNode root=null;
	//所有中心节点
	private List<KDTreeNode> treeNodes=new ArrayList<KDTreeNode>();
	private KDTreeNode closetCenter;
	private double minDistance=1000000;//存储临时最小距离，初始距离为无穷大
	private int n;
	private int m;
	
	public KDTree(){
		
	}
	
	/**
	 * 构造函数，由kmeans节点列表生成kdtree节点列表，并构造kdtree（此处传过来的kmeansNodes为所有的中心点）
	 * @param kmeansNodes，所有的中心点节点
	 */
	public KDTree(List<KmeansNode> kmeansNodes){
		if(kmeansNodes==null)
			return;
		for(int i=0;i<kmeansNodes.size();i++){
			KDTreeNode node=new KDTreeNode(kmeansNodes.get(i));
			treeNodes.add(node);
		}
		this.setN(treeNodes.get(0).getData().size());
		buildKDTree(treeNodes, null, 0, 0);
	}
	
	/*
	 * 根据传过来的参数（kmeans中的某个节点）找到离它最近的中心点，调用kdtree的搜索函数
	 */
	public int getClosetClustering(KmeansNode testNode){
		KDTreeNode testKDTreeNode=new KDTreeNode(testNode);
		searchClosetCenter(this.getRoot(), testKDTreeNode);
		int closetClusterIndex=closetCenter.getClusterIndex();
		return closetClusterIndex;
	}
	
	
	/**
	 * 构造整棵kdtree的过程，该过程是一个递归过程，先构造根节点，然后依次构造其左右子树并递归下去
	 * @param childNodes，要构造的子树节点列表
	 * @param parentNode，子树节点的父节点（因为要确定父子关系），初始状态时，parentNode为null（即表示构造整棵树的根节点）
	 * @param m， 在第m维上进行分裂（排序），采用循环的方式，从0~n-1,然后再从0开始循环
	 * @param child，表示要构造根节点的那种子树（-1表示左子树，1表示右子树，0表示构造整棵树的根节点）
	 */
	public void buildKDTree(List<KDTreeNode> childNodes, KDTreeNode parentNode, int m, int child){
		if(childNodes==null||childNodes.size()==0)
			return;
		m=m%n;
		childNodes=sortByPos(childNodes,m);
		int k=childNodes.size()/2;
		KDTreeNode rootNode=childNodes.get(k);
		rootNode.setSplit(m);
		if(child==0)
			this.setRoot(rootNode);
		else{
			if(child==-1){
				parentNode.setLeft(rootNode);
				rootNode.setParent(parentNode);
			}
			else if(child==1){
				parentNode.setRight(rootNode);
				rootNode.setParent(parentNode);
			}
		}
		if(childNodes.size()==1)
			return;
		else{
			List<KDTreeNode> leftNodes=childNodes.subList(0, k);
			buildKDTree(leftNodes, rootNode, m+1, -1);
			if(childNodes.size()>2){//只有size>2时才会有右子树
				List<KDTreeNode> rightNodes=childNodes.subList(k+1, childNodes.size());
				buildKDTree(rightNodes, rootNode, m+1, 1);
				
			}
		}
	}
	
	/**
	 * 根据给定的节点testNode，从根节点开始递归的查找距离最近的节点
	 * @param rootNode，每次递归开始的根节点
	 * @param testNode，要查找的节点
	 */
	public void searchClosetCenter(KDTreeNode rootNode, KDTreeNode testNode){
		if(rootNode==null||testNode==null)
			return;
		Stack<KDTreeNode> pathNodes=getPathNodes(rootNode, testNode);
		while(!pathNodes.isEmpty()){
			KDTreeNode node=pathNodes.pop();
			double distance=getDistance(node.getData(), testNode.getData());
			if(distance<minDistance){
				closetCenter=node;
				minDistance=distance;
				if(!node.isLeaf()){
					int split=node.getSplit();
					if(Math.abs(node.getData().get(split)-testNode.getData().get(split))<minDistance){
						if((testNode.getData().get(split)<=node.getData().get(split))&&node.getRight()!=null)
							searchClosetCenter(node.getRight(), testNode);
						else if((testNode.getData().get(split)>node.getData().get(split))&&node.getLeft()!=null)
							searchClosetCenter(node.getLeft(),testNode);
					}//if
				}//if
			}//if
		}//while
	}
	
	/**
	 * 根据数据的第pos维对数据进行排序
	 * @param kdtreeNodes，要排序的所有节点
	 * @param pos，根据第pos维排序
	 * @return
	 */
	public List<KDTreeNode> sortByPos(List<KDTreeNode> kdtreeNodes, int pos){
		for(int i=0;i<kdtreeNodes.size();i++){
			int k=i;
			for(int j=i+1;j<kdtreeNodes.size();j++){
				KDTreeNode nodeK=kdtreeNodes.get(k);
				KDTreeNode nodeJ=kdtreeNodes.get(j);
				if(nodeJ.getData().get(pos)<nodeK.getData().get(pos))
					k=j;
			}//for j
			if(i!=k){
				KDTreeNode tempNode=kdtreeNodes.get(i);
				kdtreeNodes.set(i, kdtreeNodes.get(k));
				kdtreeNodes.set(k, tempNode);
			}
		}
		return kdtreeNodes;
	}
	
	/**
	 * 每次找到近似最近距离节点时，返回真个过程中经过的节点，方便进行回溯查找，若测试数据在分裂维的值大于根节点的值进入右子树，否则进入左子树
	 * @param root，要遍历的子树的根节点
	 * @param testNode，给定的要查找的数据节点
	 * @return
	 */
	public Stack<KDTreeNode> getPathNodes(KDTreeNode root, KDTreeNode testNode){
		Stack<KDTreeNode> pathNode=new Stack<KDTreeNode>();
		KDTreeNode kNode=root;
		pathNode.push(root);
		while(!kNode.isLeaf()){//一直找到叶子节点
			int split=kNode.getSplit();
			if(testNode.getData().get(split)<=kNode.getData().get(split)){//小于等于进入左子树
				if(kNode.getLeft()!=null){
					kNode=kNode.getLeft();
					pathNode.push(kNode);
				}
				else
					break;
			}
			else{//大于进入右子树
				if(kNode.getRight()!=null){
					kNode=kNode.getRight();
					pathNode.push(kNode);
				}
				else
					break;
			}
		}//while
		return pathNode;
	}
	
	//计算两个数据的距离(欧氏距离)
		/**
		 * 
		 * @param data1
		 * @param data2
		 * @return
		 */
		public double getDistance(List<Double> data1, List<Double> data2){
			double count=0;
			for(int i=0;i<data1.size();i++){
				double div=data1.get(i)-data2.get(i);
				count+=div*div;
			}
			return Math.sqrt(count);
		}
		
		public void display(KmeansNode testNode){
			for(int i=0;i<treeNodes.size();i++){
				double distance=getDistance(testNode.getData(), treeNodes.get(i).getData());
				System.out.println(treeNodes.get(i).getData()+": "+distance);
			}
			System.out.println("closet: "+closetCenter.getData());
		}

		public KDTreeNode getRoot() {
			return root;
		}

		public void setRoot(KDTreeNode root) {
			this.root = root;
		}

		public List<KDTreeNode> getTreeNodes() {
			return treeNodes;
		}

		public void setTreeNodes(List<KDTreeNode> treeNodes) {
			this.treeNodes = treeNodes;
		}

		public int getN() {
			return n;
		}

		public void setN(int n) {
			this.n = n;
		}

		public int getM() {
			return m;
		}

		public void setM(int m) {
			this.m = m;
		}
		
		public static void main(String[] args){
			List<KmeansNode> kmList=new ArrayList<KmeansNode>();
			List<List<Double>> data=new ArrayList<List<Double>>();
			
			List<Double> point1=new ArrayList<Double>();
			point1.add(1.0);
			point1.add(3.0);
			KmeansNode kn1=new KmeansNode(point1);
			kn1.setClusterIndex(1);
			kmList.add(kn1);
			
			List<Double> point2=new ArrayList<Double>();
			point2.add(4.0);
			point2.add(5.0);
			KmeansNode kn2=new KmeansNode(point2);
			kn2.setClusterIndex(2);
			kmList.add(kn2);
			
			List<Double> point3=new ArrayList<Double>();
			point3.add(7.0);
			point3.add(6.0);
			KmeansNode kn3=new KmeansNode(point3);
			kn3.setClusterIndex(3);
			kmList.add(kn3);
			
			List<Double> point4=new ArrayList<Double>();
			point4.add(3.0);
			point4.add(4.0);
			KmeansNode kn4=new KmeansNode(point4);
			kn4.setClusterIndex(4);
			kmList.add(kn4);
			
			List<Double> point5=new ArrayList<Double>();
			point5.add(5.0);
			point5.add(9.0);
			KmeansNode kn5=new KmeansNode(point5);
			kn5.setClusterIndex(5);
			kmList.add(kn5);
			
			List<Double> point6=new ArrayList<Double>();
			point6.add(2.0);
			point6.add(4.0);
			KmeansNode kn6=new KmeansNode(point6);
			kn6.setClusterIndex(6);
			kmList.add(kn6);
			
			List<Double> point7=new ArrayList<Double>();
			point7.add(2.0);
			point7.add(6.0);
			KmeansNode kn7=new KmeansNode(point7);
			kn7.setClusterIndex(7);
			kmList.add(kn7);
			
			List<Double> point8=new ArrayList<Double>();
			point8.add(8.0);
			point8.add(5.0);
			KmeansNode kn8=new KmeansNode(point8);
			kn8.setClusterIndex(8);
			kmList.add(kn8);
			
			List<Double>test=new ArrayList<Double>();
			test.add(7.5);
			test.add(8.2);
			KmeansNode testKM=new KmeansNode(test);
			
			KDTree kdtree=new KDTree(kmList);
			int index=kdtree.getClosetClustering(testKM);
			kdtree.display(testKM);
			System.out.println(index);
		}
	

}

