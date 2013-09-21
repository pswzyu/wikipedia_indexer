package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.DATES)
public class DatesDefault {
	final static String defaultYear = "1900";
	final static String defaultMonth = "01";
	final static String defaultDay = "01";
	final static String defaultHour = "00";
	final static String defaultMinute = "00";
	final static String defaultSecond = "00";
	
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream == null)
			return;
		stream.reset();
		while (stream.hasNext()) {
			
		}
	}

}
