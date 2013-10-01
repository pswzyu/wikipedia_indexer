/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;

/**
 * @author nikhillo
 * This class is used to write an index to the disk
 * 
 */
public class IndexWriter implements Writeable {
	
	LocalDictionary dic;
	int part_number = -1;
	boolean isFwd;
	Properties props;
	INDEXFIELD field;
	TreeMap<String, LinkedList<IdAndOccurance> > idx;
	
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
		idx = new TreeMap<String, LinkedList<IdAndOccurance> >();
		this.props = props;
		this.field = keyField;
		this.isFwd = isForward;
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
		// 只有link会进这里
		addToIndex(Integer.toString(keyId), valueId, numOccurances);
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
		if (field == INDEXFIELD.CATEGORY || field == INDEXFIELD.AUTHOR) // 如果不是term,也不是link， 查字典然后加入index
		{
			key = Integer.toString(dic.lookup(key));
		}
		// 如果已经有了就加到队尾
		LinkedList<IdAndOccurance> find = idx.get(key);
		if (find != null)
		{
			ListIterator<IdAndOccurance> list_iter =  find.listIterator(find.size());
			while(list_iter.hasPrevious())
			{
				if (list_iter.previous().id < valueId) // 找到比当前id小的
					break;
			}
			list_iter.next();//然后倒回一个并且插入
			list_iter.add(new IdAndOccurance(valueId, numOccurances));
		}else // 没有就创建
		{
			LinkedList<IdAndOccurance> tmp = new LinkedList<IdAndOccurance>();
			tmp.add(new IdAndOccurance(valueId, numOccurances) );
			idx.put(key, tmp);
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
		File file = new File(filename);
		try
		{
	        if( !file.exists())
	        	file.createNewFile();
	        FileWriter fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	        StringBuffer str = new StringBuffer();
	        Set<Entry<String, LinkedList<IdAndOccurance> > > all = idx.entrySet();
	        Iterator<Entry<String, LinkedList<IdAndOccurance>>> iter = all.iterator();
	        while (iter.hasNext())
	        {
	        	Entry<String, LinkedList<IdAndOccurance>> this_entry = iter.next();
	        	ListIterator<IdAndOccurance> list_iter = this_entry.getValue().listIterator();
	        	str.append(this_entry.getKey() + ":=>");
	        	if ( list_iter.hasNext() )
	        	{
		        	while (true)
		        	{
		        		IdAndOccurance tmp = list_iter.next();
		        		str.append(tmp.id+","+tmp.occ);
		        		if (list_iter.hasNext())
		        		{
		        			str.append(";");
		        		}else
		        		{
		        			break;
		        		}
		        	}
	        	}
	        	str.append('\n');
	        }
	        bw.write(str.toString());
	        bw.flush();
	        bw.close();
	        fw.close();
		}catch(IOException ex)
		{
			ex.printStackTrace();
		}
		dic.writeToDisk();
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		// TODO Implement this method

	}
	private String getWriteFilename()
	{
		checkDir();
		return FileUtil.getRootFilesFolder(props)+"./index/" +
				FileUtil.getFieldName(field)+(part_number!=-1?"-"+part_number:"")+".txt";
	}
	private void checkDir()
	{
		File dir = new File(FileUtil.getRootFilesFolder(props)+"./index/");
		if (!dir.exists())
			dir.mkdir();
	}

}
