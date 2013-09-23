package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.Arrays;
import java.util.HashSet;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;

public class SpecialCharsDefault implements TokenizerRule {
	private final static Character[] removedChar = {'~', '(', ')', '#', '$', '%', '&', ':', ';', '_', '/', '\\', '@', '=', '^', '*', '+', '-', '<', '|', '>' };
	private final static HashSet<Character> removedCharSet = new HashSet<Character>(Arrays.asList(removedChar));


	
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream == null) {
			return;
		}
		
//		~()#$%&:;_/\\
//		@^*+-<|>
		
		while (stream.hasNext()) {
			String tmp = stream.next();
			if (this.containSC(tmp)) {
				if (tmp.length() == 1) {
					stream.previous();
					stream.remove();
					continue;
				}
				tmp = tmp.replaceAll("[~\\(\\)\\#$%&:;_\\=/\\\\]", "");
				tmp = tmp.replaceAll("^[@\\^\\*\\+\\-\\<\\|\\>]", "");
				if (tmp == "" || tmp.isEmpty()) {
					stream.previous();
					stream.remove();
					continue;
				}
				stream.previous();
				if (tmp.matches("\\d{3}-\\d{4}")) {
					stream.set(tmp);
				} else {
					System.out.println(tmp);
					stream.set(tmp.split("[@\\^\\*\\+\\-\\<\\|\\>]"));
				}
//				stream.set(tmp);
				stream.next();
			}
		}
//		stream.set(newValue);
	}
	
	private boolean containSC(String s) {
		for (char c : s.toCharArray()) {
			if(this.removedCharSet.contains(c)) {
				return true;
			}
		}
		return false;
	}

}
