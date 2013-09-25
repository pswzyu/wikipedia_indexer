/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer;

import java.util.Properties;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.AccentsDefault;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.ApostropheDefault;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.CapitalizationAndWhitespaces;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.CapitalizationDefault;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.DatesDefault;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.DelimDefault;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.EnglishStemmer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.HyphenDefault;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.NumbersDefault;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.PunctuationDefault;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.SentenceSpliter;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.SpecialCharsDefault;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.StopwordsDefault;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.WhitespaceDefault;

/**
 * Factory class to instantiate a Tokenizer instance
 * The expectation is that you need to decide which rules to apply for which field
 * Thus, given a field type, initialize the applicable rules and create the tokenizer
 * @author nikhillo
 *
 */
public class TokenizerFactory {
	//private instance, we just want one factory
	private static TokenizerFactory factory;
	
	//properties file, if you want to read soemthing for the tokenizers
	private static Properties props;
	
	/**
	 * Private constructor, singleton
	 */
	private TokenizerFactory() {
		//TODO: Implement this method
	}
	
	/**
	 * MEthod to get an instance of the factory class
	 * @return The factory instance
	 */
	public static TokenizerFactory getInstance(Properties idxProps) {
		if (factory == null) {
			factory = new TokenizerFactory();
			props = idxProps;
		}
		
		return factory;
	}
	
	/**
	 * Method to get a fully initialized tokenizer for a given field type
	 * @param field: The field for which to instantiate tokenizer
	 * @return The fully initialized tokenizer
	 */
	public Tokenizer getTokenizer(INDEXFIELD field) {
		//TODO: Implement this method
		/*
		 * For example, for field F1 I want to apply rules R1, R3 and R5
		 * For F2, the rules are R1, R2, R3, R4 and R5 both in order
		 * So the pseudo-code will be like:
		 * if (field == F1)
		 * 		return new Tokenizer(new R1(), new R3(), new R5())
		 * else if (field == F2)
		 * 		return new TOkenizer(new R1(), new R2(), new R3(), new R4(), new R5())
		 * ... etc
		 */
		if (field == INDEXFIELD.TERM)
		{
			try {
				// 分句，大小写，空白，apostrophe， hyphen， special char，dates，num，accent
				// delim， stem， stopword
				return new Tokenizer(new SentenceSpliter(), new CapitalizationAndWhitespaces(),
						new ApostropheDefault(), new HyphenDefault(),
						new SpecialCharsDefault(), new DatesDefault(), new NumbersDefault());
				//		new AccentsDefault(), new DelimDefault(), new EnglishStemmer(),
				//		new StopwordsDefault());
			} catch (TokenizerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (field == INDEXFIELD.AUTHOR)
		{
			try {
				return new Tokenizer(new WhitespaceDefault(), new CapitalizationDefault());
			} catch (TokenizerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (field == INDEXFIELD.CATEGORY)
		{
			try {
				return new Tokenizer(new WhitespaceDefault(), new CapitalizationDefault());
			} catch (TokenizerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else // LINK
		{
			try {
				return new Tokenizer(new PunctuationDefault());
			} catch (TokenizerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
