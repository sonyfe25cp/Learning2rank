package l2r.querybased.kmeans;

import java.util.ArrayList;
import java.util.List;

import l2r.data.Sample;

public class KmeansNode {
	private int number;
	private List<Double> data=new ArrayList<Double>();
	private int clusterIndex;
	private boolean isCenter=false;
	
	public  KmeansNode(List<Double> data){
		this.data=data;
	}
	
	public KmeansNode(Sample sample){
		this.data=sample.getMeanFeatures();
		this.setNumber(sample.getQid());
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
	
	public void setCenter(){
		this.isCenter=true;
	}
	public void cancelCenter(){
		this.setCenter(false);
	}

	public boolean isCenter() {
		return isCenter;
	}

	public void setCenter(boolean isCenter) {
		this.isCenter = isCenter;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	

}
