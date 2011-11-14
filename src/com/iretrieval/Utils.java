package com.iretrieval;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Element;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class Utils
{

	/**
	 * Creates a set of documents from files located at directory specified
	 * 
	 * @param feedUrl URL of RSS feed containing items to be source for
	 *        documents.
	 * 
	 * @return Set of documents, if provided path contains files to create
	 *         documents from and creation was successful. Returns empty set
	 *         otherwise.
	 */
	@SuppressWarnings("unchecked")
	public static Set<Document> loadDocuments(URL feedUrl)
	{
		Set<Document> docs = new HashSet<Document>();
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
				Document document = new Document(guid);

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
}
