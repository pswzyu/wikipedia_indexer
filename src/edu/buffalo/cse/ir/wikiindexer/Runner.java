/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Callable;     
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;     
import java.util.concurrent.Executors;     
import java.util.concurrent.Future;

import org.junit.runner.Computer;
import org.junit.runner.JUnitCore;

import edu.buffalo.cse.ir.wikiindexer.IndexerConstants.RequiredConstant;
import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.parsers.Parser;
import edu.buffalo.cse.ir.wikiindexer.test.AllTests;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.Tokenizer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerFactory;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.DocumentTransformer;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.IndexableDocument;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument;

/**
 * @author nikhillo
 * Entry class into the indexer code. Check the printUsage() method or
 * the provided documentation on how to invoke this class.
 */
public class Runner {

	// 控制同步transform的线程数量
	final static int THREAD_CONCURRENT = 1;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			printUsage();
			System.exit(1);
		} else {
			if (args[0] != null && args[0].length() > 0) {
				String filename = args[0];
				Properties properties = loadProperties(filename);
				if (properties == null) {
					System.err.println("Error while loading the Properties file. Please check the messages above and try again");
					System.exit(2);
				} else  {
					if (args[1] != null && args[1].length() == 2) {
						String mode = args[1].substring(1).toLowerCase();
						
						if ("t".equals(mode)) {
							runTests(filename);
						} else if ("i".equals(mode)) {
							runIndexer(properties);
						} else if ("b".equals(mode)) {
							runTests(filename);
							runIndexer(properties);
						} else {
							System.err.println("Invalid mode specified!");
							printUsage();
							System.exit(4);
						}	
					}
				}
			} else {
				System.err.println("The provided properties filename is empty or could not be read");
				printUsage();
				System.exit(3);
			}	
		}
	}
	
	/**
	 * Method to print the correct usage to run this class.
	 */
	private static void printUsage() {
		System.err.println("The usage is: ");
		System.err.println("java edu.buffalo.cse.ir.wikiindexer.Runner <filename> <flag>");
		System.err.println("where - ");
		System.err.println("filename: Fully qualified file name from which to load the properties");
		System.err.println("flag: one amongst the following -- ");
		System.err.println("-t: Only execute tests");
		System.err.println("-i: Only run the indexer");
		System.err.println("-b: Run both, tests first then indexer");
		
	}
	
	/**
	 * Method to run the full indexer
	 * @param properties: The properties file to run with
	 */
	private static void runIndexer(Properties properties) {
		Parser parser = new Parser(properties);
		ConcurrentLinkedQueue<WikipediaDocument> queue = new ConcurrentLinkedQueue<WikipediaDocument>();
		parser.parse(FileUtil.getDumpFileName(properties), queue);
		
		//initialize tokenizers
		Map<INDEXFIELD, Tokenizer> ifmap = new HashMap<INDEXFIELD, Tokenizer>();
		TokenizerFactory tfact = TokenizerFactory.getInstance(properties);
		for (INDEXFIELD idxf : INDEXFIELD.values()) {
			ifmap.put(idxf, tfact.getTokenizer(idxf));
		}
		
		
		
		//TODO: More code to be added here!
		/* TODO: pswzyu：上边queue是已经从文件读取并从xml中解析出来的词条内容，
		 * 接下来应该使用多线程的方式启动几个DocumentTransformer来进行index工作
		 * 新建一个indexaleDocument对象的集合， 用来放传回来的结果
		 * 
		 * 最后使用indexableDocument生成index文件
		 */
		Iterator<WikipediaDocument> queue_iter = queue.iterator();
		// 好神奇的callable线程管理方法！！
		// 新建一个future数组， 用来存放每个线程返回的indexabledocument
		ArrayList<Future> future_array = new ArrayList<Future>(queue.size());
		// 新建线程池
		ExecutorService es = Executors.newFixedThreadPool(THREAD_CONCURRENT);
		while (queue_iter.hasNext())
		{
			future_array.add(es.submit(
					new DocumentTransformer(ifmap, queue_iter.next()) ));
		}
		
		// 使用future.get()获取indexabledocument对象
		ArrayList<IndexableDocument> idxable_doc =
				new ArrayList<IndexableDocument>(queue.size());
		
		for (Iterator<Future> iter = future_array.iterator();
				iter.hasNext(); )
		{
			try {
				idxable_doc.add( (IndexableDocument)(iter.next().get()) );
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		es.shutdownNow();
		
		/**************
		 * pswzyu添加用来测试的
		 */
		
		/**************
		 * 
		 */
		
	}

	/**
	 * Method to execute all tests
	 * @param filename: Filename for the properties file
	 */
	private static void runTests(String filename) {
		System.setProperty("PROPSFILENAME", filename);
		JUnitCore core = new JUnitCore();
		core.run(new Computer(), AllTests.class);
		
	}
	
	/**
	 * Method to load the Properties object from the given file name
	 * @param filename: The filename from which to load Properties
	 * @return The loaded object
	 */
	private static Properties loadProperties(String filename) {

		try {
			Properties props = FileUtil.loadProperties(filename);
			
			if (validateProps(props)) {
				return props;
			} else {
				System.err.println("Some properties were either not loaded or recognized. Please refer to the manual for more details");
				return null;
			}
		} catch (FileNotFoundException e) {
			System.err.println("Unable to open or load the specified file: " + filename);
		} catch (IOException e) {
			System.err.println("Error while reading properties from the specified file: " + filename);
		}
		
		return null;
	}
	
	/**
	 * Method to validate that the properties object has been correctly loaded
	 * @param props: The Properties object to validate
	 * @return true if valid, false otherwise
	 */
	private static boolean validateProps(Properties props) {
		/* Validate size */
		if (props != null && props.entrySet().size() == IndexerConstants.NUM_PROPERTIES) {
			/* Get all required properties and ensure they have been set */
			Field[] flds = IndexerConstants.class.getDeclaredFields();
			boolean valid = true;
			Object key;
			
			for (Field f : flds) {
				if (f.isAnnotationPresent(RequiredConstant.class) ) {
					try {
						key = f.get(null);
						if (!props.containsKey(key) || props.get(key) == null) {
							System.err.println("The required property " + f.getName() + " is not set");
							valid = false;
						}
					} catch (IllegalArgumentException e) {
						
					} catch (IllegalAccessException e) {
						
					}
				}
			}
			
			return valid;
		}
		
		return false;
	}

}
