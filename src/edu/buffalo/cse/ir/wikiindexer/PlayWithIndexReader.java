package edu.buffalo.cse.ir.wikiindexer;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.indexer.IndexReader;

public class PlayWithIndexReader {

	public static void main(String[] args)
	{
		
//		LinkedList<Integer> list = new LinkedList<Integer>();
//		for (int step = 0; step != 2000000; ++step)
//		{
//			list.add(step);
//		}
//		long start = System.currentTimeMillis();
//		for (int step = 0; step != 10000000; ++ step)
//		{
//			list.listIterator(0);
//		}
//		long mid = System.currentTimeMillis();
//		System.out.println(""+(mid - start) + "mi");
//		for (int step = 0; step != 10000000; ++ step)
//		{
//			list.listIterator(list.size());
//		}
//		System.out.println(""+(System.currentTimeMillis() - mid) + "mi");

		
		Properties prop =  Runner.loadProperties("./files/properties.config");
		//IndexReader author = new IndexReader(prop, INDEXFIELD.AUTHOR);
		//IndexReader category = new IndexReader(prop, INDEXFIELD.CATEGORY);
		//IndexReader link = new IndexReader(prop, INDEXFIELD.LINK);
		IndexReader term = new IndexReader(prop, INDEXFIELD.TERM);

		//test(author);
		//test(category);
		//test(link);
		test(term);
	}
	public static void test(IndexReader ir)
	{
		System.out.println("=============");
		System.out.println(ir.getTotalKeyTerms());
		System.out.println(ir.getTotalValueTerms());
		printMap(ir.getPostings("videos"));
		System.out.println("-------------");
		printMap(ir.query("You'Tube", "Videos"));
		System.out.println("-------------");
		printColl(ir.getTopK(10));
		
	}
	static public void printMap(Map<String, Integer> a)
	{
		if (a == null)
		{
			System.out.println("NULL");
			return;
		}
		Set<Entry<String, Integer> > es = a.entrySet();
		Iterator<Entry<String, Integer> > iter = es.iterator();
		while (iter.hasNext())
		{
			Entry<String, Integer> next  = iter.next();
			System.out.print(next.getKey()+"->"+next.getValue()+", ");
		}
		System.out.println("");
	}
	static public void printColl(Collection<String> c)
	{
		Iterator<String> iter = c.iterator();
		while (iter.hasNext())
		{
			System.out.print(iter.next()+", ");
		}
		System.out.println("");
	}
}
