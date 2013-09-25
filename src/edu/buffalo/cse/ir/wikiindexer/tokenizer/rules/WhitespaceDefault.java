package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

import java.lang.Character;

@RuleClass(className = RULENAMES.PUNCTUATION)

public class WhitespaceDefault implements TokenizerRule {

//	String whitespaces = "/t/n/x0B/f/r";
	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream == null) {
			return;
		}
		stream.reset();
		while (stream.hasNext()) {
			String tmp = stream.next();
			tmp = tmp.replaceAll("(?s)^[\\s\\t\\n\\r]+", "");
			String[] result = tmp.split("(?s)[\\s\\t\\n\\r]+");
			stream.previous();
			stream.set(result);
			stream.next();
		}
		stream.reset();
//		// 遍历stream中的每一个元素
//		while (stream.hasNext())
//		{
//			String token = stream.next();
//			if (token.length() < 2)
//				continue;
//			int last_cut_point = 0;
//			stream.previous(); // 回到前一个元素， 这样在后边for里面add的时候是正好添加到前面了
//			// 循环遍历每一个字符
//			for (int step = 1; step != token.length(); ++ step)
//			{
//				if (whitespaces.indexOf(token.charAt(step)) !=-1 )
//				{
//					char previous = token.charAt(step-1);
//					// 如果上一个字符不是空格
//					if ( whitespaces.indexOf(previous) == -1 
//							&& step > last_cut_point)
//					{
//						stream.add( token.substring(last_cut_point, step).trim() );
//						last_cut_point = step;
//					}
//				}
//			}
//			// \for
//			stream.next();
//			stream.remove();
//		}
	}

}
