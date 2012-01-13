package com.iretrieval;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.jdom.Element;

import com.iretrieval.index.Index;
import com.iretrieval.index.IndexType;
import com.iretrieval.index.InvertedIndex;
import com.iretrieval.index.ZonedIndex;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class SearchEngine
{
	public static Index getIndex(URL feedURL, IndexType type)
	{
		Map<IndexType, Index> group = indexes.get(feedURL);
		if (group == null)
		{
			group = new HashMap<IndexType, Index>();
			indexes.put(feedURL, group);
		}

		Index index = group.get(type);
		if (index == null)
		{
			Collection<Document> documents = loadDocuments(feedURL);
			if (type.equals(IndexType.ZonedIndex))
			{
				Collection<ZonedDocument> zonedDocuments = new HashSet<ZonedDocument>();
				for (Document document : documents)
				{
					zonedDocuments.add(new ZonedDocument(document));
				}
				index = new ZonedIndex(zonedDocuments);
			}
			else if (type.equals(IndexType.InvertedIndex))
			{
				index = new InvertedIndex(documents);
			}
			else
			{
				index = new Index(documents);
			}
		}
		group.put(type, index);

		return index;
	}

	public static void main(String[] args)
	{
		String source = null;
		URL feedURL = null;
		IndexType intexType = IndexType.BasicIndex;

		for (int i = 0; i < args.length - 1; i++)
		{
			if (args[i].equals("-s"))
			{
				source = args[i + 1];
			}
			else if (args[i].equals("-t"))
			{
				intexType = IndexType.valueOf(args[i + 1]);
			}
		}
		if (source == null)
		{
			System.err.println("You should launch the program with -s [feedURL], "
					+ "where feedURL is a link to RSS feed describing the "
					+ "documents to be indexed. Program will terminate now.");
			System.exit(-1);
		}

		try
		{
			feedURL = new URL(source);
		}
		catch (MalformedURLException e)
		{
			System.err.println("MalformedURL was given as source for the index: " + source
					+ ". Program will terminate now.");
			System.exit(-1);
		}

		if (feedURL != null)
		{
			Index index = getIndex(feedURL, intexType);
			System.out.println("Index is build. Now you are able to run "
					+ "queries and retrieve documents. Type exit to quit.");
			Scanner in = new Scanner(System.in);
			String command = "";
			while (!command.equals("exit"))
			{
				System.out.print("Query> ");
				if (in.hasNextLine())
				{
					command = in.nextLine();
					if (!command.equals("exit"))
					{
						int i = 0;
						for (Document document : index.retrieveDocuments(new Query(command)))
						{
							System.out.println(++i + ") " + document.toString());
						}
					}
				}
			}
		}
	}

	/**
	 * Creates a set of documents from RSS feed
	 * 
	 * @param feedUrl
	 * URL of RSS feed containing items to be source for documents.
	 * 
	 * @return Set of documents, if provided path contains files to create
	 * documents from and creation was successful. Returns empty set otherwise.
	 */
	@SuppressWarnings("unchecked")
	private static Collection<Document> loadDocuments(URL feedUrl)
	{
		System.out.println(feedUrl);
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

	private static Map<URL, Map<IndexType, Index>> indexes = new HashMap<URL, Map<IndexType, Index>>();

}