package com.iretrieval;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class Utils
{
	public static int countMatches(String regex, String input)
	{
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(normalize(input));
		int count = 0;
		while (m.find())
		{
			count++;
		}
		return count;
	}

	/**
	 * This method is used to load the XML file to a dom4j Document and return
	 * it
	 * 
	 * @param xmlFileName
	 * The path (with the file name) to XML file to be loaded
	 * 
	 * @return dom4j Document
	 */
	public static org.dom4j.Document getDom4jDocument(final String xmlFileName)
	{
		org.dom4j.Document document = null;
		SAXReader reader = new SAXReader();
		try
		{
			document = reader.read(xmlFileName);
		}
		catch (DocumentException e)
		{
			e.printStackTrace();
		}
		return document;
	}

	/**
	 * Prepares string for indexing: removes punctuation, sets case to lower,
	 * etc.
	 * 
	 * @param string
	 * String to be processed
	 * 
	 * @return Normalized string
	 */
	public static String normalize(String string)
	{
		string = string.toLowerCase();
		string = string.replaceAll("[^a-zа-я0-9+#]+", " ");
		string = string.replaceAll("(\\s|^)((\\S{1}|\\d{2})(\\s|\\n|\\r|\\t|$){1})+", " ");
		return string;
	}
}
