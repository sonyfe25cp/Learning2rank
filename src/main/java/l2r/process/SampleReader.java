package l2r.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import l2r.data.Document;
import l2r.data.MSDocument;
import l2r.data.Sample;
import l2r.querybased.kmeans.Kmeans;

public class SampleReader {
	private List<Sample> samples=new ArrayList<Sample>();
	private List<Integer> qidList=new ArrayList<Integer>();
	
	/**
	 * 从filePath中读取文件，生成samples（相同的qid的document组成一个sample）
	 * @param filePath
	 */
	public void readSamples(String filePath){
		Sample sample=null;
		BufferedReader reader=null;
		String str=null;
		File file=new File(filePath);
		try {
			reader =new BufferedReader(new FileReader(file));
			try {
				//初始化，开始时samples为空，第一次读取一定生成一个新的sample
				if((str=reader.readLine())!=null){
					Document doc=MSDocument.parseDoc(str);
					int qid=doc.getQid();
					qidList.add(qid);
					sample=new Sample(qid);
					sample.add(doc);
				}
				while((str=reader.readLine())!=null){
					Document doc=MSDocument.parseDoc(str);
					int qid=doc.getQid();
					if(!qidList.contains(qid)){
						//如果qidList不包含该document的qid，说明上一个sample已经读取完毕，将上一个加入samples列表，并开始构建一个新的sample
						//一个sample读取完毕后，同时计算它的平均值和中位数
						sample.computeMeanFeatures();
						sample.computeMedianFeatures();
						samples.add(sample);
						qidList.add(qid);
						sample=new Sample(qid);
						sample.add(doc);
					}
					else{
						//如果qidList包含该document的qid，说明该document仍属于当前的sample，将其添加到当前sample中
						sample.add(doc);
					}
				}
				//因为最后一个sample在上面过程中没有加入，因此还要将其加入列表
				sample.computeMeanFeatures();
				sample.computeMedianFeatures();
				samples.add(sample);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void display(){
		for(int i=0;i<samples.size();i++){
			System.out.println("qid: "+samples.get(i).getQid()+"  docCount: "+samples.get(i).getDocCount());
			System.out.println("meanFeatures: "+samples.get(i).getMeanFeatures());
			System.out.println("medianFeatures: "+samples.get(i).getMedianFeatures());
			
		}
	}
	
	public List<Sample> getSamples() {
		return samples;
	}


	public void setSamples(List<Sample> samples) {
		this.samples = samples;
	}


	public List<Integer> getQidList() {
		return qidList;
	}


	public void setQidList(List<Integer> qidList) {
		this.qidList = qidList;
	}


	public static void main(String[] args){
		SampleReader sr=new SampleReader();
		sr.readSamples("E:\\sample.txt");
		//sr.display();
//		Dbscan db=new Dbscan(38325.0, 10);
//		db.dbscanCluster(sr.getSamples());
//		db.meanDistance();
//		db.displayNode();
//		db.display();
		Kmeans kmeans=new Kmeans(4,sr.getSamples());
		kmeans.clustering();
		kmeans.displayCluster();
	}

}
