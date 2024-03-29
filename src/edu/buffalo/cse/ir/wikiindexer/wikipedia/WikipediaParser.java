/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.*;


/**
 * @author nikhillo
 * This class implements Wikipedia markup processing.
 * Wikipedia markup details are presented here: http://en.wikipedia.org/wiki/Help:Wiki_markup
 * It is expected that all methods marked "todo" will be implemented by students.
 * All methods are static as the class is not expected to maintain any state.
 */
public class WikipediaParser {
	
	/*
	 * 构造方法， 传入解析了一半的的WikipediaDocument， 还有词条的实际文本，
	 * 使用下面的静态方法对实际文本进行处理，然后将文本添加到队列中
	 * @param wikidoc: wikipediaDocument object that has been parsed from the xml file
	 * @param wikitext: String object of the explaining text(should be parse by the static
	 * 	functions and add to wikidoc through addSection method)
	 * @param parser: Parser object, I need this reference to call the add method!
	 * @param docsqueue: add to this queue. silly framework!
	 */
	/*
	 * 用来分割section， 注意：由于junit测试要检验下面的parseSectionTitle， 所以这里必须
	 * 将section分好， 比如分成 "==SB== hahacontent" 然后“==SB==”这个部分用parseSectionTitle
	 * 去掉等号， 然后添加到linkedlist返回
	 * 如果文章没有section， 则返回一个title为Default的section
	 */
	/*
	 * All requirement done.
	 * 传入的text需要预先处理好其他的Markup标记；
	 * 但最终每个Section的部分还是需要去除一下Section标记，因为subsection的存在。
	 */
	private final static Pattern textFormattingPattern = Pattern.compile("('''''|'''|'')([^']+?)\\1");
	private final static Pattern splitSectionPattern = Pattern.compile("(^|(?<=\n))(={2,6})[^=]+?\\2");
	private final static Pattern replaceSectionTitlePattern = Pattern.compile("(^|(?<=\n))(={1,6})\\s*([^=]+?)\\s*\\2");
	private final static Pattern parseListItemPattern1 = Pattern.compile("[//*//#]{1,4}\\s*");
	private final static Pattern parseListItemPattern2 = Pattern.compile("(^|(?<=\n)):\\s*");
	private final static Pattern parseCategoryPattern = Pattern.compile("\\[\\[Category:[^\\[\\]]+?\\]\\]");
	private final static Pattern replaceCategoryPattern = Pattern.compile("[\\|\\s]*\\]\\]$");
	private final static Pattern replaceTagFormatPattern1 = Pattern.compile("((^|(?>=\n))<[^<>]*?>\\s*)|\\s*<[^<>]*?>");
	private final static Pattern replaceTagFormatPattern2 = Pattern.compile("((^|(?>=\n))(&lt;)[^<>]*?(&gt;)\\s*)|\\s*(&lt;)[^<>]*?(&gt;)");
	private final static Pattern parseTemplatesPattern = Pattern.compile("(?s).*\\{{2}.*\\}{2}.*");
	private final static Pattern replaceTemplatesPattern = Pattern.compile("(?s)\\{{2}.*?\\}{2}");
	private final static Pattern parseLinksPattern1 = Pattern.compile("\\[.*?\\]+");
	private final static Pattern parseLinksPattern2 = Pattern.compile("^\\[\\[[a-z]{2}:.*\\]\\]");
	private final static Pattern replaceBracketsPattern = Pattern.compile("\\s*\\(.*\\)");
	private final static Pattern replaceSquareBracketsPattern = Pattern.compile("[\\[\\]]+");
	private final static Pattern replaceSpacePattern = Pattern.compile("\\s+");
	public static ArrayList<String> splitSection(String text)
	{
		ArrayList<String> array = new ArrayList<String>();
		Matcher m = splitSectionPattern.matcher(text);
//		Matcher m = Pattern.compile("(^|(?<=\n))(={2,6})[^=]+?\\2").matcher(text);
		int tmpAnchor = 0;
		String tmp = "";
		while(m.find())
		{
			if (tmpAnchor == 0) {
				tmpAnchor = m.start();
				array.add("Default");
				array.add(text.substring(0, tmpAnchor));
//				tmpMap.put("Default", text.substring(0, tmpAnchor));
			}
			if(tmp != "" || !tmp.isEmpty())
			{
				array.add(WikipediaParser.parseSectionTitle(tmp));
				array.add(text.substring(tmpAnchor, m.start()));
//				tmpMap.put(WikipediaParser.parseSectionTitle(tmp), text.substring(tmpAnchor, m.start()));
			}
			tmp = m.group();
			tmpAnchor = m.end();
		}
		
		if(tmp != "" || !tmp.isEmpty())
		{
			array.add(WikipediaParser.parseSectionTitle(tmp));
			array.add(text.substring(tmpAnchor));
//			tmpMap.put(WikipediaParser.parseSectionTitle(tmp), text.substring(tmpAnchor));
		}
		else
		{
			array.add("Default");
			array.add(WikipediaParser.parseSectionTitle(text));
		}
		return array;
	}
	
	/* TODO */
	/**
	 * Method to parse section titles or headings.
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Sections
	 * @param titleStr: The string to be parsed
	 * @return The parsed string with the markup removed
	 */
	public static String parseSectionTitle(String titleStr) {
		if(titleStr==null)
			return null;
		titleStr = replaceSectionTitlePattern.matcher(titleStr).replaceAll("$3");
		return titleStr;
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
	 * Add code to handle definition lists.
	 */
	public static String parseListItem(String itemText) {
		if(itemText == null)
			return null;
		itemText = parseListItemPattern1.matcher(itemText).replaceAll("");
		itemText = parseListItemPattern2.matcher(itemText).replaceAll("");
		/*
		 * 下边的代码是针对Wikipedia Markup页面上来写的。
		 * 但TA给的TEST代码里只是简单地处理:开头的句子，并没有考虑;的情况。
		 */
//		Matcher m = Pattern.compile("(^|(?<=\n));.*?\n(:.*\n)*").matcher(itemText);
//		while(m.find())
//		{
//			String tmp = m.group();
//			String tmpResult = tmp.replaceAll(";\\s*(.*?)\\s*:", "$1");
//			tmpResult = tmpResult.replaceAll("(^|(?<=\n))[;:]\\s*", "");
//			itemText = itemText.replace(tmp, tmpResult);
//		}

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
		if(text == null)
			return null;
		text = textFormattingPattern.matcher(text).replaceAll("$2");
//		text = text.replaceAll("('''''|'''|'')([^']+?)\\1", "$2");
		return text;
	}
	
	
	public static Collection<String> parseCategories(String text) {
		ArrayList<String> result = new ArrayList<String>();
		Matcher m = parseCategoryPattern.matcher(text);
		while (m.find()) {
			String tmp = m.group();
			tmp = tmp.substring(11);
			tmp = replaceCategoryPattern.matcher(tmp).replaceAll("");
//			tmp = tmp.replaceAll("[\\|\\s]*\\]\\]$", "");
			result.add(tmp);
		}
//		[[Category:People from Glasgow| ]]
		return result;
	}
	/* TODO */
	/**
	 * Method to parse *any* HTML style tags like: <xyz ...> </xyz>
	 * For most cases, simply removing the tags should work.
	 * @param text: The text to be parsed
	 * @return The parsed text with the markup removed.
	 */
	public static String parseTagFormatting(String text) {
		if(text==null)
			return null;
		text = replaceTagFormatPattern1.matcher(text).replaceAll("");
		text = replaceTagFormatPattern2.matcher(text).replaceAll("");
		return text;
	}
	
	/* TODO */
	/**
	 * Method to parse wikipedia templates. These are *any* {{xyz}} tags
	 * For most cases, simply removing the tags should work.
	 * @param text: The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTemplates(String text) {
		if(text == null) {
			return null;
		}
		while (parseTemplatesPattern.matcher(text).matches()) {
			text = replaceTemplatesPattern.matcher(text).replaceAll("");
		}
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
	/*
	 * pswzyu: 我觉得link应该这样实现：首先去掉所有的htmllink， 这些不用做index
	 * 
	 * A forward index that maps the different Wikipedia pages referenced by	
 	 * a given page
 	 * 
 	 * 然后对于像[[Other Page Title|Name show on this page]]这样的链接需要将后边的
 	 * Name show on this page保存在当前的章节中， 将Other Page Title放到link列表中
	 */
	/* 
	 * @author xcv58
	 * I think someone asked the same question and I responded as if you had k links, 
	 * you could make the array return either 
	 * [link text1, link text 2, ...,link text k, link url1, link url2,...,link url k] 
	 * or in pairs 
	 * [link text1, link url 1, link text 2, link url 2,...] etc. 
	 * We wont evaluate it for such a condition, so if you change the behaviour, it should be fine.
	 * Other alternative is to ensure you pass only one link at a time to this method. 
	 * You could use some logic to split your input string with multiple links 
	 * into fragments of single links, and then call this method on each fragment
	 */
	public static String[] parseLinks(String text) {
		if ((null == text) || (text.equals(""))) {
			String[] emptyResult = new String[2];
			emptyResult[0] = "";
			emptyResult[1] = "";
			return emptyResult;
		}
		String textWithoutTag = WikipediaParser.parseTagFormatting(text);
		ArrayList<String> tmpArray = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		tmpArray.add("");
		int start = 0;
		int end = 0;
		Matcher m = parseLinksPattern1.matcher(textWithoutTag);
		while (m.find()) {
			String tmp = m.group();
			start = m.start();
			sb.append(textWithoutTag.substring(end, start));
			end = m.end();
			if (tmp.startsWith("[http:")) {
				int tmpIndex = tmp.indexOf(' ');
				if (tmpIndex == -1) {
					tmpArray.add("");
				} else {
					String tmpText = tmp.substring(tmpIndex + 1,
							tmp.indexOf(']'));
					sb.append(tmpText);
					tmpArray.add("");
					// sb.append(WikipediaParser.getVisibleText(tmpText));
				}
			} else if (tmp.startsWith("Category:", 2) || tmp.startsWith("Wikipedia:", 2)) {
				sb.append(WikipediaParser.getVisibleText(tmp));
				tmpArray.add("");
			} else if (tmp.startsWith(":Category:", 2)) {
				sb.append(WikipediaParser.getVisibleText(tmp));
				tmpArray.add("");
			} else if (tmp.startsWith("File:", 2) || tmp.startsWith("media:", 2)) {
				int lastVb = tmp.lastIndexOf('|');
				if(tmp.lastIndexOf('|') == -1) {
					sb.append("");
				} else if((tmp.lastIndexOf('|') + 3 == tmp.length()) && (tmp.indexOf('|') == lastVb)) {
					sb.append(tmp.substring(2, lastVb));
				} else {
					sb.append(WikipediaParser.getVisibleText(tmp));
				}
				tmpArray.add("");
			} else if (tmp.startsWith("Wiktionary:", 2)) {
				int lastVb = tmp.lastIndexOf('|');
				if((tmp.lastIndexOf('|') + 3 == tmp.length()) && (tmp.indexOf('|') == lastVb)) {
					tmp = tmp.substring(0, tmp.length() - 3) + tmp.substring(tmp.length() - 2);
				}
				if(tmp.lastIndexOf('|') == -1) {
					if(tmp.lastIndexOf(':') == 12) {
						sb.append(tmp.substring(2, tmp.length()-2));
					} else {
						sb.append(tmp.substring(13, tmp.length() - 2));
					}
				} else {
					sb.append(WikipediaParser.getVisibleText(tmp));
				}
				tmpArray.add("");
			} else if (parseLinksPattern2.matcher(tmp).matches()) {
				sb.append(tmp.substring(2, tmp.length() - 2));
				tmpArray.add("");
			} else {
				sb.append(WikipediaParser.getVisibleText(tmp));
				tmpArray.add(WikipediaParser.getUrlRegulation(tmp));
			}
		}
		sb.append(textWithoutTag.substring(end));
		tmpArray.set(0, sb.toString());
		String[] finalResult = (String[]) tmpArray.toArray(new String[tmpArray.size()]);
		return finalResult;
	}


	/*
	 * @author xcv58
	 * @param text: The tagged text
	 * @return The visible text
	 */
	private static String getVisibleText(String text) {
		String result = "";
		int length = text.length();
		if(text.startsWith("[[")) {
			text = replaceSquareBracketsPattern.matcher(text).replaceAll("");
//			text = text.substring(2, length - 2);
			text = replaceBracketsPattern.matcher(text).replaceAll("");
		}
		length = text.length();
		
		char[] tmpChars = text.toCharArray();
		int indexOfVb = -1;
		int indexOfComma = -1;
		int indexOfPound = -1;
		int indexOfColon = -1;
		for (int i = 0; i < length; i++) {
			if (indexOfVb == -1 && tmpChars[i] == '|') {
				indexOfVb = i;
			}
			if (indexOfComma == -1 && tmpChars[i] == ',') {
				indexOfComma = i;
			}
			if (indexOfPound == -1 && tmpChars[i] == '#') {
				indexOfPound = i;
			}
			if (indexOfColon == -1 && tmpChars[i] == ':') {
				indexOfColon = i;
			}
		}
		
		if(indexOfVb != -1) {
			if(indexOfVb + 1 == length) {
				length -= 1;
				text = text.substring(0, length);
				return WikipediaParser.getVisibleText(text);
			} else {
				result = text.substring(text.lastIndexOf('|') + 1);
				return result;
			}
		} else {
			if(indexOfPound != -1) {
				return text;
			}
			if(indexOfColon == -1) {
				
			} else if(indexOfColon == 0) {
				result = text.substring(1);
				return result;
			} else {
				result = text.substring(text.lastIndexOf(':') + 1);
				return result;
			}
			if (indexOfComma != -1) {
				result = text.substring(0, indexOfComma);
				return result;
			}
			result = text;
		}
		return result;
	}
	
	/*
	 * @author xcv58
	 * @param url: The url to be regulate
	 * @return The regulation of url.
	 */
	private static String getUrlRegulation(String url) {
		String result = "";
		int length = url.length();
		if(url.startsWith("[[")) {
			url = replaceSquareBracketsPattern.matcher(url).replaceAll("");
		}
		length = url.length();
		int indexOfVb = url.indexOf("|");
		if(indexOfVb != -1) {
			result = url.substring(0, indexOfVb);
		} else {
			result = url;
		}
		result = replaceSpacePattern.matcher(result).replaceAll("_");
		if (result.length() > 1) {
			result = Character.toUpperCase(result.charAt(0)) + result.substring(1);
		}
		return result;
	}
	
	
}
