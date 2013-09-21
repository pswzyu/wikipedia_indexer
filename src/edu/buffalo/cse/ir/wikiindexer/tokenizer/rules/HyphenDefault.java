package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;
import java.util.regex.*;

@RuleClass(className = RULENAMES.HYPHEN)
public class HyphenDefault implements TokenizerRule {

	public void apply(TokenStream stream) throws TokenizerException {
		stream.reset();
		while (stream.hasNext()) {
			String tmp = stream.next();
			int indexOfHyphen = tmp.indexOf('-');
//			int lastIndexOfHyphen = tmp.lastIndexOf('-');
			if (indexOfHyphen != -1) {
				if (tmp.matches("[-\\s]+")) {
					stream.remove();
				} else if (tmp.matches("(^[-]+\\w+)|(\\w+[-]+)")) {
					tmp = tmp.replaceAll("[-]", "");
					stream.set(tmp);
				} else if (tmp.matches("^[a-Z]+[-]+[a-Z]+")) {
					stream.set(tmp.substring(0, indexOfHyphen), tmp.substring(indexOfHyphen + 1));
				} else if (true) {
					//B-52 | 12-B | 6-6
					continue;
				}
			}
		}
	}
}
