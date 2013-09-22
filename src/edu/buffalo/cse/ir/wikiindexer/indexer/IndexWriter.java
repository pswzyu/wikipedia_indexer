/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.util.LinkedList;
import java.util.Properties;
import java.util.SortedMap;
import java.util.SortedSet;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;

/**
 * @author nikhillo
 * This class is used to write an index to the disk
 * 
 */
public class IndexWriter implements Writeable {
	
	LocalDictionary dic;
	int part_number;
	Properties props;
	INDEXFIELD field;
	SortedMap<Integer, LinkedList<IdAndOccurance> > idx;
	
	/**
	 * Constructor that assumes the underlying index is inverted
	 * Every index (inverted or forward), has a key field and the value field
	 * The key field is the field on which the postings are aggregated
	 * The value field is the field whose postings we are accumulating
	 * For term index for example:
	 * 	Key: Term (or term id) - referenced by TERM INDEXFIELD
	 * 	Value: Document (or document id) - referenced by LINK INDEXFIELD
	 * @param props: The Properties file
	 * @param keyField: The index field that is the key for this index
	 * @param valueField: The index field that is the value for this index
	 */
	public IndexWriter(Properties props, INDEXFIELD keyField, INDEXFIELD valueField) {
		this(props, keyField, valueField, false);
	}
	
	/**
	 * Overloaded constructor that allows specifying the index type as
	 * inverted or forward
	 * Every index (inverted or forward), has a key field and the value field
	 * The key field is the field on which the postings are aggregated
	 * The value field is the field whose postings we are accumulating
	 * For term index for example:
	 * 	Key: Term (or term id) - referenced by TERM INDEXFIELD
	 * 	Value: Document (or document id) - referenced by LINK INDEXFIELD
	 * @param props: The Properties file
	 * @param keyField: The index field that is the key for this index
	 * @param valueField: The index field that is the value for this index
	 * @param isForward: true if the index is a forward index, false if inverted
	 */
	public IndexWriter(Properties props, INDEXFIELD keyField, INDEXFIELD valueField, boolean isForward) {
		//TODO: Implement this method
		dic = new LocalDictionary(props, keyField);
		this.props = props;
		this.field = keyField;
	}
	
	/**
	 * Method to make the writer self aware of the current partition it is handling
	 * Applicable only for distributed indexes.
	 * @param pnum: The partition number
	 */
	public void setPartitionNumber(int pnum) {
		part_number = pnum;
		dic.setSurfix(Integer.toString(part_number));
	}
	
	/**
	 * Method to add a given key - value mapping to the index
	 * @param keyId: The id for the key field, pre-converted
	 * @param valueId: The id for the value field, pre-converted
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, int valueId, int numOccurances) throws IndexerException {
		//TODO: Implement this method
	}
	
	/**
	 * Method to add a given key - value mapping to the index
	 * @param keyId: The id for the key field, pre-converted
	 * @param value: The value for the value field
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, String value, int numOccurances) throws IndexerException {
		//TODO: Implement this method
	}
	
	/**
	 * Method to add a given key - value mapping to the index
	 * @param key: The key for the key field
	 * @param valueId: The id for the value field, pre-converted
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(String key, int valueId, int numOccurances) throws IndexerException {
		// for term
		int term_id = dic.lookup(key);
		// 如果已经有了就加到队尾
		if (idx.containsKey(term_id))
		{
			idx.get(term_id).add(new IdAndOccurance(valueId, numOccurances));
		}else // 没有就创建
		{
			idx.put(term_id, new LinkedList<IdAndOccurance>());
		}
	}
	
	/**
	 * Method to add a given key - value mapping to the index
	 * @param key: The key for the key field
	 * @param value: The value for the value field
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(String key, String value, int numOccurances) throws IndexerException {
		//TODO: Implement this method
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException {
		// TODO Implement this method
		String filename = getWriteFilename();
		
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		// TODO Implement this method

	}
	private String getWriteFilename()
	{
		return FileUtil.getRootFilesFolder(props)+"./index/" +
				FileUtil.getFieldName(field)+"-"+(part_number!=0?part_number:"")+".txt";
	}

}
