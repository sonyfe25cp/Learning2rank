package l2r.querybased.kmeans;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
	private int clusterIndex;
	private List<KmeansNode> nodeList=new ArrayList<KmeansNode>();
	private KmeansNode center;
	private boolean centerChange=true;
	
	public Cluster(int index){
		this.clusterIndex=index;
	}
	
	public Cluster(int index, KmeansNode center){
		this.clusterIndex=index;
		this.center=center;
	}
	
	public void addNode(KmeansNode node){
		nodeList.add(node);
	}
	
	public void removeNode(KmeansNode node){
		if(nodeList.contains(node))
			nodeList.remove(node);
	}
	
	public int getClusterIndex() {
		return clusterIndex;
	}
	public void setClusterIndex(int clusterIndex) {
		this.clusterIndex = clusterIndex;
	}
	public List<KmeansNode> getNodeList() {
		return nodeList;
	}
	public void setNodeList(List<KmeansNode> nodeList) {
		this.nodeList = nodeList;
	}
	public KmeansNode getCenter() {
		return center;
	}
	public void setCenter(KmeansNode center) {
		this.center = center;
	}

	public boolean isCenterChange() {
		return centerChange;
	}

	public void setCenterChange(boolean centerChange) {
		this.centerChange = centerChange;
	}
	
	

}

