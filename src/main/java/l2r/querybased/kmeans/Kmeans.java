package l2r.querybased.kmeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import l2r.data.Sample;
import l2r.process.SampleReader;


public class Kmeans {
	private int k;
	private int m;//数据维数
	private int dataSetLength;
	private List<List<Double>> dataSet=new ArrayList<List<Double>>();
	private List<KmeansNode> kNodes=new ArrayList<KmeansNode>();
	private List<Cluster> clusters=new ArrayList<Cluster>();
	private List<KmeansNode> centers=new ArrayList<KmeansNode>();
	private int maxRepeat=200;
	private boolean centersChange=true;
	
	/**
	 * 构造函数，首先根据提供的数据构造kmeans节点，并且初始化k个类及类中心（随机查找）
	 * @param k
	 * @param dataSet
	 */
//	public Kmeans(int k, List<List<Double>> dataSet){
//		this.k=k;
//		this.dataSet=dataSet;
//		if(dataSet!=null)
//			m=dataSet.get(0).size();
//		for(int i=0;i<dataSet.size();i++){
//			KmeansNode node=new KmeansNode(dataSet.get(i));
//			kNodes.add(node);
//		}
//		initCluster();
//	}
	
	public Kmeans(int k, List<Sample> samples){
		if(samples==null||samples.size()<k){
			System.out.println("samples is null or k is larger than samples' size! ");
		}
		this.k=k;
		this.m=samples.get(0).getMedianFeatures().size();
		for(Sample sample:samples){
			KmeansNode node=new KmeansNode(sample);
			kNodes.add(node);
		}
		initCluster();
	}
	
	/**
	 * 初始化类的过程，首先随机查找k个中心点，将所有节点分配到距离最近的中心点所在的类中
	 */
	public void initCluster(){
		List<Integer> centerIndex=new ArrayList<Integer>();
		Random rand=new Random();
		//初始化类中心，并将其加入相应的类中（从0到k-1）
		for(int i=0;i<k;i++){
			int index=rand.nextInt(kNodes.size());
			while(centerIndex.contains(index)){
				index=rand.nextInt(kNodes.size());
			}
			//System.out.println(i);
			centerIndex.add(index);
			KmeansNode center=new KmeansNode(kNodes.get(index).getData());
			center.setCenter();
			center.setClusterIndex(i);
			centers.add(center);
			Cluster cluster=new Cluster(i, center);
			clusters.add(cluster);
		}
		//将所有数据分到相应的类中（根据与各个类中心点的聚类）
		for(int i=0;i<kNodes.size();i++){
			KmeansNode node=kNodes.get(i);
			int clusterIndex=getMinClusterIndex(node);
			node.setClusterIndex(clusterIndex);
			clusters.get(clusterIndex).addNode(node);
		}
	}
	
	/**
	 * kmeans聚类的核心过程（迭代），每次计算新的类中心节点，并将所有的节点分配给新的类别，直到满足条件结束（中心节点不在发生变化）
	 */
	public void clustering(){
		int repeat=0;
		while(true){
			//System.out.println("第"+(j++)+"次迭代：");
			calNewCenter();
			if(centersChange==false||repeat>=maxRepeat){
				return;
			}
			//为数据点重新分配类别
			for(int i=0;i<kNodes.size();i++){
				KmeansNode node=kNodes.get(i);
				int oldIndex=node.getClusterIndex();
				//次数查找最近距离的中心节点使用kdtree的查找功能，如果想改成普通的kmeans查找过程，使用getMinClusterIndex函数
				int newIndex=getMinClusterIndex(node);
				if(oldIndex!=newIndex){
					clusters.get(oldIndex).removeNode(node);
					node.setClusterIndex(newIndex);
					clusters.get(newIndex).addNode(node);
				}
			}
			repeat++;
		}
	}
	
	/**
	 * 计算新的类中心节点，使用找质心的方法，即求所有点在某一维上的平均值
	 */
	public void calNewCenter(){
		List<KmeansNode> newCenters=new ArrayList<KmeansNode>();
		for(int i=0;i<k;i++){
			//System.out.println("第"+i+"个类：");
			List<KmeansNode> nodes=new ArrayList<KmeansNode>();
			nodes=clusters.get(i).getNodeList();
			if(nodes!=null&&nodes.size()!=0){
				double[] center=new double[m];
				List<Double> centerData=new ArrayList<Double>();
				for(int j=0;j<nodes.size();j++){
					for(int k=0;k<m;k++){
						center[k]+=nodes.get(j).getData().get(k);
					}
				}
				for(int k=0;k<m;k++){
					center[k]=center[k]/nodes.size();
					centerData.add(center[k]);
				}
				KmeansNode newCenter=new KmeansNode(centerData);
				newCenter.setCenter();
				newCenter.setClusterIndex(i);
				clusters.get(i).setCenter(newCenter);
				newCenters.add(newCenter);
			}
			else{//如果某类为空，随机选择一个节点为中心点
				Random rand=new Random();
				int index=rand.nextInt(kNodes.size());
				KmeansNode center=new KmeansNode(kNodes.get(index).getData());
				clusters.get(i).setCenter(center);
				newCenters.add(center);
			}
				
		}
		if(getError(centers, newCenters)<10)
			centersChange=false;
		else
			centers=newCenters;
	}
	
	/**
	 * 判断中心节点有没有发生变化，根据中心节点的坐标进行判断
	 * @param center，旧的中心节点
	 * @param newCenter，新计算的中心节点
	 * @return
	 */
	public boolean isEquals(KmeansNode center, KmeansNode newCenter){
		boolean equal=true;
		for(int i=0;i<center.getData().size();i++){
			if(!(center.getData().get(i)-newCenter.getData().get(i)==0.0)){
				equal=false;
				break;
			}
		}
		return equal;
	}
	
	/**
	 * 使用kdtree的查找功能查找距离最近的中心节点
	 * @param testNode，要归类的节点
	 * @return，返回距离最近的中心节点所在的类别的索引号
	 */
	public int getClosetCluster(KmeansNode testNode){
		KDTree kdtree=new KDTree(centers);
		int closetClusterIndex=kdtree.getClosetClustering(testNode);
		return closetClusterIndex;
	}
	
	/**
	 * 使用传统的方式查找距离最近的中心节点
	 * @param node，要归类的节点
	 * @return，返回距离最近的中心节点所在的类别索引号
	 */
	public int getMinClusterIndex(KmeansNode node){
		int index=0;
		double minDistance=getDistance(node, centers.get(0));
		if(minDistance==0){
			return index;
		}
		for(int i=1;i<k;i++){
			double distance=getDistance(node, centers.get(i));
			if(distance==0)
				return i;
			else if(distance<minDistance){
				minDistance=distance;
				index=i;
			}
		}
		return index;
	}
	
	/**
	 * 计算两个节点之间的距离，使用欧氏距离公式
	 * @param node1
	 * @param node2
	 * @return
	 */
	public double getDistance(KmeansNode node1, KmeansNode node2){
		double distance=0.0;
		for(int i=0;i<node1.getData().size();i++){
			double x=node1.getData().get(i);
			double y=node2.getData().get(i);
			distance+=(x-y)*(x-y);
		}
		if(distance==0)
			return 0.0;
		else
			return -Math.sqrt(distance);
	}
	
	public double getError(List<KmeansNode> oldCenters, List<KmeansNode> newCenters){
		double error=0.0;
		for(int i=0;i<oldCenters.size();i++){
			//System.out.println(i);
			for(int j=0;j<newCenters.size();j++){
				error+=Math.abs(getDistance(oldCenters.get(i), newCenters.get(j)));
			}
		}
		return error;
	}
	
	public void displayCluster(){
		for(int i=0;i<clusters.size();i++){
			System.out.println("cluster"+i+": "+"center: "+clusters.get(i).getCenter().getData());
			for(int j=0;j<clusters.get(i).getNodeList().size();j++){
				System.out.print(clusters.get(i).getNodeList().get(j).getNumber()+" ");
			}
			System.out.println();
		}
	}
	
	public int getK() {
		return k;
	}
	public void setK(int k) {
		this.k = k;
	}
	public int getDataSetLength() {
		return dataSetLength;
	}
	public void setDataSetLength(int dataSetLength) {
		this.dataSetLength = dataSetLength;
	}
	public List<List<Double>> getDataSet() {
		return dataSet;
	}
	public void setDataSet(List<List<Double>> dataSet) {
		this.dataSet = dataSet;
	}
	public List<KmeansNode> getkNodes() {
		return kNodes;
	}
	public void setkNodes(List<KmeansNode> kNodes) {
		this.kNodes = kNodes;
	}
	public List<Cluster> getClusters() {
		return clusters;
	}
	public void setClusters(List<Cluster> clusters) {
		this.clusters = clusters;
	}
	public boolean isCentersChange() {
		return centersChange;
	}
	public void setCentersChange(boolean centersChange) {
		this.centersChange = centersChange;
	}
	
	public static void main(String[] args){
		SampleReader sr=new SampleReader();
		sr.readSamples("E:\\sample.txt");
		Kmeans kmeans=new Kmeans(6,sr.getSamples());
		kmeans.clustering();
		kmeans.displayCluster();
		
	}
	
	

}