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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.Tokenizer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerFactoryForQuery;

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
	HashMap< String, String > inv_other_dic; // id->name
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
		occ_freq = new HashMap<String, Integer>();
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
		if (field == INDEXFIELD.LINK)
			oth_dic = new HashMap<String, String>();
		File file = new File(getReadFilename(-1)[0]);
		FileReader fr;
		try {
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ( (line = br.readLine()) != null )
			{
				String[] str_id = line.split(":=>"); // 0->str, 1->id
				doc_dic.put(str_id[1], str_id[0]); // 要反过来存储因为后边基本都是知道id找名字
				if (field == INDEXFIELD.LINK)// 对于link需要将docdic反过来作为othdic
				{
					oth_dic.put(str_id[0], str_id[1]);
				}
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
		File file = null;
		if (field != INDEXFIELD.LINK) // link的字典用的doc的
		{
			// 先读取字典
			oth_dic = new HashMap<String, String>();
			inv_other_dic = new HashMap<String, String>();
			file = new File(getReadFilename(0)[0]);
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line = null;
				while ( (line = br.readLine()) != null )
				{
					String[] str_id = line.split(":=>"); // 0->str, 1->id
					oth_dic.put(str_id[0], str_id[1]); // 一正一反
					inv_other_dic.put(str_id[1], str_id[0]);
				}
				br.close();
				fr.close();
					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else // link的反表就是docdic
		{
			inv_other_dic = doc_dic;
		}
		// 然后读取idx
		file = new File(getReadFilename(0)[1]);
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ( (line = br.readLine()) != null )
			{
				String[] split1 = line.split(":=>"); // 0是名字
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
				
				String tmp = null;
				while (  (tmp = br_idx.readLine() ) != null )
				{
					int total_occ = 0; // 统计这个item总共出现了几次
					String[] split1 = tmp.split(":=>"); // 0是term
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
				br_idx.close();
				fr_idx.close();
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
		TokenizerFactoryForQuery tfq = TokenizerFactoryForQuery.getInstance(props);
		TokenStream stream = new TokenStream(key);
		Tokenizer tk = tfq.getTokenizer(1);
		try {
			tk.tokenize(stream);
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stream.reset();
		key = stream.next();
		
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		String search_key=null;
		if (field == INDEXFIELD.TERM) // 如果是term直接查找
		{
			search_key = key;
		}else
		{
			search_key = oth_dic.get(key);
		}
		if (search_key == null)
			return null;
			
		LinkedList<IdAndOccurance> li = idx.get(search_key);
		if (li == null)
			return null;
		ListIterator<IdAndOccurance> iter = li.listIterator();
		while(iter.hasNext())
		{
			IdAndOccurance iao = iter.next();
			result.put(doc_dic.get(Integer.toString(iao.id)), iao.occ);
		}
		return result;
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
		HashSet<String> result = new HashSet<String>();
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
		if(field != INDEXFIELD.TERM) // 查字典
		{
			HashSet<String> new_result = new HashSet<String>();
			Iterator<String> iter = result.iterator();
			while (iter.hasNext())
			{
				new_result.add(  inv_other_dic.get(iter.next() )  );	
			}
			return new_result;
		}else
		{
			return result;
		}
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
		TokenizerFactoryForQuery tfq = TokenizerFactoryForQuery.getInstance(props);
		TokenStream stream = new TokenStream("");
		stream.append(terms);
		Tokenizer tk = tfq.getTokenizer(1);
		try {
			tk.tokenize(stream);
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stream.reset();
		stream.getAllTokens().toArray(terms);
		
		// HashMap< DocId, IdAndOccurance<has how many kinds of term,
		// 	all the terms occured how many time> >
		HashMap<Integer, IdAndOccurance > temp_re = new HashMap<Integer, IdAndOccurance>();
		if ( field != INDEXFIELD.TERM ) // 如果不是term的话需要先转成id
		{
			for (int step = 0; step != terms.length; ++ step)
			{
				terms[step] = oth_dic.get(terms[step]);
				if (terms[step] == null)
					return null;
			}
		}
		// 统计tmp——re
		for (int step = 0; step != terms.length; ++ step)
		{
			LinkedList<IdAndOccurance> li = idx.get(terms[step]);
			if (li == null)
				return null;
			ListIterator<IdAndOccurance> iter = li.listIterator();
			while(iter.hasNext())
			{
				IdAndOccurance iao = iter.next(); // term出现的每一个doc
				IdAndOccurance re_iao = temp_re.get(iao.id); // 在tmp中找有没有这个doc
				if (re_iao != null) // 这个docid在之前的term遍历时添加过
				{
					re_iao.id += 1;
					re_iao.occ += iao.occ;
				}else // 这个docid在之前没出现过
				{
					if (step == 0) // 如果是遍历第一个query term， 那么添加did，否则直接忽略
					{
						temp_re.put(iao.id, new IdAndOccurance(1, iao.occ));
					}
				}
			}
		}
		LinkedHashMap<String, Integer> re = new LinkedHashMap<String, Integer>();
		// 从tmp中遍历， 依次找出最大的，删除出现次数不足terms.length的
		while (!temp_re.isEmpty())
		{
			IdAndOccurance now_highest = new IdAndOccurance(-1, 0);
			Integer highest_did = 0;
			Set<Integer> docids = temp_re.keySet();
			Iterator<Integer> iter = docids.iterator();
			while (iter.hasNext())
			{
				Integer did = iter.next();
				IdAndOccurance toto = temp_re.get(did);
				if (toto.id < terms.length) // 不是每个term都出现在这个did里了
				{
					iter.remove();
					continue;
				}
				if (toto.occ >= now_highest.occ)
				{
					now_highest = toto;
					highest_did = did;
				}
			}
			if (now_highest.id != -1) // id是用来计数的， 如果id还是-1说明所有的都被删掉了
			{
				re.put(doc_dic.get(Integer.toString(highest_did)), now_highest.occ);
				temp_re.remove(highest_did);
			}
		}
		return re;
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
