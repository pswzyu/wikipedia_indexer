/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;

/**
 * @author nikhillo
 * An abstract class that represents a dictionary object for a given index
 */
public abstract class Dictionary implements Writeable {
	
	// 存储词典的每一个元素， 可以保证速度， 因为每一个string都会得到一个独特的hashcode，而外面看到的是id
	HashMap<String, Integer > items;
	// 为词条分配id
	int auto_increase = 1;
	Properties props;
	INDEXFIELD field;
	String surfix = "";
	
	/*
	 * document dictionary 时field需要填写link， 因为link没有dictionary， link的dic其实就是
	 * document的dic
	 */
	
	public Dictionary (Properties props, INDEXFIELD field) {
		// 根据props中提供的文件名打开文件，创建相应的读取和写入槽
		items = new HashMap<String, Integer>();
		this.props = props;
		this.field = field;
	}
	/*
	 * pswzyu: set the surfix to the filename of the dictionary, used to differ different
	 * partitions to the term dictionary
	 */
	public void setSurfix(String s)
	{
		surfix = s;
	}
	
	/* (non-Javadoc) // 直接写入硬盘即可，dictionary不用做什么太多的工作， 一片文章只会产生一个记录，三个field才三个
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException {
		// 根据这个dictionary对应的field获取文件名
		String filename = getWriteFilename();
		File file = new File(filename);
		try
		{
	        if( !file.exists())
	        	file.createNewFile();
	        FileWriter fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	        StringBuffer str = new StringBuffer();
	        Set<String> keys = items.keySet();
	        Iterator<String> iter = keys.iterator();
	        while (iter.hasNext())
	        {
	        	String this_key = iter.next();
	        	str.append(this_key + ":=" + items.get(this_key));
	        }
	        bw.write(str.toString());
	        bw.flush();
	        bw.close();
	        fw.close();
		}catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		// TODO Implement this method

	}
	
	/**
	 * Method to check if the given value exists in the dictionary or not
	 * Unlike the subclassed lookup methods, it only checks if the value exists
	 * and does not change the underlying data structure
	 * @param value: The value to be looked up
	 * @return true if found, false otherwise
	 */
	public boolean exists(String value) {
		items.containsKey(value);
		return false;
	}
	
	/**
	 * MEthod to lookup a given string from the dictionary.
	 * The query string can be an exact match or have wild cards (* and ?)
	 * Must be implemented ONLY AS A BONUS
	 * @param queryStr: The query string to be searched
	 * @return A collection of ordered strings enumerating all matches if found
	 * null if no match is found
	 */
	public Collection<String> query(String queryStr) {
		//TODO: Implement this method (FOR A BONUS)
		LinkedList<String> result = new LinkedList<String>();
		if (exists(queryStr))
		{
			result.add(queryStr);
			return result;
		}else
		{
			return null;
		}
	}
	
	/**
	 * Method to get the total number of terms in the dictionary
	 * @return The size of the dictionary
	 */
	public int getTotalTerms() {
		return items.size();
	}
	protected abstract String getWriteFilename();
}
