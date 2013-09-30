package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

import java.lang.Character;
import java.util.regex.Pattern;

@RuleClass(className = RULENAMES.PUNCTUATION)
public class ApostropheDefault implements TokenizerRule {

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream == null) {
			return;
		}
		// 这个rule进行的是分句， 将 .?!并且后边跟大写字母， 或是空白的进行分割
		stream.reset();
		Pattern replacePattern1 = Pattern.compile("let's");
		Pattern replacePattern2 = Pattern.compile("'s");
		Pattern replacePattern3 = Pattern.compile("s'");
		Pattern replacePattern4 = Pattern.compile("'m");
		Pattern replacePattern5 = Pattern.compile("'re");
		Pattern replacePattern6 = Pattern.compile("'ve");
		Pattern replacePattern7 = Pattern.compile("'d");
		Pattern replacePattern8 = Pattern.compile("'ll");
		Pattern replacePattern9 = Pattern.compile("'em");
		Pattern replacePattern10 = Pattern.compile("won't");
		Pattern replacePattern11 = Pattern.compile("shan't");
		Pattern replacePattern12 = Pattern.compile("n't");
		Pattern replacePattern13 = Pattern.compile("'");
		while (stream.hasNext()) {
			String tmp = stream.next();
			if (!tmp.contains("'")) {
				continue;
			}
			boolean containSpace = false;
			if (tmp.contains(" ")) {
				containSpace = true;
			}
			tmp = replacePattern1.matcher(tmp).replaceAll("let us");
			tmp = replacePattern2.matcher(tmp).replaceAll("");
			tmp = replacePattern3.matcher(tmp).replaceAll("s");
			tmp = replacePattern4.matcher(tmp).replaceAll(" am");
			tmp = replacePattern5.matcher(tmp).replaceAll(" are");
			tmp = replacePattern6.matcher(tmp).replaceAll(" have");
			tmp = replacePattern7.matcher(tmp).replaceAll(" would");
			tmp = replacePattern8.matcher(tmp).replaceAll(" will");
			tmp = replacePattern9.matcher(tmp).replaceAll("them");
			tmp = replacePattern10.matcher(tmp).replaceAll("will not");
			tmp = replacePattern11.matcher(tmp).replaceAll("shall not");
			tmp = replacePattern12.matcher(tmp).replaceAll(" not");
			tmp = replacePattern13.matcher(tmp).replaceAll("");

//			tmp = tmp.replaceAll("let's", "let us");
//			tmp = tmp.replaceAll("'s", "");
//			tmp = tmp.replaceAll("s'", "s");
//			tmp = tmp.replaceAll("'m", " am");
//			tmp = tmp.replaceAll("'re", " are");
//			tmp = tmp.replaceAll("'ve", " have");
//			tmp = tmp.replaceAll("'d", " would");
//			tmp = tmp.replaceAll("'ll", " will");
//			tmp = tmp.replaceAll("'em", "them");
//			tmp = tmp.replaceAll("won't", "will not");
//			tmp = tmp.replaceAll("shan't", "shall not");
//			tmp = tmp.replaceAll("n't", " not");
//			tmp = tmp.replaceAll("'", "");
			stream.previous();
			if (containSpace) {
				stream.set(tmp);
			} else {
				stream.set(tmp.split("\\s+"));
			}
			stream.next();
		}
		
		// 遍历stream中的每一个元素
//		while (stream.hasNext())
//		{
//			String token = stream.next();
//			int pos = token.indexOf("'");
//			if ( pos != -1)
//			{
//				token = token.replace("'s", "");
//				token = token.replace("s'", "");
//				token = token.replace("'ve", " have");
//				token = token.replace("'m", " am");
//				token = token.replace("'ll", " will");
//			}
//			if (pos == token.length() - 1) // 如果出现在最后则需要去掉
//			{
//				token = token.substring(0, token.length() - 1);
//			}
//		}
	}
}
