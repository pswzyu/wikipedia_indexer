package edu.buffalo.cse.ir.wikiindexer.wikipedia.test;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.ListIterator;

import org.junit.Test;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaParser;


public class TokenStreamTest {

	@Test
	public void test() {
		//fail("Not yet implemented");
		//assertEquals("", 0);
		
		TokenStream ts = new TokenStream("1");
		ts.append("2", "3", "4", "5", "6", "7");
		ts.next();
		for (int step = 8; step != 30; ++ step)
		{
			ts.append(Integer.toString(step));
			ts.reset();
		}
		ts.append("30");
		printAll(ts);
		
	}
	public void printAll(TokenStream ts)
	{
		LinkedList<String> a = (LinkedList<String>) ts.getAllTokens();
		ListIterator<String> iter = a.listIterator();
		while (iter.hasNext())
		{
			System.out.print("-"+iter.next()+"-");
		}
		System.out.println("\n");
	}

}
