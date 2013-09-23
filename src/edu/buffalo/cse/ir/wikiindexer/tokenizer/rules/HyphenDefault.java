package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;


@RuleClass(className = RULENAMES.HYPHEN)
public class HyphenDefault implements TokenizerRule {

	public void apply(TokenStream stream) throws TokenizerException {
		if (stream == null)
			return;
		stream.reset();
		while (stream.hasNext()) {
			String tmp = stream.next();
			int indexOfHyphen = tmp.indexOf('-');
			if (indexOfHyphen != -1) {
				if (tmp.matches("[-\\s]+")) {
					stream.previous();
					stream.remove();
				} else if (tmp.matches("(^[\\-\\s]+\\w+)|(\\w+[\\-\\s]+)")) {
					tmp = tmp.replaceAll("[\\-\\s]+", "");
					stream.previous();
					stream.set(tmp);
				} else if (tmp.matches("[A-z]+[\\-\\s]+[A-z]+")) {
					stream.previous();
					stream.set(tmp.substring(0, indexOfHyphen) + " " + tmp.substring(indexOfHyphen + 1));
				}
//				else if (true) {
//					//B-52 | 12-B | 6-6
//					continue;
//				}
			}
		}
	}
}
