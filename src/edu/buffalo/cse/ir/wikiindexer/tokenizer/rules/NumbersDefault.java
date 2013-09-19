package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

import java.lang.Character;

@RuleClass(className = RULENAMES.PUNCTUATION)
public class NumbersDefault implements TokenizerRule {

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		stream.reset();
		
		// 遍历stream中的每一个元素
		while (stream.hasNext())
		{
			String token = stream.next();
			//如果全是数字， 则删除
			boolean all_num = true;
			for (int step = 0; step != token.length(); ++ step)
			{
				if ( !Character.isDigit( token.charAt(step) ) )
				{
					all_num = false;
				}
			}
			if (all_num)
				stream.remove();
		}
	}
}
