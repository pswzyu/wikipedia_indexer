package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

import java.lang.Character;

@RuleClass(className = RULENAMES.PUNCTUATION)
public class SentenceSpliter implements TokenizerRule {

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// 这个rule进行的是分句， 将 .?!并且后边跟大写字母， 或是空白的进行分割
		stream.reset();
		
		// 遍历stream中的每一个元素
		while (stream.hasNext())
		{
			String token = stream.next();
			if (token.length() < 2)
				continue;
			int last_cut_point = 0;
			stream.previous(); // 回到前一个元素， 这样在后边for里面add的时候是正好添加到前面了
			// 循环遍历每一个字符
			for (int step = 0; step != token.length() - 1; ++ step)
			{
				if (inArray(token.charAt(step)) )
				{
					char next = token.charAt(step+1);
					// 如果下一个字符是空格， 或者大写字母
					if (next == ' ' || Character.isUpperCase(next) 
							&& step > last_cut_point)
					{
						stream.add( removeRedundent(
								token.substring(last_cut_point, step)   )   );
						last_cut_point = step+1;
					}
				}
			}
			stream.add( removeRedundent(
					token.substring(last_cut_point, token.length())   )   );
			// \for
			stream.remove();
		}
		
	}
	public String removeRedundent(String t)
	{
		int start_point = 0;
		int stop_point = t.length();
		
		for (int step = 0; step != t.length(); ++step)
		{
			if ( !inArray(t.charAt(step)) )
			{
				start_point = step;
				break;
			}
		}
		for (int step = t.length() - 1; step != -1; -- step)
		{
			if ( !inArray(t.charAt(step)) )
			{
				stop_point = step+1;
				break;
			}
		}
		if (stop_point > start_point)
		{
			return t.substring(start_point, stop_point);
		}else
		{
			return "";
		}
	}
	public boolean inArray(char ch)
	{
		if (ch == '.' || ch == '?' || ch == '!')
			return true;
		else
			return false;
	}
}
