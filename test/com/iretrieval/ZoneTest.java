package com.iretrieval;

import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.iretrieval.index.IndexType;
import com.iretrieval.index.ZonedIndex;

public class ZoneTest
{
	private final String rssLocation = "http://bookmart.pragmatictips.com/?cmd=rss";
	private final String examplesFileLocation = "/home/ivan/Java/workspaces/Education/Information Retrieval/docs/ZoneWeightsTrainingSet.xml";
	private ZonedIndex index = null;
	private Set<TrainingExample> examples = null;

	@Before
	public void setUp() throws Exception
	{
		index = (ZonedIndex) SearchEngine.getIndex(new URL(rssLocation), IndexType.ZonedIndex);
		examples = TrainingExample.loadExamples(examplesFileLocation);
	}

	@After
	public void tearDown() throws Exception
	{
		index = null;
		examples = null;
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
