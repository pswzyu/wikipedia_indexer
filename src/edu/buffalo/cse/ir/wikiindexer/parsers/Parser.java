/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.parsers;

import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Properties;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument;

/**
 * @author nikhillo
 *
 */
public class Parser {
	/* */
	private final Properties props;
	
	/**
	 * 
	 * @param idxConfig
	 * @param parser
	 */
	public Parser(Properties idxProps) {
		props = idxProps;
	}
	
	/* TODO: Implement this method */
	/**
	 * 
	 * @param filename
	 * @param docs
	 */
	public void parse(String filename, Collection<WikipediaDocument> docs) {
		/* TODO: pswzyu: 这里需要读取文件内容，然后解析xml， 将每一个page标签中的内容
		 * 实例化为一个WikipediaDocument对象，
		 * 然后还要使用wikipeidaParser对WikipediaDocument继续解析
		 * 然后调用下面的add方法添加到docs中，
		 * 这里实际上没有必要使用下面那个add， 因为这里不是多线程的
		 */
		SAXParser sp = null;
		Inn_MyHandler handler = null;
		try
		{
			//Create a "parser factory" for creating SAX parsers
	        SAXParserFactory spfac = SAXParserFactory.newInstance();
	        //Now use the parser factory to create a SAXParser object
	        sp = spfac.newSAXParser();
	        handler = new Inn_MyHandler(docs);
	        sp.parse(filename, handler);
		}
		catch(ParserConfigurationException | SAXException | IOException e)
		{
			e.printStackTrace();
		}
		
		if (handler != null)
		{
	        
		}


	}
	class Inn_MyHandler extends DefaultHandler
	{
		// document的一些信息，每处理一个新的就覆盖
		int idFromXml = 0;
		String timestampFromXml = "";
		String authorFromXml = "";
		String ttl = "";
		String text = "";
		// 当前节点内部的文字
		String last_string = "";
		// 当栈用， 记录当前节点的path
		LinkedList<String> element_stack;
		
		ConcurrentLinkedQueue<WikipediaDocument> queue;
		public Inn_MyHandler(Collection<WikipediaDocument> docs)
		{
			queue = (ConcurrentLinkedQueue<WikipediaDocument>) docs;
			element_stack = new LinkedList<String>();
		}
		
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// 自动垃圾清理， 所以不用回收原来string
			last_string += new String(ch, start, length);
		}

		@Override
		public void endDocument() throws SAXException {
			System.out.println("endDocument");
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			System.out.print(qName);
			printList(element_stack);
			// 将元素栈的最后一个元素弹出
			// 此时元素栈最后一个元素是当前元素的父节点
			element_stack.removeLast();
			if (element_stack.isEmpty())
			{
				return;
			}
			// 当父节点是page的时候
			if (element_stack.getLast().equals("page"))
			{
				if (qName.equals("title"))
				{
					ttl = last_string;
				}
				if (qName.equals("id"))
				{
					idFromXml = Integer.parseInt(last_string);
				}
			}
			// 当父节点是revision的时候
			if (element_stack.getLast().equals("revision"))
			{
				if (qName.equals("timestamp"))
				{
					timestampFromXml = last_string;
					//javax.xml.bind.DatatypeConverter
					//	.parseDateTime(last_string).getTime();
				}
				if (qName.equals("text"))
				{
					text = last_string;
				}
			}
			if (element_stack.getLast().equals("contributor"))
			{
				if (qName.equals("ip") || qName.equals("username"))
				{
					// ip 和 username同时出现
					if (!authorFromXml.equals(""))
					{
						throw new SAXException("ip 和 username同时出现");
					}
					authorFromXml = last_string;
				}
			}
			
			// page 结束的话就将当前这个wikipediadocument做好放到队列
			if (qName.equals("page"))
			{
				try
				{
					WikipediaDocument temp_d = new WikipediaDocument(idFromXml, timestampFromXml,
							authorFromXml, ttl);
					temp_d.addInitialSection(text);
					queue.add(temp_d);
				}catch(ParseException e)
				{
					e.printStackTrace();
				}
			}
		}

		@Override
		public void startDocument() throws SAXException {
			System.out.println("startDocument");
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			// 每当新page开始的时候就清空一下author， 为了判断是否有一片page同时
			// 有username和ip的情况
			if (qName.equals("page"))
			{
				authorFromXml = "";
			}
			element_stack.addLast(qName);
			// 开始新节点的时候将已经记录的文字节点清空
			last_string = "";
		}
		public void printList(LinkedList<String> l)
		{
			ListIterator<String> i = l.listIterator();
			while(i.hasNext())
			{
				System.out.print(i.next()+"->");
			}
			System.out.print("\n");
		}
		
	}
	
	
	/**
	 * Method to add the given document to the collection.
	 * PLEASE USE THIS METHOD TO POPULATE THE COLLECTION AS YOU PARSE DOCUMENTS
	 * For better performance, add the document to the collection only after
	 * you have completely populated it, i.e., parsing is complete for that document.
	 * @param doc: The WikipediaDocument to be added
	 * @param documents: The collection of WikipediaDocuments to be added to
	 */
	private synchronized void add(WikipediaDocument doc, Collection<WikipediaDocument> documents) {
		documents.add(doc);
	}
}
