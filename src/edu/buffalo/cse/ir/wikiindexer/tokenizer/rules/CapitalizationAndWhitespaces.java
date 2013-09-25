package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

import java.lang.Character;

@RuleClass(className = RULENAMES.CAPITALIZATION)

public class CapitalizationAndWhitespaces implements TokenizerRule {

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream == null){
			return;
		}
		stream.reset();
		// 遍历stream中的每一个元素
		while (stream.hasNext())
		{
			String token = stream.next();
			if (token.length() < 2)
				continue;
			int last_cut_point = -1;
			boolean last_word_cap = false;
			boolean first_word = true;
			stream.previous(); // 回到前一个元素， 这样在后边for里面add的时候是正好添加到前面了
			// 循环遍历每一个字符
			for (int step = 0; step != token.length()-1; ++ step)
			{
				if ( inArray(token.charAt(step)) ) // 如果这个是空格， 后边的不是空格
				{
					char next = token.charAt(step+1);
					if ( !inArray(next) )
					{
						String trimmed = trimWord( token.substring(last_cut_point+1, step),
								first_word);
						boolean this_cap = checkCamel(  trimmed );
						if (this_cap && last_word_cap)
						{
							stream.add(" "+trimmed);
							stream.mwpHumanUse();
						}else
						{
							stream.add(trimmed);
						}
						last_word_cap = this_cap;
						last_cut_point = step;
						if (first_word) // 看看是不是句首
							first_word = false;
					}
				}
			}
			// 最后一个词
			String trimmed = trimWord( token.substring(last_cut_point+1, token.length()),
					first_word);
			boolean this_cap = checkCamel(  trimmed );
			stream.add(trimmed);
			if (this_cap && last_word_cap)
				stream.mwpHumanUse();
			// \for
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
	public boolean inArray(char ch)
	{
		if (ch == '\t' || ch == '\n' || ch == '\f' || ch == '\r' || ch ==' ')
			return true;
		else
			return false;
	}

}