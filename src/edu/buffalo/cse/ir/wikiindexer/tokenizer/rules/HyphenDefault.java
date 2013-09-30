package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;


@RuleClass(className = RULENAMES.HYPHEN)
public class HyphenDefault implements TokenizerRule {

	public void apply(TokenStream stream) throws TokenizerException {
		if (stream == null){
			return;
		}
		stream.reset();
		Pattern existHyphen = Pattern.compile("[–\\-\\s]+");
		Pattern hyphenOneside = Pattern.compile("(^[–\\-\\s]+\\w+)|(\\w+[–\\-\\s]+)");
		Pattern hyphenCenter = Pattern.compile("[A-z]+[–\\-\\s]+[A-z]+");
		Pattern replaceAllHyphen = Pattern.compile("[–\\-\\s]+");
		while (stream.hasNext()) {
			String tmp = stream.next();
			int indexOfHyphen = tmp.indexOf('-');
			if (indexOfHyphen != -1) {
				if (existHyphen.matcher(tmp).matches()) {
//				if (tmp.matches("[–\\-\\s]+")) {
					stream.previous();
					stream.remove();
				} else if (hyphenOneside.matcher(tmp).matches()) {
//				} else if (tmp.matches("(^[–\\-\\s]+\\w+)|(\\w+[–\\-\\s]+)")) {
					tmp = replaceAllHyphen.matcher(tmp).replaceAll("");
//					tmp = tmp.replaceAll("[–\\-\\s]+", "");
					stream.previous();
					stream.set(tmp);
				} else if (hyphenCenter.matcher(tmp).matches()) {
//				} else if (tmp.matches("[A-z]+[–\\-\\s]+[A-z]+")) {
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
