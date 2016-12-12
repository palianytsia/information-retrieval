package ua.edu.ukma.fin.iretrieval.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ua.edu.ukma.fin.iretrieval.Query;

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

	private VectorSpaceIndex vectorSpaceIndex = null;
}
