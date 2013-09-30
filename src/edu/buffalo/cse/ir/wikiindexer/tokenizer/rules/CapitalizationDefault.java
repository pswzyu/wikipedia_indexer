package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

import java.lang.Character;
import java.util.regex.Pattern;

@RuleClass(className = RULENAMES.PUNCTUATION)

public class CapitalizationDefault implements TokenizerRule {

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream == null){
			return;
		}
		stream.reset();
		Pattern pattern = Pattern.compile(".*?[\\.\\!\\?]");
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
			if (pattern.matcher(token).matches()) {
//			if (token.matches(".*?[\\.\\!\\?]")) {
				isFirstWord = true;
			}
		}
	}
	
	private String getLowerCase (String s) {
		int countOfUpChar = 0;
		for (char c:s.toCharArray()) {
			if (c >= 'A' && c <= 'Z') {
				countOfUpChar += 1;
			}
		}
		if (countOfUpChar == 0) {
			return s;
		} else if (countOfUpChar == 1 && (s.charAt(0) >= 'A' && s.charAt(0) <= 'Z')) {
			return s.toLowerCase();
		}
		System.out.println(s);
		return s;
	}

}
