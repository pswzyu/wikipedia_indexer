/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.HashMap;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;

/**
 * A simple map based token view of the transformed document
 * @author nikhillo
 *
 */
public class IndexableDocument {
	
	HashMap<INDEXFIELD, TokenStream> ts_pool;
	String page_title;
	
	public String getPageTitle() {
		return page_title;
	}

	public void setPageTitle(String page_title) {
		this.page_title = page_title;
	}

	/**
	 * Default constructor
	 */
	public IndexableDocument() {
		//TODO: Init state as needed
		ts_pool = new HashMap<INDEXFIELD, TokenStream>();
	}
	
	/**
	 * MEthod to add a field and stream to the map
	 * If the field already exists in the map, the streams should be merged
	 * @param field: The field to be added
	 * @param stream: The stream to be added.
	 */
	public void addField(INDEXFIELD field, TokenStream stream) {
		//TODO: Implement this method
		ts_pool.put(field, stream);
	}
	
	/**
	 * Method to return the stream for a given field
	 * @param key: The field for which the stream is requested
	 * @return The underlying stream if the key exists, null otherwise
	 */
	public TokenStream getStream(INDEXFIELD key) {
		//TODO: Implement this method
		return ts_pool.get(key);
	}
	
	/**
	 * Method to return a unique identifier for the given document.
	 * It is left to the student to identify what this must be
	 * But also look at how it is referenced in the indexing process
	 * 这里将title拼成一个连接的样子用parserLinks解析， 保证和link一样
	 * @return A unique identifier for the given document
	 */
	public String getDocumentIdentifier() {
		//TODO: Implement this method
		return WikipediaParser.parseLinks("[["+getPageTitle()+"]]")[1];
	}
	
}
