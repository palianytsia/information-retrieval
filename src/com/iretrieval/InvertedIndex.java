package com.iretrieval;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class InvertedIndex
{

	private InvertedIndex(Collection<Document> documents)
	{
		for (Document document : documents)
		{
			if (document != null && document.getGuid() != null)
			{
				documentsCache.put(document.getGuid(), document);
				StringTokenizer st = new StringTokenizer(document.getRawText());
				String term = st.nextToken();
				Map<String, Integer> postings = postingsList.get(term);
				if (postings == null)
				{
					postings = new HashMap<String, Integer>();
					postingsList.put(term, postings);
				}
				Integer termFrequency = postings.get(document.getGuid());
				if (termFrequency == null)
				{
					postings.put(document.getGuid(), 1);
				}
				else
				{
					++termFrequency;
				}
			}
		}
	}

	public Set<String> getDictionary()
	{
		return Collections.unmodifiableSet(postingsList.keySet());
	};

	public int getDocumentFrequency(String term)
	{
		Map<String, Integer> postings = postingsList.get(term);
		if (postings != null)
		{
			return postings.size();
		}
		return 0;
	}

	public double getInvertedDocumentFrequency(String term)
	{
		int documentFrequency = getDocumentFrequency(term);
		if (documentFrequency > 0 && documentsCache != null)
		{
			return documentFrequency / documentsCache.size();
		}
		return 0.0;
	}

	public int getTermFrequency(String term, Document document)
	{
		Map<String, Integer> postings = postingsList.get(term);
		if (postings != null && document != null)
		{
			Integer termFrequency = postings.get(document.getGuid());
			return termFrequency.intValue();
		}
		return 0;
	}

	public double getTfIdfWeight(String term, Document document)
	{
		return getTermFrequency(term, document) * getInvertedDocumentFrequency(term);
	}

	public static InvertedIndex getInstance(URL feedURL)
	{
		InvertedIndex instance = instances.get(feedURL.toString());
		if (instance == null)
		{
			synchronized (InvertedIndex.class)
			{
				instance = new InvertedIndex(Utils.loadDocuments(feedURL));
			}
		}
		return instance;
	}

	public Set<Document> retrieveDocuments(Query query)
	{
		Set<Document> results = new HashSet<Document>();
		if (query != null)
		{
			for (String term : query.getTerms())
			{
				Map<String, Integer> postings = postingsList.get(term);
				if (postings != null)
				{
					for (String guid : postings.keySet())
					{
						Document document = documentsCache.get(guid);
						if (document != null)
						{
							results.add(document);
						}
					}
				}
			}
		}
		return results;
	}

	private Map<String, Document> documentsCache = new HashMap<String, Document>();
	private Map<String, Map<String, Integer>> postingsList = new TreeMap<String, Map<String, Integer>>();

	private static volatile Map<String, InvertedIndex> instances = Collections
			.synchronizedMap(new HashMap<String, InvertedIndex>());

}
