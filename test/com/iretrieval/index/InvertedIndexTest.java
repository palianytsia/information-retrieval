package com.iretrieval.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InvertedIndexTest extends IndexTest
{
	@Before
	public void setUp()
	{
		super.setUp();
		invertedIndex = new InvertedIndex(IndexTest.docs);
		assertNotNull(invertedIndex);
	}

	@After
	public void tearDown()
	{
		super.tearDown();
		invertedIndex = null;
	}

	@Test
	public void testGetDocumentFrequency()
	{
		assertEquals(2, invertedIndex.getDocumentFrequency("car"));
		assertEquals(2, invertedIndex.getDocumentFrequency("auto"));
		assertEquals(1, invertedIndex.getDocumentFrequency("insurance"));
		assertEquals(1, invertedIndex.getDocumentFrequency("best"));
		assertEquals(1, invertedIndex.getDocumentFrequency("fast"));
		assertEquals(0, invertedIndex.getDocumentFrequency("word"));
	}

	@Test
	public void testGetInverseDocumentFrequency()
	{
		assertEquals(null, 0.17609125905568, invertedIndex.getInverseDocumentFrequency("car"),
				0.001);
		assertEquals(null, 0.47712125471966, invertedIndex.getInverseDocumentFrequency("insurance"),
				0.001);
		assertEquals(null, 0.47712125471966, invertedIndex.getInverseDocumentFrequency("best"),
				0.001);
	}

	private InvertedIndex invertedIndex = null;
}