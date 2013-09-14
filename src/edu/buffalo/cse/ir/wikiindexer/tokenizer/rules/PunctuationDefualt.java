package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.EnglishStemmer.Stemmer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.PUNCTUATION)
public class PunctuationDefualt implements TokenizerRule {

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// 这个rule进行的是分句， 将 .?!并且后边跟大写字母， 或是空白的进行分割
		
	}

}
