/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;

/**
 * @author nikhillo
 * This class is used to introspect a given index
 * The expectation is the class should be able to read the index
 * and all associated dictionaries.
 */
public class IndexReader {
	
	Properties props;
	INDEXFIELD field;
	
	TreeMap< String, LinkedList<IdAndOccurance> > idx;
	HashMap< String, Integer > occ_freq; // 用来存储idx中某个key的总共出现次数
	HashMap< String, String > oth_dic; // 名字->id
	HashMap< String, String > doc_dic; // id->名字。 因为搜索的不一样
	
	/**
	 * Constructor to create an instance 
	 * @param props: The properties file
	 * @param field: The index field whose index is to be read
	 */
	public IndexReader(Properties props, INDEXFIELD field) {
		this.props = props;
		this.field = field;
		recoverDocDic();
		idx = new TreeMap<String, LinkedList<IdAndOccurance> >();
		switch (field)
		{
		case TERM:
			recoverTerm();
			break;
		case LINK:
		case AUTHOR:
		case CATEGORY:
			recoverOther();
		}
	}
	/*
	 * 反序列化
	 */
	public void recoverDocDic()
	{
		doc_dic = new HashMap<String, String>();
		File file = new File(getReadFilename(-1)[0]);
		FileReader fr;
		try {
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ( (line = br.readLine()) != null )
			{
				String[] str_id = line.split(":="); // 0->str, 1->id
				doc_dic.put(str_id[1], str_id[0]); // 要反过来存储因为后边基本都是知道id找名字
			}
			br.close();
			fr.close();
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * 读取相关的文件到内存对象-link, auth, cate
	 */
	public void recoverOther()
	{
		// 先读取字典
		oth_dic = new HashMap<String, String>();
		File file = new File(getReadFilename(0)[0]);
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ( (line = br.readLine()) != null )
			{
				String[] str_id = line.split(":="); // 0->str, 1->id
				oth_dic.put(str_id[0], str_id[1]); // 不要反过来，后边基本都是找名字
			}
			br.close();
			fr.close();
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 然后读取idx
		file = new File(getReadFilename(0)[1]);
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ( (line = br.readLine()) != null )
			{
				String[] split1 = line.split(":="); // 0是名字
				String[] split2 = split1[1].split(";");// 每个都是一个did,occ
				int total_occ = 0; // 统计这个item总共出现了几次
				LinkedList<IdAndOccurance> li =  new LinkedList<IdAndOccurance>();
				for (int step = 0; step != split2.length; ++ step)
				{
					String[] split3 = split2[step].split(",");
					total_occ += Integer.parseInt(split3[1]);
					li.add(new IdAndOccurance(Integer.parseInt(split3[0]), 
							Integer.parseInt(split3[1]) ));
				}
				idx.put(split1[0], li);
				occ_freq.put(split1[0], total_occ);
			}
			br.close();
			fr.close();
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * 读取相关的文件到内存对象-term
	 */
	public void recoverTerm()
	{
		File[] idx_file = new File[Partitioner.getNumPartitions()];
		for (int step = 0; step != Partitioner.getNumPartitions(); ++ step)
		{
			idx_file[step] = new File(getReadFilename(step)[1]);
		}
		try
		{
			for (int step = 0; step != Partitioner.getNumPartitions(); ++step)
			{
				FileReader fr_idx = new FileReader(idx_file[step]);
				BufferedReader br_idx = new BufferedReader(fr_idx);
				StringBuffer sb_idx = new StringBuffer();
				
				String tmp = null;
				while (  (tmp = br_idx.readLine() ) != null )
				{
					int total_occ = 0; // 统计这个item总共出现了几次
					String[] split1 = tmp.split(":="); // 0是term
					String[] split2 = split1[1].split(";");// 每个都是一个did,occ
					LinkedList<IdAndOccurance> li = idx.get(split1[0]);
					if (li == null) // 如果没有这个term
					{
						li = new LinkedList<IdAndOccurance> ();
						for (int step1 = 0; step1 != split2.length; ++step1)
						{
							String[] split3 = split2[step1].split(",");
							li.add(new IdAndOccurance(Integer.parseInt(split3[0]),
									Integer.parseInt(split3[1])) );
							total_occ += Integer.parseInt(split3[1]);
						}
						idx.put(split1[0], li);
						occ_freq.put(split1[0], total_occ); // 新建这个词总共出现的次数
					}else // 已经有这个term了， 将文件中的插入到linkedlist中正确的位置
					{
						ListIterator<IdAndOccurance> iter = li.listIterator();
						int list_id_now = 0;
						IdAndOccurance t = null;
						for (int step1 = 0; step1 != split2.length; ++step1)
						{
							String[] split3 = split2[step1].split(","); // 0->id,1->occ
							total_occ += Integer.parseInt(split3[1]);
							boolean jumped = false;
							while (true)
							{
								// 目标是将list指针移到第一个比split里的大的元素
								if (!iter.hasNext() ||
										list_id_now >= Integer.parseInt(split3[0]) )
								{
									break;
								}
								t = iter.next();
								list_id_now = t.id;
								jumped = true;
							}
							if ( jumped && list_id_now >= Integer.parseInt(split3[0]) && iter.hasPrevious())
								iter.previous();
							
							if (list_id_now == Integer.parseInt(split3[0]))
							{
								t.occ += Integer.parseInt(split3[1]);
								
							}else
							{
								iter.add(new IdAndOccurance( Integer.parseInt(split3[0]),
										Integer.parseInt(split3[1]) ));
							}
						}
						// 更新总共出现的次数
						occ_freq.put(split1[0], occ_freq.get(split1[0]) + total_occ);
					}
				}
			}
		}catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * Method to get the total number of terms in the key dictionary
	 * @return The total number of terms as above
	 */
	public int getTotalKeyTerms() {
		return idx.size();
	}
	
	/**
	 * Method to get the total number of terms in the value dictionary
	 * @return The total number of terms as above
	 */
	public int getTotalValueTerms() {
		return doc_dic.size();
	}
	
	/**
	 * Method to retrieve the postings list for a given dictionary term
	 * @param key: The dictionary term to be queried
	 * @return The postings list with the value term as the key and the
	 * number of occurrences as value. An ordering is not expected on the map
	 */
	public Map<String, Integer> getPostings(String key) {
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		String search_key;
		if (field == INDEXFIELD.TERM) // 如果是term直接查找
		{
			search_key = key;
		}else // 如果不是term， 需要先到对应的dic中找到id
		{
			search_key = oth_dic.get(key);
		}
			
		LinkedList<IdAndOccurance> li = idx.get(search_key);
		ListIterator<IdAndOccurance> iter = li.listIterator();
		while(iter.hasNext())
		{
			IdAndOccurance iao = iter.next();
			result.put(doc_dic.get(iao.id), iao.occ);
		}
		return null;
	}
	
	/**
	 * Method to get the top k key terms from the given index
	 * The top here refers to the largest size of postings.
	 * @param k: The number of postings list requested
	 * @return An ordered collection of dictionary terms that satisfy the requirement
	 * If k is more than the total size of the index, return the full index and don't 
	 * pad the collection. Return null in case of an error or invalid inputs
	 */
	public Collection<String> getTopK(int k) {
		Set<Entry<String, Integer> > pairs = occ_freq.entrySet();
		LinkedList<String> result = new LinkedList<String>();
		for(int step = 0; step != k; ++ step)
		{
			Iterator<Entry<String, Integer> > iter = pairs.iterator();
			Entry<String, Integer> max_now = null;
			while (iter.hasNext())
			{
				Entry<String, Integer> val = iter.next();
				if ( result.contains(val.getKey()) ) // 这个已经是找过的最大的
					continue;
				if (max_now == null || val.getValue() > max_now.getValue())
				{
					max_now = val;
				}
			}
			if (max_now == null) // 已经没有那么多元素了
			{
				break;
			}else
			{
				result.add(max_now.getKey());
			}
		}
		return result;
	}
	
	/**
	 * Method to execute a boolean AND query on the index
	 * @param terms The terms to be queried on
	 * @return An ordered map containing the results of the query
	 * The key is the value field of the dictionary and the value
	 * is the sum of occurrences across the different postings.
	 * The value with the highest cumulative count should be the
	 * first entry in the map.
	 */
	public Map<String, Integer> query(String... terms) {
		//TODO: Implement this method (FOR A BONUS)
		return null;
	}
	/*
	 * pswzyu：获取读取文件的文件名， 如果是term的话需要传入part， 如果不是则传入0
	 * 获取doc dic的话part传-1
	 */
	private String[] getReadFilename(int part)
	{
		String[] re = new String[2];
		if (part == -1)
		{
			re[0] = new String(FileUtil.getRootFilesFolder(props)+"./dics/document.txt");
		}else
		{
			re[0] = new String(FileUtil.getRootFilesFolder(props)+"./dics/" +
					FileUtil.getFieldName(field)+
					(field==INDEXFIELD.TERM?"-"+part:"")+".txt");
			re[1] = new String(FileUtil.getRootFilesFolder(props)+"./index/" +
					FileUtil.getFieldName(field)+
					(field==INDEXFIELD.TERM?"-"+part:"")+".txt");
		}
		return re;
	}
}
