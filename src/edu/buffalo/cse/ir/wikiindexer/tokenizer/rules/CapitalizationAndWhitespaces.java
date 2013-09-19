package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

import java.lang.Character;

@RuleClass(className = RULENAMES.PUNCTUATION)

public class CapitalizationAndWhitespaces implements TokenizerRule {

	String whitespaces = "/t/n/x0B/f/r";
	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		stream.reset();
		// 遍历stream中的每一个元素
		while (stream.hasNext())
		{
			String token = stream.next();
			if (token.length() < 2)
				continue;
			int last_cut_point = 0;
			boolean last_word_cap = false;
			boolean first_word = true;
			stream.previous(); // 回到前一个元素， 这样在后边for里面add的时候是正好添加到前面了
			// 循环遍历每一个字符
			for (int step = 1; step != token.length(); ++ step)
			{
				if (whitespaces.indexOf(token.charAt(step)) !=-1 )
				{
					char previous = token.charAt(step-1);
					// 如果上一个字符不是空格
					if ( whitespaces.indexOf(previous) == -1 
							&& step > last_cut_point)
					{
						if (first_word) // 看看是不是句首
							first_word = false;
						
						String trimmed = trimWord( token.substring(last_cut_point, step),
								first_word);
						boolean this_cap = checkCamel(  trimmed );
						stream.add(trimmed);
						if (this_cap && last_word_cap)
							stream.mergeWithPrevious();
						last_word_cap = this_cap;
						last_cut_point = step;
					}
				}
			}
			// \for
			stream.next();
			stream.remove();
		}
	}
	
	public String trimWord(String a, boolean first_word)
	{
		boolean all_upper_case = true;
		boolean all_lower_case = true;
		for (int step = 0; step != a.length(); ++ step) // 先检查一下是不是全都是大写
		{
			if ( Character.isLowerCase(  a.charAt(step)  ))
			{
				all_upper_case = false;
			}else
			{
				all_lower_case = false;
			}
		}
		if (all_lower_case)
			return a.trim();
		if ( !all_upper_case && !first_word && !checkCamel(a)) // 如果不是全大写， 也不是句首
		{
			a.toLowerCase();
		}
		return a.trim();
	}
	
	public boolean checkCamel(String word)
	{
		if (word.length() > 1 && Character.isUpperCase( word.charAt(0) ) )
		{
			return true;
		}
		return false;
	}

}