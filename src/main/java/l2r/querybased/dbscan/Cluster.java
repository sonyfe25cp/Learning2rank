package l2r.querybased.dbscan;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
	private int index;
	private List<Node> nodes=new ArrayList<Node>();
	
	public  Cluster(int index){
		this.index=index;
	}
	
	public void addNode(Node node){
		if(!nodes.contains(node)){
			nodes.add(node);
		}
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public List<Node> getNodes() {
		return nodes;
	}
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
	
	

}
