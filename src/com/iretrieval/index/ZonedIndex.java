package com.iretrieval.index;

import java.util.Collection;
import java.util.Comparator;

import com.iretrieval.Document;
import com.iretrieval.Query;
import com.iretrieval.ZonedDocument;

public class ZonedIndex extends Index
{
	public ZonedIndex(Collection<ZonedDocument> documents)
	{
		super(documents);
	}

	@Override
	public ZonedDocument getDocumentFromCache(String guid)
	{
		return (ZonedDocument) super.getDocumentFromCache(guid);
	}

	@Override
	protected Comparator<Document> getDocumentComparator(final Query query)
	{
		return new Comparator<Document>()
		{
			public int compare(Document a, Document b)
			{
				return Double.valueOf(((ZonedDocument) a).getWeightedZoneScore(query)).compareTo(
						Double.valueOf(((ZonedDocument) b).getWeightedZoneScore(query)));
			}
		};
	}

}
