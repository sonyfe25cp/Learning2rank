package l2r.querybased.dbscan;

import java.util.ArrayList;
import java.util.List;

import l2r.data.Sample;
import l2r.process.SampleReader;

public class Dbscan {
	private List<Node> nodeList=new ArrayList<Node>();
	private List<Cluster> clusters=new ArrayList<Cluster>();
	private double e;
	private int minp;
	
	public Dbscan(double e, int minp){
		this.e=e;
		this.minp=minp;
	}
	
	public void dbscanCluster(List<Sample> samples){
		getNodes(samples);
		for(Node node:nodeList){
			List<Node> neighbor=getNeighbor(node);
			if(neighbor.size()>minp){
				node.setKey(true);
				node.setNeighbors(neighbor);
			}
		}
		clustering();
	}
	
	public void getNodes(List<Sample> samples){
		for(Sample sample:samples){
			int number=sample.getQid();
			List<Double> features=sample.getMeanFeatures();
			nodeList.add(new Node(number, features));
		}
	}
	
	public double getDistance(List<Double> feature1, List<Double> feature2){
		double distance=0.0;
		for(int i=0;i<feature1.size();i++){
			double x=feature1.get(i);
			double y=feature2.get(i);
			distance+=(x-y)*(x-y);
		}
		//System.out.println("dis: "+Math.sqrt(distance));
		return Math.sqrt(distance);
	}
	
	public List<Node> getNeighbor(Node testNode){
		List<Node> neighbor=new ArrayList<Node>();
		for(Node node:nodeList){
			if(node!=testNode){
				double distance=getDistance(testNode.getFeatures(),node.getFeatures());
				if(distance>e){
					neighbor.add(node);
				}
			}
		}
		return neighbor;
	}
	
	public void clustering(){
		int clusterIndex=-1;
		for(Node node:nodeList){
			if(node.isKey()){
				if(!node.isClustered()){
					clusterIndex++;
					Cluster cluster=new Cluster(clusterIndex);
					clusters.add(cluster);
					cluster.addNode(node);
					expandCluster(cluster, node.getNeighbors());
				}
			}
		}
	}
	
	public void expandCluster(Cluster cluster, List<Node> nodes){
		for(Node node:nodes){
			if(!node.isKey()){
				if(!node.isClustered()){
					node.setClustered(true);
					cluster.addNode(node);
				}
			}
			else{
				if(!node.isClustered()){
					node.setClustered(true);
					cluster.addNode(node);
					expandCluster(cluster, node.getNeighbors());
				}
			}
		}
	}
	
	public double meanDistance(){
		int length=nodeList.size();
		double distance=0.0;
		int count=0;
		for(int i=0;i<length;i++){
			for(int j=i+1;j<length;j++){
				double dis=getDistance(nodeList.get(i).getFeatures(), nodeList.get(j).getFeatures());
				System.out.println(nodeList.get(i).getNumber()+"----"+nodeList.get(j).getNumber()+"==="+dis);
				distance+=dis;
				count++;
			}
		}
		distance=distance/count;
		System.out.println("meanDis: "+distance);
		return distance;
	}
	
	public void displayNode(){
		for(Node node:nodeList){
			System.out.println("node: "+node.getNumber()+" iskey: "+node.isKey()+" neighbor: "+node.getNeighbors().size());
		}
	}
	
	public void display(){
		//System.out.println("nodeList length: "+nodeList.size());
//		for(Node node:nodeList){
//			System.out.println("node"+node.getNumber()+" iskey: "+node.isKey()+" neighbor: "+node.getNeighbors());
//		}
		if(clusters==null||clusters.size()==0)
			return;
		for(int i=0;i<clusters.size();i++){
			System.out.println("cluster"+clusters.get(i).getIndex()+": ");
			for(Node node:clusters.get(i).getNodes()){
				System.out.print(node.getNumber()+"  ");
			}
		}
		System.out.println();
	}
	
	public static void main(String[] args){
		SampleReader sr=new SampleReader();
		sr.readSamples("E:\\sample.txt");
		Dbscan db=new Dbscan(38325.0, 10);
		db.dbscanCluster(sr.getSamples());
		db.meanDistance();
		db.displayNode();
		db.display();
	}

}
