package com.iretrieval;

import static org.junit.Assert.assertFalse;

import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.iretrieval.index.IndexType;
import com.iretrieval.index.ZonedIndex;

public class ZonedDocumentTest
{

	private final String rssLocation = "http://bookmart.pragmatictips.com/?cmd=rss";
	private final Query query = new Query("Java");
	private ZonedIndex index = null;

	@Before
	public void setUp() throws Exception
	{
		index = (ZonedIndex) SearchEngine.getIndex(new URL(rssLocation), IndexType.ZonedIndex);
	}

	@After
	public void tearDown() throws Exception
	{
		index = null;
	}

	@Test
	public void testGetWeightedZoneScore()
	{
		ZonedDocument document = index.getDocumentFromCache("http://bookmart.pragmatictips.com/"
						+ "index.php?cmd=fullbookinformation&book=" + 23);
		double weightedZoneScore = document.getWeightedZoneScore(query);
		double termFrequency = document.getTermFrequency(query);
		assertFalse(termFrequency == weightedZoneScore);
	}
}
