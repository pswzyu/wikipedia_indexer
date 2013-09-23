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
import java.util.SortedMap;
import java.util.TreeMap;

import edu.buffalo.cse.ir.wikiindexer.indexer.IdAndOccurance;
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
		
		TreeMap<Integer, LinkedList<IdAndOccurance> > idx
			= new TreeMap<Integer, LinkedList<IdAndOccurance> >();
		idx.put(3, new LinkedList<IdAndOccurance>());
		idx.get(3).add(new IdAndOccurance(3, 4));
		LinkedList<IdAndOccurance> a = idx.get(3);
		Iterator ii = a.iterator();
		while (ii.hasNext())
		{
			IdAndOccurance o = (IdAndOccurance)(ii.next());
			print(Integer.toString(o.getOcc())+"\n");
		}
		new PlayGround().testMerge();
	}
	public void testMerge()
	{
		LinkedList<IdAndOccurance> li = new LinkedList<IdAndOccurance>();
		li.add(new IdAndOccurance(5, 1));
		li.add(new IdAndOccurance(9, 1));
		li.add(new IdAndOccurance(10, 1));
		li.add(new IdAndOccurance(11, 1));
		String[] split2 = {"12,1"};
		ListIterator<IdAndOccurance> iter = li.listIterator();
		int list_id_now = 0;
		IdAndOccurance t = null;
		for (int step1 = 0; step1 != split2.length; ++step1)
		{
			String[] split3 = split2[step1].split(","); // 0->id,1->occ
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
		printList1(li);
	}
	public static void printList1(LinkedList<IdAndOccurance> a)
	{
		for (ListIterator<IdAndOccurance> iter = a.listIterator();
				iter.hasNext(); )
		{
			IdAndOccurance t = iter.next();
			System.out.print( t.id + ":" + t.occ + "  ;  ");
		}
		System.out.print("\n");
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
	public static void print(String a)
	{
		System.out.print(a);
	}

}
