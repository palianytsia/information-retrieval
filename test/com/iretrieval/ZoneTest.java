package com.iretrieval;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.iretrieval.index.IndexType;
import com.iretrieval.index.ZonedIndex;

public class ZoneTest
{
	private final String rssLocation = "";
	private ZonedIndex index = null;
	private Set<TrainingExample> examples = new HashSet<TrainingExample>();

	@Before
	public void setUp() throws Exception
	{
		int[] localIds = { 344, 340, 342, 4, 128, 49, 42, 43, 45, 46 };
		boolean[] relevance = { true, true, true, false, false, true, true, true, false, false };
		index = (ZonedIndex) SearchEngine.getIndex(new URL(rssLocation), IndexType.ZonedIndex);
		if (localIds.length == relevance.length)
		{
			for (int i = 0; i < localIds.length; i++)
			{
				Document document = index.getDocumentFromCache("http://bookmart.pragmatictips.com/"
						+ "index.php?cmd=fullbookinformation&book=" + localIds[i]);
				assertNotNull(document);
				examples.add(new TrainingExample((ZonedDocument) document, "java", relevance[i]));
			}
		}
	}

	@After
	public void tearDown() throws Exception
	{
	}

	private void testWeights()
	{
		double totalWeight = 0.0;
		System.out.println("==== Zones weights ====");
		for (ZoneName name : ZoneName.values())
		{
			Double weight = Zone.getWeight(name);
			assertTrue(weight > 0.0);
			System.out.println(name.toString() + " " + weight);
			totalWeight += weight;
		}
		System.out.println("=======================");
		assertTrue(1.0 == totalWeight);
	}

	@Test
	public void testAdjustWeights()
	{
		ZonedDocument testDoc = (ZonedDocument) index.getDocumentFromCache("http://bookmart.pragmatictips.com/"
				+ "index.php?cmd=fullbookinformation&book=344");
		double oldWeight = testDoc.getWeightedZoneScore("java");
		testWeights();
		Zone.adjustWeights(examples);
		testWeights();
		assertTrue(oldWeight != testDoc.getWeightedZoneScore("java"));
		int i = 0;
		for (Document document : index.retrieveDocuments(new Query("Java")))
		{
		  System.out.println(++i + ") " + document.toString());
		}
	}

}
