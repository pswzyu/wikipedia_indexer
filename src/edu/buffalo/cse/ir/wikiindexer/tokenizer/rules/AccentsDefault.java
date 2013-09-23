package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.text.Normalizer;
import java.text.Normalizer.Form;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;


@RuleClass(className = RULENAMES.ACCENTS)
public class AccentsDefault implements TokenizerRule {

	public void apply(TokenStream stream) throws TokenizerException {
		if (stream == null)
			return;
		stream.reset();
		while (stream.hasNext()) {
			String token = stream.next();
//			String tmp = Normalizer.normalize(token, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+","");
			String tmp = Normalizer.normalize(token, Form.NFD);
			tmp = tmp.replaceAll("[^\\p{ASCII}]", "");
//			.replaceAll("\\p{InCombiningDiacriticalMarks}+","");
			if(!token.equals(tmp)) {
				stream.previous();
				stream.set(tmp);
				stream.next();
			}
		}
	}
}
