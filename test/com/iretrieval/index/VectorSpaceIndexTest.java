package com.iretrieval.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.iretrieval.Query;

public class VectorSpaceIndexTest extends InvertedIndexTest
{
	@Before
	public void setUp()
	{
		super.setUp();
		vectorSpaceIndex = new VectorSpaceIndex(IndexTest.docs);
		assertNotNull(vectorSpaceIndex);
	}

	@After
	public void tearDown()
	{
		super.tearDown();
		vectorSpaceIndex = null;
	}

	@Test
	public void testGetCosineSimilarity()
	{
		Query query = new Query("best auto insurance");
		assertEquals(null, 0.46145682864166, vectorSpaceIndex.getCosineSimilarity(IndexTest.a, query), 0.001);
		assertEquals(null, 0.33737567466051, vectorSpaceIndex.getCosineSimilarity(IndexTest.b, query), 0.001);
		assertEquals(null, 0.12502479392953, vectorSpaceIndex.getCosineSimilarity(IndexTest.c, query), 0.001);
	}

	@Test
	public void testGetEuclideanLength()
	{
		assertEquals(null, 2.44948974278318, vectorSpaceIndex.getEuclideanLength(IndexTest.a), 0.001);
		assertEquals(null, 1.41421356237310, vectorSpaceIndex.getEuclideanLength(IndexTest.b), 0.001);
		assertEquals(null, 1.41421356237310, vectorSpaceIndex.getEuclideanLength(IndexTest.c), 0.001);
	}

	private VectorSpaceIndex vectorSpaceIndex = null;
}
