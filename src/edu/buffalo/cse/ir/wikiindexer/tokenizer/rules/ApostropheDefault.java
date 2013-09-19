package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

import java.lang.Character;

@RuleClass(className = RULENAMES.PUNCTUATION)
public class ApostropheDefault implements TokenizerRule {

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// 这个rule进行的是分句， 将 .?!并且后边跟大写字母， 或是空白的进行分割
		stream.reset();
		
		// 遍历stream中的每一个元素
		while (stream.hasNext())
		{
			String token = stream.next();
			int pos = token.indexOf("'");
			if ( pos != -1)
			{
				token = token.replace("'s", "");
				token = token.replace("s'", "");
				token = token.replace("'ve", " have");
				token = token.replace("'m", " am");
				token = token.replace("'ll", " will");
			}
			if (pos == token.length() - 1) // 如果出现在最后则需要去掉
			{
				token = token.substring(0, token.length() - 1);
			}
		}
	}
}
