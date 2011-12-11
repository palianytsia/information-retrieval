package com.iretrieval;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Element;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class Utils
{
	public static int countMatches(String regex, String input)
	{
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		int count = 0;
		while (m.find())
		{
			count++;
		}
		return count;
	}

	/**
	 * Creates a set of documents from files located at directory specified
	 * 
	 * @param feedUrl
	 * URL of RSS feed containing items to be source for documents.
	 * 
	 * @return Set of documents, if provided path contains files to create
	 * documents from and creation was successful. Returns empty set otherwise.
	 */
	@SuppressWarnings("unchecked")
	public static Collection<ZonedDocument> loadDocuments(URL feedUrl)
	{
		Set<ZonedDocument> docs = new HashSet<ZonedDocument>();
		List<SyndEntry> items = new ArrayList<SyndEntry>();
		try
		{
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed sf = input.build(new XmlReader(feedUrl));
			items.addAll(sf.getEntries());
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return docs;
		}

		for (SyndEntry item : items)
		{
			String guid = item.getUri();
			if (guid != null && !guid.isEmpty())
			{
				ZonedDocument document = new ZonedDocument(guid);

				String title = item.getTitle();
				if (title != null && !title.isEmpty())
				{
					document.setTitle(title);
				}

				SyndContent description = item.getDescription();
				if (description != null && description.getValue() != null
						&& !description.getValue().isEmpty())
				{
					document.setDescription(description.getValue());
				}

				String link = item.getLink();
				if (link != null && !link.isEmpty())
				{
					document.setLink(link);
				}

				Date pubDate = item.getPublishedDate();
				if (pubDate != null)
				{
					document.setPubDate(pubDate);
				}

				Collection<SyndCategory> categories = item.getCategories();
				if (categories != null)
				{
					for (SyndCategory category : categories)
					{
						if (category != null && category.getName() != null
								&& !category.getName().isEmpty())
						{
							document.addCategory(category.getName());
						}
					}
				}

				List<Element> extraFields = (List<Element>) item.getForeignMarkup();
				for (Element extraField : extraFields)
				{
					if (extraField != null && extraField.getNamespaceURI() != null
							&& extraField.getName() != null && extraField.getValue() != null
							&& !extraField.getNamespaceURI().isEmpty()
							&& !extraField.getName().isEmpty() && !extraField.getValue().isEmpty())
					{
						document.addExtraField(extraField.getNamespaceURI(), extraField.getName(),
								extraField.getValue());
					}
				}

				docs.add(document);
			}
		}

		return docs;
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
