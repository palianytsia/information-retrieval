package com.iretrieval;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZonedIndex extends InvertedIndex implements InformationRetrieval
{
	public static ZonedIndex getInstance(URL feedURL)
	{
		ZonedIndex instance = instances.get(feedURL.toString());
		if (instance == null)
		{
			synchronized (ZonedIndex.class)
			{
				instance = new ZonedIndex(Utils.loadDocuments(feedURL));
			}
		}
		return instance;
	}

	private static volatile Map<String, ZonedIndex> instances = Collections
			.synchronizedMap(new HashMap<String, ZonedIndex>());
	
	protected ZonedIndex(Collection<ZonedDocument> documents)
	{
		super(documents);
	}
	
	@Override
	public List<Document> retrieveDocuments(final Query query)
	{
		List<Document> results = super.retrieveDocuments(query);
		Collections.sort(results, new Comparator<Document>()
		{
			public int compare(Document a, Document b)
			{
				return Double.valueOf(((ZonedDocument) a).getWeightedZoneScore(query)).compareTo(
						Double.valueOf(((ZonedDocument) b).getWeightedZoneScore(query)));
			}
		});
		Collections.reverse(results);
		return results;
	}
}
