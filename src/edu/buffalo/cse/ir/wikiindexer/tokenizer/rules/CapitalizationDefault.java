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
		boolean isFirstWord = true;
		// 遍历stream中的每一个元素
		while (stream.hasNext()) {
			String token = stream.next();
			if (isFirstWord) {
				isFirstWord = false;
				stream.previous();
				stream.set(this.getLowerCase(token));
				stream.next();
			}
			if (token.matches(".*?[\\.\\!\\?]")) {
				isFirstWord = true;
			}
		}
	}
	
	private String getLowerCase (String s) {
		if (s.matches("[A-Z]*")) {
			return s;
		}
		return s.toLowerCase();
	}

}
