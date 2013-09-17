/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

/**
 * This class represents a stream of tokens as the name suggests.
 * It wraps the token stream and provides utility methods to manipulate it
 * @author nikhillo
 *
 */
public class TokenStream implements Iterator<String>{
	
	/* 这个类是tokenize进行的场地，要完成append， get token，计数，
	 * 排序，remove，遍历等很多操作，这里需要分别实现两个以对比性能
	 * 一种是使用LinkedList进行其他操作， 而在最后需要排序计数的时候
	 * 		将其转换成ArrayList， 然后进行排序计数
	 * 一种是使用LinkedList和Tree同时进行存储
	 * 
	 * 这里实现的是第一种
	 */
	LinkedList<String> token_pool = new LinkedList<String>();
	ListIterator<String> main_iter;
	HashMap<String, Integer> tokenMap;
	
	/**
	 * Default constructor
	 * @param bldr: THe stringbuilder to seed the stream
	 */
	public TokenStream(StringBuilder bldr) {
		this(bldr.toString());
	}
	
	/**
	 * Overloaded constructor
	 * @param bldr: THe stringbuilder to seed the stream
	 */
	public TokenStream(String string) {
		token_pool.add(string);
		main_iter = token_pool.listIterator();
		tokenMap = new HashMap<String, Integer>(1024);
		tokenMap.put(string, 1);
	}
	
	/**
	 * Method to append tokens to the stream
	 * 少用！！！
	 * @param tokens: The tokens to be appended
	 */
	public void append(String... tokens) {
		
		// list iteroatr 是fail-fast的， 所以只能用一个迭代器，这里就是循环一圈在环回来	
		int next_index = main_iter.nextIndex();
		while (main_iter.hasNext())
		{
			main_iter.next();
		}
		for (int step = 0; step != tokens.length; ++step)
		{
			main_iter.add(tokens[step]);
			this.mapAdd(tokens[step]);
		}
		main_iter = token_pool.listIterator();
		while (main_iter.nextIndex() != next_index)
		{
			main_iter.next();
		}
	}
	
	/**
	 * Method to retrieve a map of token to count mapping
	 * This map should contain the unique set of tokens as keys
	 * The values should be the number of occurrences of the token in the given stream
	 * @return The map as described above, no restrictions on ordering applicable
	 */
	public Map<String, Integer> getTokenMap() {
//		tokenMap.size();
//		HashMap<String, Integer> result = new HashMap<String, Integer>(tokenMap.size());
//		return result;
		/*
		 * @author xcv58
		 * 我认为不需要深克隆了。
		 * 如果需要我在修改这部分代码。
		 */
		return tokenMap;
	}
	
	/**
	 * Method to get the underlying token stream as a collection of tokens
	 * @return A collection containing the ordered tokens as wrapped by this stream
	 * Each token must be a separate element within the collection.
	 * Operations on the returned collection should NOT affect the token stream
	 */
	public Collection<String> getAllTokens() {
		LinkedList<String> ret = (LinkedList<String>)token_pool.clone();
		Collections.copy(ret, token_pool);
		return ret;
	}
	
	/**
	 * Method to query for the given token within the stream
	 * @param token: The token to be queried
	 * @return: THe number of times it occurs within the stream, 0 if not found
	 */
	public int query(String token) {
		Integer result = tokenMap.get(token);
		if (result == null)
			return 0;
		else
			return result;
	}
	
	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasNext() {
		return main_iter.hasNext();
	}
	
	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasPrevious() {
		return main_iter.hasPrevious();
	}
	
	/**
	 * Iterator method: Method to get the next token from the stream
	 * Callers must call the set method to modify the token, changing the value
	 * of the token returned by this method must not alter the stream
	 * @return The next token from the stream, null if at the end
	 */
	public String next() {
		return main_iter.next();
	}
	
	/**
	 * Iterator method: Method to get the previous token from the stream
	 * Callers must call the set method to modify the token, changing the value
	 * of the token returned by this method must not alter the stream
	 * @return The next token from the stream, null if at the end
	 */
	public String previous() {
		return main_iter.previous();
	}
	
	/**
	 * Iterator method: Method to remove the current token from the stream
	 */
	public void remove() {
		String tmp = null;
		if (main_iter.hasNext()) {
			main_iter.next();
			tmp = main_iter.previous();
		} else if (main_iter.hasPrevious()) {
			main_iter.previous();
			tmp = main_iter.next();
		}
		// The blow line is true means main_iter doesn't have previous and next,
		// cannot run remove
		if (null != tmp) {
			this.mapRemove(tmp);
			main_iter.remove();
		} else {
			return;
		}
	}
	
	/**
	 * Method to merge the current token with the previous token, assumes whitespace
	 * separator between tokens when merged. The token iterator should now point
	 * to the newly merged token (i.e. the previous one)
	 * 此方法只能在刚刚调用完next之后使用！！！！
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithPrevious() {
		// 如果根本没有上一个元素， 返回失败
		if (!main_iter.hasPrevious())
			return false;
		String merged_tk = main_iter.previous();
		this.mapRemove(merged_tk);
		String merged_two = main_iter.previous();
		this.mapRemove(merged_two);
		merged_tk = merged_two + merged_tk;
		main_iter.remove();
		main_iter.next();
		main_iter.set(merged_tk);
		this.mapAdd(merged_tk);
		return true;
	}
	
	/*
	 * @author xcv58
	 * Method to remove element from tokenMap.
	 */
	private boolean mapRemove(String s) {
		Integer tmpCount = tokenMap.get(s);
		if (tmpCount == null) {
			tokenMap.put(s, 0);
			System.err.println("ERROR: tokenMap append ERROR. Doesn't have key: " + s);
			return false;
		} else {
			if(tmpCount.equals("1"))
				tokenMap.remove(s);
			else
				tokenMap.put(s, tmpCount - 1);
		}
		return true;
	}

	/*
	 * @author xcv58
	 * Method to add element to tokenMap.
	 */
	private boolean mapAdd(String s) {
		Integer tmpCount = tokenMap.get(s);
		if (tmpCount == null) {
			tokenMap.put(s, 1);
		} else {
			tokenMap.put(s, tmpCount + 1);
		}
		return true;
	}
	/**
	 * Method to merge the current token with the next token, assumes whitespace
	 * separator between tokens when merged. The token iterator should now point
	 * to the newly merged token (i.e. the current one)
	 * 此方法只能在刚刚调用万previous方法后调用！！
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithNext() {
		// 如果根本没有上一个元素， 返回失败
		if (!main_iter.hasNext())
			return false;
		String merged_tk = main_iter.next();
		this.mapRemove(merged_tk);
		String merged_two = main_iter.next();
		this.mapRemove(merged_two);
		merged_tk += merged_two;
		merged_tk = merged_two + merged_tk;
		main_iter.remove();
		main_iter.previous();
		main_iter.set(merged_tk);
		this.mapAdd(merged_tk);
		return true;
	}
	
	/**
	 * Method to replace the current token with the given tokens
	 * The stream should be manipulated accordingly based upon the number of tokens set
	 * It is expected that remove will be called to delete a token instead of passing
	 * null or an empty string here.
	 * The iterator should point to the last set token, i.e, last token in the passed array.
	 * 此方法只能在刚调用完next之后调用！！
	 * @param newValue: The array of new values with every new token as a separate element within the array
	 */
	public void set(String... newValue) {
		if(!main_iter.hasPrevious())
			return;
		main_iter.previous();
		String tmp = main_iter.next();
		this.mapRemove(tmp);
		main_iter.remove();
		for (int step = 0; step != newValue.length; ++step)
		{
			main_iter.add(newValue[step]);
			this.mapAdd(newValue[step]);
		}
	}
	
	/**
	 * Iterator method: Method to reset the iterator to the start of the stream
	 * next must be called to get a token
	 */
	public void reset() {
		main_iter = token_pool.listIterator();
	}
	
	/**
	 * Iterator method: Method to set the iterator to beyond the last token in the stream
	 * previous must be called to get a token
	 * 少用！！！
	 */
	public void seekEnd() {
		main_iter = token_pool.listIterator(token_pool.size());
	}
	
	/**
	 * Method to merge this stream with another stream
	 * @param other: The stream to be merged
	 */
	public void merge(TokenStream other) {
		int other_now = other.main_iter.nextIndex();
		while (other.hasNext())
		{
			String tmp = other.next();
			main_iter.add(tmp);
			this.mapAdd(tmp);
		}
		other.reset();
		while (other.main_iter.nextIndex() != other_now)
		{
			String tmp = other.next();
			main_iter.add(tmp);
			this.mapAdd(tmp);
		}
	}
}
