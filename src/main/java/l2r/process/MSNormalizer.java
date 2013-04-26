package l2r.process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import l2r.data.Document;
import l2r.data.MSDocument;
import l2r.data.Sample;


/**
 * The Class MSNormalizer.
 */
public final class MSNormalizer implements Normalizer{

	/** 一个ArrayList，保存特征向量纬度个MaxMin，每个MaxMin实例保存这一维度的权重的最大值和最小值. */
	private ArrayList<MaxMin> maxmin = new ArrayList<MSNormalizer.MaxMin>();
	
	
	public MSNormalizer(){}
	
	public MSNormalizer(ArrayList<MaxMin> mm)
	{
		this.maxmin = mm;
	}
	/**
	 * Gets the normalizer.
	 *
	 * @param f 训练文本文件
	 * @return the normalizer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Normalizer getNormalizer(File f) throws IOException
	{
		MSNormalizer normalizer = new MSNormalizer();
		DataReader reader = new MSDataReader(f);
		Sample smaple = reader.getNextSample();
		
		if (smaple == null)
			return null;
		
		//初始化一个同样featuresize大小的初始向量
		int featureSize = smaple.getDocuments().get(0).getFeatures().size();
		for (int i=0; i<featureSize; i++)
		{
			normalizer.maxmin.add(new MaxMin(0.0, Double.MAX_VALUE));		
		}
		
		do
		{
			List<Document> docs = smaple.getDocuments();
			for (Document doc : docs)
			{
				for (int i=0; i<featureSize; i++)
				{
					if (doc.getFeatures().size() == 7)
					{
						System.out.println("d");
					}
					double val = doc.getFeatures().get(i);

					if (val > normalizer.maxmin.get(i).getMax())
						normalizer.maxmin.get(i).setMax(doc.getFeatures().get(i));
					if (val < normalizer.maxmin.get(i).getMin())
						normalizer.maxmin.get(i).setMin(val);
				}
			}
		}while ((smaple = reader.getNextSample()) != null);
		
		return normalizer;
	}
	
	/**
	 * 这个函数功能跟上面的getNormalizer()相同，但这里是接收已读取的samples作为参数，因为一般情况下，会先读完数据，再进行处理
	 * @param samples
	 */
	public void computeNorParameters(List<Sample> samples)
	{
		if(samples==null)
			return;
		int featureSize=samples.get(0).getDocuments().get(0).getFeatures().size();
		//
		for(int i=0;i<featureSize;i++)
		{
			this.maxmin.add(new MaxMin(0.0, Double.MAX_VALUE));
		}
		for(Sample sample: samples)
		{
			for(Document doc: sample.getDocuments())
			{
				for(int i=0;i<featureSize;i++)
				{
					double value=doc.getFeatures().get(i);
					
					if(value>maxmin.get(i).getMax())
						this.maxmin.get(i).setMax(value);
					if(value<maxmin.get(i).getMin())
						this.maxmin.get(i).setMin(value);
				}
			}
		}
		
	}
	
	/* 注意这个类没有更改doc对象，而是创建了一个全新的document的实例
	 * @see listnet.process.Normalizer#normalize(listnet.data.Document)
	 */
	@Override
	public Document normalize(Document doc) {
		
		int featureSize = this.maxmin.size();
		ArrayList<Double> oldfeatures = doc.getFeatures();
		ArrayList<Double> features = new ArrayList<Double>(featureSize);
		for (int i=0; i<featureSize; i++)
		{
			double max = maxmin.get(i).getMax();
			double min = maxmin.get(i).getMin();
			if (max == min)
				features.add(new Double(0.0));
			else
				features.add(new Double(oldfeatures.get(i)-min)/(max-min));//平滑权重 curr-min/max-min  归一化
		}
		
		return new MSDocument(doc.getQid(), doc.getRelevance(), features);
	}

	/* 注意这个类没有更改sample对象，而是创建了一个全新的sample的实例
	 * @see listnet.process.Normalizer#normalize(listnet.data.Sample)
	 */
	@Override
	public Sample normalize(Sample sample) {
		Sample res = new Sample(sample.getQid());
		
		List<Document> docs = sample.getDocuments();
		for (Document doc : docs)
		{
			res.add(normalize(doc));
		}
		
		return res;
	}

	@Override
	public ArrayList<MaxMin> getNorParameters() {
		return this.maxmin;
	}
	
	public void debug()
	{
		System.out.println("MSNormalizer : ");
		for (int i=0; i<maxmin.size(); i++)
		{
			System.out.println("max["+i+"] = " + maxmin.get(i).getMax() + " min["+i+"] = " + maxmin.get(i).getMin());
		}
	}

}
