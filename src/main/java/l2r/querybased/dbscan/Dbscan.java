package l2r.querybased.dbscan;

import java.util.ArrayList;
import java.util.List;

import l2r.data.Sample;
import l2r.process.SampleReader;

public class Dbscan {
	//所有节点
	private List<Node> nodeList=new ArrayList<Node>();
	//所有的类别
	private List<Cluster> clusters=new ArrayList<Cluster>();
	//最小半径阈值
	private double e;
	//半径内结点个数最小值，用来判断节点是否为核心点
	private int minp;
	
	/**
	 * 构造函数，初始化参数e和minp
	 * @param e
	 * @param minp
	 */
	public Dbscan(double e, int minp){
		this.e=e;
		this.minp=minp;
	}
	
	/**
	 * 根据传过来的samples集合，构造dbscan节点集合，并聚类
	 * @param samples
	 */
	public void dbscanCluster(List<Sample> samples){
		getNodes(samples);
		for(Node node:nodeList)
		{
			List<Node> neighbor=getNeighbor(node);
			if(neighbor.size()>minp)
			{
				node.setKey(true);
				node.setNeighbors(neighbor);
			}
		}
		//开始聚类
		clustering();
	}
	
	/**
	 * 根据samples节点生成dbscan节点
	 * @param samples
	 */
	public void getNodes(List<Sample> samples){
		for(Sample sample:samples)
		{
			int number=sample.getQid();
			List<Double> features=sample.getMeanFeatures();
			nodeList.add(new Node(number, features));
		}
	}
	
	/**
	 * 计算两个节点之间的距离，欧氏距离公式，对samples的聚类中没有用到，而是使用getSimilarity计算相似度
	 * @param feature1
	 * @param feature2
	 * @return
	 */
	public double getDistance(List<Double> feature1, List<Double> feature2){
		double distance=0.0;
		for(int i=0;i<feature1.size();i++)
		{
			double x=feature1.get(i);
			double y=feature2.get(i);
			distance+=(x-y)*(x-y);
		}
		//System.out.println("dis: "+Math.sqrt(distance));
		return Math.sqrt(distance);
	}
	
	/**
	 * 使用余弦值计算两个节点向量的相似度，即两个向量的点积与它们模的比值(a*b)/(|a||b|),可以将相似度约束到0~1之间
	 * @param node1
	 * @param node2
	 * @return
	 */
	public double getSimilarity(Node node1, Node node2){
		double similarity=0.0;
		for(int i=0;i<node1.getFeatures().size();i++)
		{
			similarity+=node1.getFeatures().get(i)*node2.getFeatures().get(i);
		}
		return similarity/(node1.getModule()*node2.getModule());
	}
	
	/**
	 * 查找当前节点的邻居节点，邻居节点为满足相似度大于最小半径阈值的节点
	 * @param testNode
	 * @return
	 */
	public List<Node> getNeighbor(Node testNode){
		List<Node> neighbor=new ArrayList<Node>();
		for(Node node:nodeList)
		{
			if(node!=testNode)
			{
				double distance=getSimilarity(testNode,node);
				if(distance>e)
				{
					neighbor.add(node);
				}
			}
		}
		return neighbor;
	}
	
	/**
	 * 核心聚类过程，首先判断节点是否为核心点，若是核心点，如果该点还没有被聚类，则以该点为核心创建一个类，并将其
	 * 邻居节点加入该类中，然后递归的对其邻居节点进行判断，若邻居节点仍为核心点，则把它们的邻居节点也加入该类中
	 */
	public void clustering(){
		int clusterIndex=-1;
		for(Node node:nodeList)
		{
			if(node.isKey())
			{
				if(!node.isClustered())
				{
					clusterIndex++;
					Cluster cluster=new Cluster(clusterIndex);
					clusters.add(cluster);
					node.setClustered(true);
					cluster.addNode(node);
					expandCluster(cluster, node.getNeighbors());
				}
			}
		}
	}
	
	/**
	 * 对核心点的类进行扩展，把核心点的邻居节点加入到该类中，并递归判断每个核心点的邻居节点
	 * @param cluster
	 * @param nodes
	 */
	public void expandCluster(Cluster cluster, List<Node> nodes){
		for(Node node:nodes)
		{
			if(!node.isKey())
			{
				if(!node.isClustered())
				{
					node.setClustered(true);
					cluster.addNode(node);
				}
			}
			else{
				if(!node.isClustered())
				{
					node.setClustered(true);
					cluster.addNode(node);
					expandCluster(cluster, node.getNeighbors());
				}
			}
		}
	}
	
	/**
	 * 计算所有节点之间相似度的平均值，聚类过程中并没有用到，只是用来方便确定参数
	 * @return
	 */
	public double meanDistance(){
		int length=nodeList.size();
		double distance=0.0;
		int count=0;
		for(int i=0;i<length;i++)
		{
			for(int j=i+1;j<length;j++)
			{
				double dis=getSimilarity(nodeList.get(i), nodeList.get(j));
				System.out.println(nodeList.get(i).getNumber()+"----"+nodeList.get(j).getNumber()+"==="+dis);
				distance+=dis;
				count++;
			}
		}
		distance=distance/count;
		System.out.println("meanDis: "+distance);
		return distance;
	}
	
	/**
	 *用来进行输出展示每个节点，看是否为核心点
	 */
	public void displayNode(){
		for(Node node:nodeList)
		{
			String str="node: "+node.getNumber()+" iskey: "+node.isKey()+" neighbor: "+node.getNeighbors().size()+" [";
			for(Node neighbor:node.getNeighbors()){
				str+=neighbor.getNumber()+" ";
			}
			str+="]";
			System.out.println(str);
		}
	}
	
	/**
	 * 输出展示每个类及类中的节点
	 */
	public void display(){
		//System.out.println("nodeList length: "+nodeList.size());
//		for(Node node:nodeList){
//			System.out.println("node"+node.getNumber()+" iskey: "+node.isKey()+" neighbor: "+node.getNeighbors());
//		}
		if(clusters==null||clusters.size()==0)
			return;
		for(int i=0;i<clusters.size();i++)
		{
			System.out.println("cluster"+clusters.get(i).getIndex()+": "+"size: "+clusters.get(i).getNodes().size());
			for(Node node:clusters.get(i).getNodes())
			{
				System.out.print(node.getNumber()+"  ");
			}
		}
		System.out.println();
	}
	
	public static void main(String[] args){
		SampleReader sr=new SampleReader();
		sr.readSamples("D:\\sample.txt");
		Dbscan db=new Dbscan(0.85, 70);
		db.dbscanCluster(sr.getSamples());
		db.meanDistance();
		db.displayNode();
		db.display();
	}

}
