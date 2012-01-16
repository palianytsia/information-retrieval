package com.iretrieval.index;

import java.util.Collection;
import java.util.Comparator;

import com.iretrieval.Document;
import com.iretrieval.Query;

public class VectorSpaceIndex extends InvertedIndex
{

	public VectorSpaceIndex(Collection<? extends Document> documents)
	{
		super(documents);
	}

	protected double getCosineSimilarity(Document document, Query query)
	{
		double cosineSimilarity = 0.0;
		for (String term : query.getTerms())
		{
			double inverseDocumentFrequency = getInverseDocumentFrequency(term);
			double termFrequency = document.getTermFrequency(term);
			double euclideanLength = getEuclideanLength(document);
			cosineSimilarity += inverseDocumentFrequency * (termFrequency / euclideanLength);
		}
		return cosineSimilarity;
	}

	@Override
	protected Comparator<Document> getDocumentComparator(final Query query)
	{
		return new Comparator<Document>()
		{
			public int compare(Document a, Document b)
			{
				return Double.valueOf(getCosineSimilarity(a, query)).compareTo(
						Double.valueOf(getCosineSimilarity(b, query)));
			}
		};
	}

	protected double getEuclideanLength(Document document)
	{
		double euclideanLength = 0.0;
		for (String term : this.getDictionary())
		{
			euclideanLength += Math.pow(document.getTermFrequency(term), 2);
		}
		euclideanLength = Math.sqrt(euclideanLength);
		return euclideanLength;
	}
}
