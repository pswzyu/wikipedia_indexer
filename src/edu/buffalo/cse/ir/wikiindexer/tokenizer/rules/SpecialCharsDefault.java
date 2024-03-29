package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.SPECIALCHARS)
public class SpecialCharsDefault implements TokenizerRule {
	private final static Character[] removedChar = {'\'', '*','~', ',', '(', ')', '#', '$', '%', '&', ':', ';', '_', '/', '\\', '@', '=', '^', '*', '+', '<', '|', '>', '"', '.', '!' };
	private final static HashSet<Character> removedCharSet = new HashSet<Character>(Arrays.asList(removedChar));
	
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream == null) {
			return;
		}
		stream.reset();
		Pattern replacePattern1 = Pattern.compile("['\\*~\\(\\)\\#$%&:;,_!\"\\=/\\s\\\\]");
		Pattern replacePattern2 = Pattern.compile("^[@\\^\\*\\+\\<\\|\\>\\.!]+");
		Pattern replacePattern3 = Pattern.compile("\\.*$");
		Pattern phoneNumberPattern = Pattern.compile("\\d{3}-\\d{4}");
		while (stream.hasNext()) {
			String tmp = stream.next();
			if (this.containSC(tmp)) {
				if (tmp.length() == 1) {
					stream.previous();
					stream.remove();
					continue;
				}
				tmp = replacePattern1.matcher(tmp).replaceAll("");
//				tmp = tmp.replaceAll("[~\\(\\)\\#$%&:;,_!\"\\=/\\s\\\\]", "");
				tmp = replacePattern2.matcher(tmp).replaceAll("");
//				tmp = tmp.replaceAll("^[@\\^\\*\\+\\<\\|\\>\\.!]", "");
				tmp = replacePattern3.matcher(tmp).replaceAll("");
//				tmp = tmp.replaceAll("\\.*$", "");
//				int length = tmp.length();
//				if (length > 1) {
//					while (tmp.charAt(length - 1) == '.') {
//						tmp = tmp.substring(0, length - 1);
//						length--;
//					}
//				}
				if (tmp == "" || tmp.isEmpty()) {
					stream.previous();
					stream.remove();
					continue;
				}
				stream.previous();
				if (phoneNumberPattern.matcher(tmp).matches()) {
//				if (tmp.matches("\\d{3}-\\d{4}")) {
					stream.set(tmp);
				} else {
					stream.set(tmp.split("[@\\^\\*\\+\\<\\|\\>]"));
				}
				stream.next();
			}
		}
		stream.reset();
	}
	
	private boolean containSC(String s) {
		for (char c : s.toCharArray()) {
			if(this.removedCharSet.contains((Character)c)) {
				return true;
			}
		}
		return false;
	}

}
