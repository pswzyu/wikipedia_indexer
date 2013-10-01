package edu.buffalo.cse.ir.wikiindexer;

import java.util.Properties;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.indexer.IndexReader;

public class PlayWithIndexReader {

	public static void main(String[] args)
	{
//		Properties prop =  Runner.loadProperties("./files/properties.config");
//		IndexReader author = new IndexReader(prop, INDEXFIELD.AUTHOR);
//		IndexReader category = new IndexReader(prop, INDEXFIELD.CATEGORY);
//		IndexReader link = new IndexReader(prop, INDEXFIELD.LINK);
//		IndexReader term = new IndexReader(prop, INDEXFIELD.TERM);
		
//		test(author);
//		test(category);
//		test(link);
//		test(term);
	}
	public static void test(IndexReader ir)
	{
		System.out.println("=============");
		System.out.println(ir.getTotalKeyTerms());
		System.out.println(ir.getTotalValueTerms());
		ir.getPostings("");
		System.out.println("-------------");
	}
}
