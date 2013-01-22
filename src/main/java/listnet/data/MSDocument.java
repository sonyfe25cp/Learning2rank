/*
 * 作者：罗磊
 * 任何问题可以联系作者的Email：luoleicn@gmail.com
 * 
 * 遵循知识共享（CC By2.5）协议详见http://creativecommons.org/licenses/by/2.5/cn/
 */
package listnet.data;
import java.util.ArrayList;



/**
 * The Class MSDocument.
 */
public class MSDocument implements Document{

	/** The qid. */
	private int qid;
	
	/** The relevance. */
	private int relevance;
	
	/** The features. */
	private ArrayList<Double> features;
	
	/**
	 * Instantiates a new mS document.
	 *
	 * @param qid the qid
	 * @param rel the rel
	 * @param f the f
	 */
	public MSDocument(int qid, int rel, ArrayList<Double> f)
	{
		this.features = f;
		this.relevance = rel;
		this.qid = qid;
	}
	
	/**
	 * 从训练数据中取出一条文档数据，把它从string解析为Document对象.
	 *
	 * @param line the line
	 * @return Document对象
	 */
	public static Document parseDoc(String line)
	{
		if (line == null)
			throw new NullPointerException("待解析为document的string为null");
				
		return parse(line);
	}

	/**
	 * 根据微软数据集的具体情况，解析出相关性以及特征，并包装返回。.
	 *
	 * @param line the line
	 * @return the document
	 */
	private static Document parse(String line)
	{
		ArrayList<Double> list = new ArrayList<Double>();
		int rel = 0;
		int qid = 0;
		
		String[] strs = line.split("\\s");
		rel = Integer.parseInt(strs[0]);
		qid = Integer.parseInt(strs[1].split(":")[1]);
		
		for (int i=2; i<strs.length; i++)
		{
			if(validforTREC(i)){
				list.add(Double.parseDouble(strs[i].split(":")[1]));
			}
		}

		return new MSDocument(qid, rel, list);
	}

	/**
	 * 由于某些特征没有实现....于是训练的时候也不用这些特征了...
	 * @param i
	 * @return
	 * Aug 3, 2012
	 */
	private static boolean validforTREC(int index){
		//41-45，66-70，91-95，116-125，128-136 这些维特征没有
		if((index>=41&&index<=45)||(index>=66&&index<=70)||(index>=91&&index<=95)
				||(index>=116&&index<=125)||(index>=128&&index<=136)){
			return false;
		}else{
			return true;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see listnet.data.Document#getFeatures()
	 */
	@Override
	public ArrayList<Double> getFeatures() {
		return this.features;
	}

	/* (non-Javadoc)
	 * @see listnet.data.Document#getRelevance()
	 */
	@Override
	public int getRelevance() {
		return this.relevance;
	}

	/* (non-Javadoc)
	 * @see listnet.data.Document#getQid()
	 */
	@Override
	public int getQid() {
		return qid;
	}
}
