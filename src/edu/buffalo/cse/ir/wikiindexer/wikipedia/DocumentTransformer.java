/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.Tokenizer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument.Section;

/**
 * A Callable document transformer that converts the given WikipediaDocument object
 * into an IndexableDocument object using the given Tokenizer
 * @author nikhillo
 *
 */
public class DocumentTransformer implements Callable<IndexableDocument> {
	
	/*
	 * 添加变量， 保存初始化的时候传进来的参数
	 */
	HashMap<INDEXFIELD, Tokenizer> tz_map;
	WikipediaDocument target_doc;
	
	/**
	 * Default constructor, DO NOT change
	 * @param tknizerMap: A map mapping a fully initialized tokenizer to a given field type
	 * @param doc: The WikipediaDocument to be processed
	 */
	public DocumentTransformer(Map<INDEXFIELD, Tokenizer> tknizerMap, WikipediaDocument doc) {
		//TODO: Implement this method
		/* TODO: pswzyu:tknizerMap 是一个hashmap， 针对每一个文章field使用不同的
		 * tokenizer
		 */
		tz_map = (HashMap<INDEXFIELD, Tokenizer>) tknizerMap;
		target_doc = doc;
	}
	
	/**
	 * Method to trigger the transformation
	 * @throws TokenizerException Inc ase any tokenization error occurs
	 */
	public IndexableDocument call() throws TokenizerException {
		// TODO Implement this method
		/* TODO： 对每一个wikipidiaDocument的field调用对应的tokenizer
		 * 需要先将field的内容放到一个tokenizerstream中， 然后调用tokenizer的tokenize方法
		 */
		// term, 就是将文章内容取出来
		List<Section> sections = target_doc.getSections();
		Iterator<Section> sections_iter = sections.iterator();
		String sections_string = "";
		while (sections_iter.hasNext())
		{
			Section tmp_sec = sections_iter.next();
			if (tmp_sec.getTitle().equals("Default"))
			{
				// 标题要是default的话就不将标题添加
			}else
			{
				// 在标题两边添加.避免在分句的时候混淆，注意这个空格很必要
				sections_string += ". " + tmp_sec.getTitle() + ". ";
			}
			sections_string += tmp_sec.getText();
		}
		TokenStream ts_term = new TokenStream(sections_string);
		// author
		TokenStream ts_author = new TokenStream(target_doc.getAuthor());
		// category
		List<String> catagories = target_doc.getCategories();
		Iterator<String> catagories_iter = catagories.iterator();
		String catagories_string = "";
		while (catagories_iter.hasNext())
		{
			catagories_string += catagories_iter.next() + ". ";
		}
		TokenStream ts_catagory = new TokenStream(catagories_string);
		//LINK
		Set<String> links = target_doc.getLinks();
		Iterator<String> links_iter = links.iterator();
		String links_string = "";
		while (links_iter.hasNext())
		{
			links_string += links_iter.next() + ". ";
		}
		TokenStream ts_link = new TokenStream(links_string);
		
		// Lets ROCK!
		tz_map.get(INDEXFIELD.TERM).tokenize(ts_term);
		tz_map.get(INDEXFIELD.AUTHOR).tokenize(ts_author);
		tz_map.get(INDEXFIELD.CATEGORY).tokenize(ts_catagory);
		tz_map.get(INDEXFIELD.LINK).tokenize(ts_link);
		
		//将解析好的tokenstream装箱
		IndexableDocument product = new IndexableDocument();
		product.addField(INDEXFIELD.TERM, ts_term);
		product.addField(INDEXFIELD.AUTHOR, ts_author);
		product.addField(INDEXFIELD.CATEGORY, ts_catagory);
		product.addField(INDEXFIELD.LINK, ts_link);
		
		return product;
	}
	
}
