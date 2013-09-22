package edu.buffalo.cse.ir.wikiindexer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;

public class PlayGround {

	BufferedReader br;
	BufferedWriter bw;
	public static void main(String[] args) {
		//new PlayGround().test();
		System.out.println(new String("12345678").substring(2, 4));
		PlayGround me = new PlayGround();
		me.testFile();
		me.testFile2();
	}
	public void testFile()
	{
		try {
	        File file = new File("./test.txt");
	        if( !file.exists() || file.isDirectory())
	            throw new FileNotFoundException();
	        FileReader fr = new FileReader(file);
	        FileWriter fw = new FileWriter(file);
	        br = new BufferedReader(fr);
	        bw = new BufferedWriter(fw);
	        String temp = null;
	        StringBuffer sb = new StringBuffer();
	        bw.write("1w2123131231");
	        bw.flush();
			temp = br.readLine();
	        while(temp != null)
	        {
	            sb.append(temp+" ");
	            temp = br.readLine();
	        }
	        System.out.println("test1:"+sb.toString());
		} catch (IOException e) {
			System.out.println("test1:err");
			e.printStackTrace();
		}
	}
	public void testFile2()
	{
		try {
	        String temp = null;
	        StringBuffer sb = new StringBuffer();
	        //bw.write("1w2123131231");
	        //bw.flush();
			temp = br.readLine();
	        while(temp != null)
	        {
	            sb.append(temp+" ");
	            temp = br.readLine();
	        }
	        System.out.println("test2:"+sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void aaaa()
	{
		// 测试一下LinkedList的remove操作删除的是哪个元素: 删除当前的元素， 删除的是刚刚next得到的元素
		// 然后指针指向next得到的元素之前的那个元素
		LinkedList<String> test_list = new LinkedList<String>();
		test_list.add("1");
		test_list.add("2");
		test_list.add("3");
		test_list.add("4");
		test_list.add("5");
		// 测试两个独立的iterator会不会互相影响:不会
		ListIterator list_iter1 = test_list.listIterator();
		ListIterator list_iter2 = test_list.listIterator();
		
		list_iter1.next();
		System.out.println("now:"+list_iter1.next());
		list_iter1.remove();
		printList(test_list);
		System.out.println("now:"+list_iter1.next());
		
		test_list.add("10");
		//System.out.println(list_iter1.next());
		//System.out.println(list_iter2.next());
		//System.out.println(list_iter1.next());
		// 测试iterator返回的是引用还是clone
		String a = (String) list_iter1.next();
		System.out.println(a);
		a = "100";
		printList(test_list);
		
//		System.out.println(list_iter1.next());
//		System.out.println(list_iter1.previous());
//		System.out.println(list_iter1.next());
//		System.out.println(list_iter1.previous());
//		System.out.println(list_iter1.next());
//		System.out.println(list_iter1.previous());
		list_iter1.set("111");
		printList(test_list);
		ListIterator list_iter3 = test_list.listIterator(test_list.size());
		System.out.println(list_iter3.previous());
	}
	public static void printList(LinkedList<String> a)
	{
		for (ListIterator<String> iter = a.listIterator();
				iter.hasNext(); )
		{
			System.out.print(iter.next() + "-");
		}
		System.out.print("\n");
	}
	public void test() {
		//fail("Not yet implemented");
		//assertEquals("", 0);
		
		TokenStream ts = new TokenStream("1");
		ts.append("2", "3", "4", "5", "6", "7");
		ts.next();
		for (int step = 8; step != 15; ++ step)
		{
			ts.append(Integer.toString(step));
			//ts.reset();
		}
		ts.append("15");
		ts.append("15");
		printAll(ts);
		printNext(ts);
		printMap(ts.getTokenMap());
		printNext(ts);
		print("query:"+ts.query("15") + "\n");
		printNext(ts);
		ts.mergeWithNext();
		printAll(ts);
		printNext(ts);
		ts.mergeWithPrevious();
		printAll(ts);
		printNext(ts);
		ts.set("7.1", "7.2", "7.3");
		printAll(ts);
		printNext(ts);
		TokenStream ts2 = new TokenStream("8.1");
		ts2.append("8.2", "8.3");
		ts.merge(ts2);
		printAll(ts);
		printAll(ts2);
		printNext(ts);
		printNext(ts2);
	}
	public void printAll(TokenStream ts)
	{
		LinkedList<String> a = (LinkedList<String>) ts.getAllTokens();
		ListIterator<String> iter = a.listIterator();
		while (iter.hasNext())
		{
			System.out.print("-"+iter.next()+"-");
		}
		System.out.print("\n");
	}
	public void printMap(Map<String, Integer> a)
	{
		Set<String> aa = a.keySet();
		Iterator<String> aaa = aa.iterator();
		while (aaa.hasNext())
		{
			String key = aaa.next();
			System.out.print("-"+key+":"+a.get(key).toString()+"-");
		}
		System.out.print("\n");
	}
	public void printNext(TokenStream ts)
	{
		print("Next:"+ts.next()+"\n");
	}
	public void print(String a)
	{
		System.out.print(a);
	}

}
