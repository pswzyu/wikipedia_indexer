package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

import java.lang.Character;

@RuleClass(className = RULENAMES.PUNCTUATION)

public class CapitalizationDefault implements TokenizerRule {

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		stream.reset();
		// 遍历stream中的每一个元素
		if (stream.hasNext())
		{
			String token = stream.next();
			stream.previous();
			stream.set(token.toLowerCase());
			stream.next();
		}
	}

}
