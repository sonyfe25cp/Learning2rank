package l2r.querybased.dbscan;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private int number;
	private List<Double> features=new ArrayList<Double>();
	private boolean isKey=false;
	private List<Node> neighbors=new ArrayList<Node>();
	private boolean isClustered=false;
	private int clusterIndex=-1;
	
	public Node(int number, List<Double> features){
		this.number=number;
		this.features=features;
	}
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public List<Double> getFeatures() {
		return features;
	}
	public void setFeatures(List<Double> features) {
		this.features = features;
	}
	public boolean isKey() {
		return isKey;
	}
	public void setKey(boolean isKey) {
		this.isKey = isKey;
	}
	public List<Node> getNeighbors() {
		return neighbors;
	}
	public void setNeighbors(List<Node> neighbors) {
		this.neighbors = neighbors;
	}
	public boolean isClustered() {
		return isClustered;
	}
	public void setClustered(boolean isClustered) {
		this.isClustered = isClustered;
	}
	public int getClusterIndex() {
		return clusterIndex;
	}
	public void setClusterIndex(int clusterIndex) {
		this.clusterIndex = clusterIndex;
	}
	
	

}
