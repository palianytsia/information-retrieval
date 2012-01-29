package com.iretrieval.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.iretrieval.Document;
import com.iretrieval.Query;
import com.iretrieval.index.Index;
import com.iretrieval.index.InvertedIndex;

public class IndexTest
{
	@Before
	public void setUp()
	{
		index = new InvertedIndex(IndexTest.docs);
		assertNotNull(index);
	}
	
	@After
	public void tearDown() {
		index = null;
	}

	@Test
	public void testGetDictionary()
	{
		Set<String> dictionary = index.getDictionary();
		assertNotNull(dictionary);
		assertTrue(dictionary.size() == 7);
		assertTrue(dictionary.contains("car"));
	}

	@Test
	public void testGetDocumentFromCache()
	{
		Document document = index.getDocumentFromCache(a.getGuid());
		assertNotNull(document);
		assertEquals(a, document);
	}

	@Test
	public void testRetrieveDocuments()
	{
		List<Document> results = index.retrieveDocuments(new Query("car"));
		assertNotNull(results);
		assertEquals(2, results.size());
		assertTrue(results.contains(a) && results.contains(b));
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		a.setTitle("<h1>Auto insurance policy</h1>");
		a.setBody("<p><strong>Car</strong> insurance.</p>");
		b.setTitle("best car");
		c.setTitle("fast auto");
		c.setBody("Tractor");
		docs.add(a);
		docs.add(b);
		docs.add(c);
	}

	private Index index = null;

	protected final static Document a = new Document("Document a");
	protected final static Document b = new Document("Document b");
	protected final static Document c = new Document("Document c");
	protected final static Collection<Document> docs = new HashSet<Document>();

}
