package com.iretrieval.index;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import com.iretrieval.Document;
import com.iretrieval.Query;
import com.iretrieval.Utils;

public class Index
{

	/**
	 * Constructs an inverted index for a given collection of documents.
	 * 
	 * @see "Introduction to information retrieval. 1.2 A first take at building
	 * an inverted index."
	 * 
	 * @param documents
	 * Documents collection to index, among documents that are equal will be
	 * indexed only the first one.
	 */
	public Index(Collection<? extends Document> documents)
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
						SortedSet<String> postings = postingsList.get(term);
						if (postings == null)
						{
							postings = new TreeSet<String>();
							postingsList.put(term, postings);
						}
						postings.add(document.getGuid());
					}
				}
			}
		}
	}

	/**
	 * Given a GUID returns cached copy of the document
	 * 
	 * @param guid
	 * <a href="http://en.wikipedia.org/wiki/Globally_Unique_Identifier"
	 * >Globally Unique Identifier</a> of the document
	 * 
	 * @return cached copy of the document
	 */
	public Document getDocumentFromCache(String guid)
	{
		return documentsCache.get(guid);
	};

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
				SortedSet<String> postings = postingsList.get(term);
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
			Collections.sort(results, getDocumentComparator(query));
			Collections.reverse(results);
		}
		return results;
	}

	/**
	 * Gets the dictionary
	 * 
	 * @return Unmodifiable set of words
	 */
	protected Set<String> getDictionary()
	{
		return Collections.unmodifiableSet(postingsList.keySet());
	}

	/**
	 * Return comparator used in {@link #retrieveDocuments(Query)
	 * retrieveDocuments method} to range results. Override this method to
	 * change documents' ranking scheme.
	 * 
	 * @param query
	 * As weight of the document depends on a query it is required when building
	 * comparator
	 * 
	 * @return Comparator object
	 */
	protected Comparator<Document> getDocumentComparator(final Query query)
	{
		return new Comparator<Document>()
		{
			public int compare(Document a, Document b)
			{
				return Double.valueOf(a.getTermFrequency(query)).compareTo(
						Double.valueOf(b.getTermFrequency(query)));
			}
		};
	}

	protected Map<String, Document> documentsCache = new HashMap<String, Document>();
	protected Map<String, SortedSet<String>> postingsList = new TreeMap<String, SortedSet<String>>();

}
