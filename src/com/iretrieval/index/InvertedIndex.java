package com.iretrieval.index;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.iretrieval.Document;
import com.iretrieval.Query;

public class InvertedIndex extends Index
{

	public InvertedIndex(Collection<? extends Document> documents)
	{
		super(documents);
	}

	/**
	 * Retrieves document frequency for the term.
	 * 
	 * @see "Introduction to information retrieval. 1.2 A first take at building"
	 * an inverted index.
	 * 
	 * @param term
	 * Term to retrieve document frequency for.
	 * 
	 * @return Document frequency or 0 if it is impossible to get one.
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

	/**
	 * Calculates inverse document frequency for the term.
	 * 
	 * @see "Introduction to information retrieval. 6.2.1 Inverse document".
	 * frequency
	 * 
	 * @param term
	 * Term to calculate inverse document frequency for.
	 * 
	 * @return Inverse document frequency or 0 if it is impossible to calculate
	 * one.
	 */
	public double getInverseDocumentFrequency(String term)
	{
		int documentFrequency = getDocumentFrequency(term);
		if (documentsCache != null && documentsCache.size() > 0)
		{
			double value = documentFrequency / (double) documentsCache.size();
			return value;
		}
		return 0.0;
	}

	/**
	 * Calculates tf-idf weight of the document against the given query.
	 * 
	 * @see "Introduction to information retrieval. 6.2.2 Tf-idf weighting"
	 * 
	 * @param query
	 * Query containing terms to calculate inverse document frequency for, that
	 * then will be used for calculation tf-idf weight for the query-document
	 * pair.
	 * 
	 * @param document
	 * Document to calculate tf-idf weigh for.
	 * 
	 * @return tf-idf weight.
	 */
	public double getTfIdfWeight(Document document, Query query)
	{
		double weight = 0.0;
		for (String term : query.getTerms())
		{
			weight += document.getTermFrequency(term) * getInverseDocumentFrequency(term);
		}
		return weight;
	}

	@Override
	protected Comparator<Document> getDocumentComparator(final Query query)
	{
		return new Comparator<Document>()
		{
			public int compare(Document a, Document b)
			{
				return Double.valueOf(getTfIdfWeight(a, query)).compareTo(
						Double.valueOf(getTfIdfWeight(b, query)));
			}
		};
	}

}