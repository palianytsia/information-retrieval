package com.iretrieval.index;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.iretrieval.ZoneName;

public class ZonedIndexTest extends IndexTest
{
	@Before
	public void setUp()
	{
		super.setUp();
		zonedIndex = new ZonedIndex(ZonedIndex.convertDocuments(IndexTest.docs), null);
		assertNotNull(zonedIndex);
	}
	
	@After
	public void tearDown() {
		super.tearDown();
		zonedIndex = null;
	}

	@Test
	public void testAdjustWeights()
	{
		assertTrue(zonedIndex.getZoneWeight(ZoneName.Title) > zonedIndex.getZoneWeight(ZoneName.Description));
	}
	
	@Test
	public void testGetWeightedZoneScoreZonedDocumentQuery()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetWeightedZoneScoreZonedDocumentString()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetZoneWeight()
	{
		double totalWeight = 0.0;
		System.out.println("==== Zones weights ====");
		for (ZoneName name : ZoneName.values())
		{
			Double weight = zonedIndex.getZoneWeight(name);
			assertTrue(weight > 0.0);
			totalWeight += weight;
			System.out.println(name.toString() + " " + weight);
		}
		System.out.println("=======================");
		assertTrue(1.0 == totalWeight);
	}
	
	private ZonedIndex zonedIndex = null;
}
