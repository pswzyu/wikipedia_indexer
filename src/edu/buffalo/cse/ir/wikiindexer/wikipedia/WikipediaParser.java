/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

/**
 * @author nikhillo
 * This class implements Wikipedia markup processing.
 * Wikipedia markup details are presented here: http://en.wikipedia.org/wiki/Help:Wiki_markup
 * It is expected that all methods marked "todo" will be implemented by students.
 * All methods are static as the class is not expected to maintain any state.
 */
public class WikipediaParser {
	/* TODO */
	/**
	 * Method to parse section titles or headings.
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Sections
	 * @param titleStr: The string to be parsed
	 * @return The parsed string with the markup removed
	 */
	/*
	 * @author xcv58
	 * How and where split section? 
	 */
	public static String parseSectionTitle(String titleStr) {
		return null;
	}
	
	/* TODO */
	/**
	 * Method to parse list items (ordered, unordered and definition lists).
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Lists
	 * @param itemText: The string to be parsed
	 * @return The parsed string with markup removed
	 */
	/**
	 * @author xcv58
	 * I cannot find the usage case of definition lists! 
	 */
	public static String parseListItem(String itemText) {
		itemText = itemText.replaceAll("[//*//#]{1,3}","");
		return itemText;
	}
	
	/* TODO */
	/**
	 * Method to parse text formatting: bold and italics.
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Text_formatting first point
	 * @param text: The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTextFormatting(String text) {
		text = text.replaceAll("('''''|'''|'')(.+?)\\1", "$2");
		return null;
	}
	
	/* TODO */
	/**
	 * Method to parse *any* HTML style tags like: <xyz ...> </xyz>
	 * For most cases, simply removing the tags should work.
	 * @param text: The text to be parsed
	 * @return The parsed text with the markup removed.
	 */
	public static String parseTagFormatting(String text) {
		//The commented code is less efficient. But much more precise. The second line can only remove all tags.
//		text = text.replaceAll("(?s)<(.*?)(.*?)>(.*?)</\\1>","$3");
		text = text.replaceAll("<[^<>]*>","");
		return text;
	}
	
	/* TODO */
	/**
	 * Method to parse wikipedia templates. These are *any* {{xyz}} tags
	 * For most cases, simply removing the tags should work.
	 * @param text: The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	/*
	 * @author xcv58
	 * Should retain text within tag?
	 */
	public static String parseTemplates(String text) {
		text = text.replaceAll("\\{{2}([^\\{\\}]*?)\\}{2}","$1");
		return text;
	}
	
	
	/* TODO */
	/**
	 * Method to parse links and URLs.
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Links_and_URLs
	 * @param text: The text to be parsed
	 * @return An array containing two elements as follows - 
	 *  The 0th element is the parsed text as visible to the user on the page
	 *  The 1st element is the link url
	 */
	/**
	 * @author xcv58
	 * Wait for content from WikepediaDocument.
	 * What string exactly this method can got? 
	 * 	I think it should be string that only have one url. And this require pre-process.
	 * And where to store the result Sting[]?
	 * @return
	 */
	public static String[] parseLinks(String text) {
		return null;
	}
	
	
	
}
