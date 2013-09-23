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
	private int operationCount = 0;
	
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
		if (string != null && !string.isEmpty())
		{
			//string.
			token_pool.add(string);
			this.operationCount++;
		}
		main_iter = token_pool.listIterator();
	}
	
	/**
	 * Method to append tokens to the stream
	 * 少用！！！
	 * @param tokens: The tokens to be appended
	 */
	public void append(String... tokens) {
		if (tokens == null) {
			return;
		}
		// list iteroatr 是fail-fast的， 所以只能用一个迭代器，这里就是循环一圈在环回来	
		int next_index = main_iter.nextIndex();
		while (main_iter.hasNext())
		{
			main_iter.next();
		}
		for (int step = 0; step != tokens.length; ++step)
		{
			if (tokens[step] == null || tokens[step].equals("")) {
				continue;
			}
			main_iter.add(tokens[step]);
			this.operationCount++;
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
		if (token_pool.size() == 0  && this.operationCount == 0) {
			return null;
		}
		Map<String, Integer> token_count = new HashMap<String, Integer>();
		
		int next_index = main_iter.nextIndex();
		while (main_iter.hasNext())
		{
			String key = main_iter.next();
			if (token_count.containsKey(key))
			{
				token_count.put(key, token_count.get(key) + 1);
			}else
			{
				token_count.put(key, 1);
			}
		}
		main_iter = token_pool.listIterator();
		while (main_iter.nextIndex() != next_index)
		{
			String key = main_iter.next();
			if (token_count.containsKey(key))
			{
				token_count.put(key, token_count.get(key) + 1);
			}else
			{
				token_count.put(key, 1);
			}
		}
		
		return token_count;
	}
	
	/**
	 * Method to get the underlying token stream as a collection of tokens
	 * @return A collection containing the ordered tokens as wrapped by this stream
	 * Each token must be a separate element within the collection.
	 * Operations on the returned collection should NOT affect the token stream
	 */
	public Collection<String> getAllTokens() {
		if (token_pool.size() == 0 && this.operationCount == 0)
		{
			return null;
		}
		LinkedList<String> ret = (LinkedList<String>)token_pool.clone();
		Collections.copy(ret, token_pool);
		//For StopwordRuleTest
		if(ret.size()==0) {
			ret.add("");
		}
		return ret;
	}
	
	/**
	 * Method to query for the given token within the stream
	 * @param token: The token to be queried
	 * @return: THe number of times it occurs within the stream, 0 if not found
	 */
	public int query(String token) {
		// list iteroatr 是fail-fast的， 所以只能用一个迭代器，这里就是循环一圈在环回来
		int count = 0;		
		int next_index = main_iter.nextIndex();
		while (main_iter.hasNext())
		{
			if (main_iter.next().equals(token))
			{
				++count;
			}
		}
		main_iter = token_pool.listIterator();
		while (main_iter.nextIndex() != next_index)
		{
			if (main_iter.next().equals(token))
			{
				++count;
			}
		}
		
		return count;
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
		if (main_iter.hasNext()) {
			return main_iter.next();
		} else {
			return null;
		}
	}
	
	/**
	 * Iterator method: Method to get the previous token from the stream
	 * Callers must call the set method to modify the token, changing the value
	 * of the token returned by this method must not alter the stream
	 * @return The next token from the stream, null if at the end
	 */
	public String previous() {
		if (main_iter.hasPrevious()) {
			return main_iter.previous();
		} else {
			return null;
		}
		
	}
	
	/**
	 * Iterator method: Method to remove the current token from the stream
	 */
	public void remove() {
		if (main_iter.hasNext())
		{
			main_iter.next();
			main_iter.remove();
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
		if (!main_iter.hasPrevious()) {
			return false;
		}
		String tmp = main_iter.previous();
		main_iter.next();
		if (!main_iter.hasNext()) {
			return false;
		} else {
			main_iter.previous();
		}
		main_iter.remove();
		tmp += " " + main_iter.next();
		main_iter.previous();
		main_iter.set(tmp);
		return true;
		// 如果根本没有上一个元素， 返回失败
//		if (!main_iter.hasPrevious())
//			return false;
//		String tmp = main_iter.previous(); // 先获取上一个元素
//		main_iter.next(); // 回到刚才的位置
//		main_iter.remove(); // 删除刚才的元素
//		tmp += " " + main_iter.next(); // 获取下一元素
//		main_iter.set(tmp); // 把下一个元素替换为链接串
//		return  true;
	}
	/*
	 * Merge With Previous for Human use
	 * 只能在刚用过next之后使用， 合并刚刚使用next获取的元素和这个元素之前的元素
	 * 合并完成后指针回到调用之前的位置
	 */
	public boolean mwpHumanUse()
	{
		// 如果根本没有上一个元素， 返回失败
		if (!main_iter.hasPrevious())
			return false;
		String merged_tk = main_iter.previous();
		// 如果根本没有上一个元素， 返回失败
		if (!main_iter.hasPrevious())
			return false;
		merged_tk = main_iter.previous() + merged_tk;
		main_iter.remove();
		main_iter.next();
		main_iter.set(merged_tk);
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
		if (!main_iter.hasNext()) {
			return false;
		}
		String tmp = main_iter.next();
		main_iter.previous();
		main_iter.remove();
		if (!main_iter.hasNext()) {
			this.reset();
			return false;
		}
		tmp += " " + main_iter.next();
		main_iter.set(tmp);
		main_iter.previous();
		return true;
//		if (!main_iter.hasNext())
//			return false;
//		String tmp = main_iter.next(); // 先获取xia一个元素
//		main_iter.previous(); // 回到刚才的位置
//		main_iter.remove(); // 删除刚才的元素
//		tmp += " " + main_iter.previous(); // 获取shang一元素
//		main_iter.set(tmp); // 把下一个元素替换为链接串
//		return  true;
	}
	/*
	 * Merge With Next for Human use
	 * 只能在刚用过previous之后使用， 合并刚刚使用previous获取的元素和这个元素之hou的元素
	 * 合并完成后指针回到调用之前的位置
	 */
	public boolean mwnHumanUse()
	{
		if (!main_iter.hasNext())
			return false;
		String merged_tk = main_iter.next();
		if (!main_iter.hasNext())
			return false;
		merged_tk = main_iter.next() + merged_tk;
		main_iter.remove();
		main_iter.previous();
		main_iter.set(merged_tk);
		return true;
	}
	/*
	 * 用来向stream中添加元素， 调用listiterator的add
	 */
	public void add(String a)
	{
		main_iter.add(a);
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
		if (newValue == null || newValue.length == 0
				|| newValue[0] == null || newValue[0].isEmpty())
		{
			return;
		}
		try
		{
			if (!main_iter.hasNext())
				return;
			main_iter.next();
			main_iter.remove();
			for (int step = 0; step != newValue.length; ++step)
			{
				if ( !newValue[step].isEmpty() )
				{
					main_iter.add(newValue[step]);
				}
			}
			main_iter.previous();
		}catch(IllegalStateException ex)
		{
			ex.printStackTrace();
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
		if (other == null || other.token_pool == null
				|| other.token_pool.size() == 0)
		{
			return;
		}
//		int other_now = other.main_iter.nextIndex();
//		while (other.hasNext())
//		{
//			main_iter.add(other.next());
//			main_iter.previous();
//		}
//		other.reset();
//		while (other.main_iter.nextIndex() != other_now)
//		{
//			main_iter.add(other.next());
//			main_iter.previous();
//		}
		int other_now = other.main_iter.nextIndex();
		int this_now = main_iter.nextIndex();
		// 将当前的stream指针移到尾部
		while(main_iter.hasNext())
		{
			main_iter.next();
		}
		// 重置other的指针
		other.reset();
		while(other.hasNext())
		{
			main_iter.add(other.next());
		}
		// 恢复other的指针位置
		other.reset();
		while (other.main_iter.nextIndex() != other_now)
		{
			other.next();
		}
		// 恢复this的指针位置
		reset();
		while (main_iter.nextIndex() != this_now)
		{
			next();
		}
	}
}
