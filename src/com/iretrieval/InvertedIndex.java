package com.iretrieval;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class InvertedIndex implements InformationRetrieval
{

	/**
	 * Constructs an inverted index for a given collection of documents
	 * 
	 * @see "Introduction to information retrieval. 1.2 A first take at building"
	 * an inverted index
	 * 
	 * @param documents
	 * Documents collection to index, among documents that are equal will be
	 * indexed only the first one
	 */
	protected InvertedIndex(Collection<? extends Document> documents)
	{
		for (Document document : documents)
		{
			if (document != null && document.getGuid() != null)
			{
				if (documentsCache.put(document.getGuid(), document) == null)
				{
					StringTokenizer st = new StringTokenizer(Utils.normalize(document.getRawText()));
					while (st.hasMoreTokens())
					{
						String term = st.nextToken();
						List<String> postings = postingsList.get(term);
						if (postings == null)
						{
							postings = new LinkedList<String>();
							postingsList.put(term, postings);
						}
						postings.add(document.getGuid());
					}
				}
			}
		}
	}

	/**
	 * Gets the dictionary
	 * 
	 * @return Unmodifiable set of words
	 */
	public Set<String> getDictionary()
	{
		return Collections.unmodifiableSet(postingsList.keySet());
	};

	/**
	 * Retrieves document frequency for the term
	 * 
	 * @see "Introduction to information retrieval. 1.2 A first take at building"
	 * an inverted index
	 * 
	 * @param term
	 * Term to retrieve document frequency for
	 * 
	 * @return Document frequency or 0 if it is impossible to get one
	 */
	public int getDocumentFrequency(String term)
	{
		List<String> postings = postingsList.get(term);
		if (postings != null)
		{
			return postings.size();
		}
		return 0;
	}

	public Document getDocumentFromCache(String guid)
	{
		return documentsCache.get(guid);
	}

	/**
	 * Calculates inverse document frequency for the term
	 * 
	 * @see "Introduction to information retrieval. 6.2.1 Inverse document"
	 * frequency
	 * 
	 * @param term
	 * Term to calculate inverse document frequency for
	 * 
	 * @return Inverse document frequency or 0 if it is impossible to calculate
	 * one
	 */
	public double getInverseDocumentFrequency(String term)
	{
		int documentFrequency = getDocumentFrequency(term);
		if (documentsCache != null && documentsCache.size() > 0)
		{
			return documentFrequency / documentsCache.size();
		}
		return 0.0;
	}

	/**
	 * Calculates tf-idf weight for the term-document pair
	 * 
	 * @see "Introduction to information retrieval. 6.2.2 Tf-idf weighting"
	 * 
	 * @param term
	 * Term from term-document pair to calculate inverse document frequency for
	 * 
	 * @param document
	 * Document from term-document pair to calculate inverse document frequency
	 * for
	 * 
	 * @return tf-idf weight
	 */
	public double getTfIdfWeight(String term, Document document)
	{
		return document.getTermFrequency(term) * getInverseDocumentFrequency(term);
	}

	/**
	 * Retrieves documents that satisfy the query given
	 * 
	 * @param query
	 * Query to retrieve documents for
	 * 
	 * @return List of documents
	 */
	public List<Document> retrieveDocuments(final Query query)
	{
		List<Document> results = new LinkedList<Document>();
		if (query != null)
		{
			for (String term : query.getTerms())
			{
				List<String> postings = postingsList.get(term);
				if (postings != null)
				{
					for (String guid : postings)
					{
						Document document = documentsCache.get(guid);
						if (document != null && !results.contains(document))
						{
							results.add(document);
						}
					}
				}
			}
			Collections.sort(results, new Comparator<Document>()
			{
				public int compare(Document a, Document b)
				{
					return Double.valueOf(a.getTermFrequency(query)).compareTo(
							Double.valueOf(b.getTermFrequency(query)));
				}
			});
			Collections.reverse(results);
		}
		return results;
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

	private Map<String, Document> documentsCache = new HashMap<String, Document>();
	private Map<String, List<String>> postingsList = new TreeMap<String, List<String>>();

	private static volatile Map<String, InvertedIndex> instances = Collections
			.synchronizedMap(new HashMap<String, InvertedIndex>());

}