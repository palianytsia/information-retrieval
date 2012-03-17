package com.iretrieval;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;

public class Utils
{
	/**
	 * Counts how many times term occurs in haystack string
	 * 
	 * @param needle
	 * Term to count occurrences for.
	 * 
	 * @param haystack
	 * The input string.
	 * 
	 * @return Term frequency of the term in the haystack.
	 */
	public static int countTerms(String term, String haystack)
	{
		Pattern p = Pattern.compile("\\b" + term + "\\b", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(haystack);
		int count = 0;
		while (m.find())
		{
			count++;
		}
		return count;
	}

	/**
	 * Prepares string for indexing: strips tags, removes punctuation, removes
	 * trailing and leading whitespace, sets case to lower, etc.
	 * 
	 * @param string
	 * String to be processed. If null is passed returns an empty string.
	 * 
	 * @return Normalized string
	 */
	public static String normalize(String string)
	{
		if (string == null) {
			return "";
		}
		string = Jsoup.parse(string).text();
		string = string.toLowerCase();
		string = string.replaceAll("[^a-zа-я0-9+#]+", " ");
		string = string.replaceAll("(\\s|^)((\\S{1}|\\d{2})(\\s|\\n|\\r|\\t|$){1})+", " ");
		return string.trim();
	}
}
