package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

import java.lang.Character;
import java.util.regex.Pattern;

@RuleClass(className = RULENAMES.NUMBERS)
public class NumbersDefault implements TokenizerRule {

	public void apply(TokenStream stream) throws TokenizerException {
		if (stream == null) {
			return;
		}
		stream.reset();
		Pattern skiPattern = Pattern.compile("((-{0,1}\\d{8})|(\\d{2}:\\d{2}:\\d{2})|(-{0,1}\\d{8} \\d{2}:\\d{2}:\\d{2}))\\D*");
		Pattern matchPattern = Pattern.compile("[0-9,\\.\\%\\/]+");
		Pattern replacePattern = Pattern.compile("[0-9\\,\\.]");
		
		// 遍历stream中的每一个元素
		while (stream.hasNext())
		{
			String token = stream.next();
			int firstIndexOfNumber = this.getFirstIndexOfNumber(token);
			if (firstIndexOfNumber == -1) {
				continue;
			}
			if (skiPattern.matcher(token).matches()) {
//			if (token.matches("((-{0,1}\\d{8})|(\\d{2}:\\d{2}:\\d{2})|(-{0,1}\\d{8} \\d{2}:\\d{2}:\\d{2}))\\D*")) {
				continue;
			}
			token = this.removeNumber(token, matchPattern, replacePattern);
			stream.previous();
			if (token == "" || token.isEmpty()) {
				stream.remove();
			} else {
				stream.set(token);
			}
			stream.next();
//			if (token.matches("[0-9,\\.]+")) {
//				stream.previous();
//				stream.remove();
//			} 
//			else if (token.matches(regex))
//			//如果全是数字， 则删除
//			boolean all_num = true;
//			for (int step = 0; step != token.length(); ++ step)
//			{
//				if ( !Character.isDigit( token.charAt(step) ) )
//				{
//					all_num = false;
//				}
//			}
//			if (all_num)
//				stream.remove();
		}
	}
	
	private String removeNumber(String s, Pattern matchPattern, Pattern replacePattern) {
		String tmp = s;
		if (matchPattern.matcher(tmp).matches()) {
//		if (s.matches("[0-9,\\.\\%\\/]+")) {
			tmp = replacePattern.matcher(tmp).replaceAll("");
//			tmp = s.replaceAll("[0-9\\,\\.]", "");
		}
		return tmp;
	}
	
	private int getFirstIndexOfNumber(String s) {
		int tmp = -1;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= '0' && c <= '9') {
				tmp = i;
				return tmp;
			}
		}
		return tmp;
	}
}
