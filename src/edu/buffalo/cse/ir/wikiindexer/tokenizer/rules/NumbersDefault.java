package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

import java.lang.Character;

@RuleClass(className = RULENAMES.PUNCTUATION)
public class NumbersDefault implements TokenizerRule {

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream == null) {
			return;
		}
		stream.reset();
		
		// 遍历stream中的每一个元素
		while (stream.hasNext())
		{
			String token = stream.next();
			int firstIndexOfNumber = this.getFirstIndexOfNumber(token);
			if (firstIndexOfNumber == -1) {
				continue;
			}
			token = this.removeNumber(token);
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
	
	private String removeNumber(String s) {
		String tmp = s;
		if (s.matches("[0-9,\\.\\%\\/]+")) {
			tmp = s.replaceAll("[0-9\\,\\.]", "");
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
