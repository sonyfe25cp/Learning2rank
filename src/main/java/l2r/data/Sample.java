package l2r.data;

import java.util.ArrayList;

/**
 * The Class Sample.
 */
public final class Sample {

	/** The documents. */
	private ArrayList<Document> documents = new ArrayList<Document>();
	
	public Document getQueryFeature(){
		ArrayList<Double> queryFeature = new ArrayList<Double>();
		int qid=0;
		int relId = 0;
		for(Document doc:documents){
			qid = doc.getQid();
			ArrayList<Double> feature = doc.getFeatures();
			queryFeature.addAll(feature);
		}
		Document queryDoc = new MSDocument(qid,relId,queryFeature);
		return queryDoc;
	}
	
	/** The qid. */
	private int qid;
	
	/**
	 * Instantiates a new sample.
	 *
	 * @param qid query id
	 */
	public Sample(int qid)
	{
		this.qid = qid;
	}
	
	/**
	 * Adds the.
	 *
	 * @param doc the doc
	 */
	public void add(Document doc)
	{
		documents.add(doc);
	}

	/**
	 * Gets the documents.
	 *
	 * @return the documents
	 */
	public ArrayList<Document> getDocuments() {
		return documents;
	}

	/**
	 * Gets the qid.
	 *
	 * @return the qid
	 */
	public int getQid() {
		return qid;
	}
	
}
