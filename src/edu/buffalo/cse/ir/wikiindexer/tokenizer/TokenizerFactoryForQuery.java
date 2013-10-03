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
public class TokenizerFactoryForQuery {
	//private instance, we just want one factory
	private static TokenizerFactoryForQuery factory;
	
	//properties file, if you want to read soemthing for the tokenizers
	private static Properties props;
	
	/**
	 * Private constructor, singleton
	 */
	private TokenizerFactoryForQuery() {
		//TODO: Implement this method
	}
	
	/**
	 * MEthod to get an instance of the factory class
	 * @return The factory instance
	 */
	public static TokenizerFactoryForQuery getInstance(Properties idxProps) {
		if (factory == null) {
			factory = new TokenizerFactoryForQuery();
			props = idxProps;
		}
		
		return factory;
	}
	
	/**
	 * Method to get a fully initialized tokenizer for a given field type
	 * @param field: The field for which to instantiate tokenizer
	 * @return The fully initialized tokenizer
	 */
	public Tokenizer getTokenizer(int type, INDEXFIELD field) {
		if (field == INDEXFIELD.TERM)
		{
			try {
				// 
				if (type == 0)
				{
					return new Tokenizer(new CapitalizationDefault(), new WhitespaceDefault(),
							new ApostropheDefault(), new HyphenDefault(),new SpecialCharsDefault(),
							new AccentsDefault(), new DelimDefault(), new EnglishStemmer(),
							new StopwordsDefault());
				}
				else if (type == 1)
				{
					return new Tokenizer(new CapitalizationDefault(), new ApostropheDefault(),
							new HyphenDefault(),new SpecialCharsDefault(),
							new AccentsDefault(), new DelimDefault(), new EnglishStemmer(),
							new StopwordsDefault());
				}
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
