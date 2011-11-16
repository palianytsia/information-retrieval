package com.iretrieval;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InvertedIndexTest
{
	private InvertedIndex index = null;

	@Before
	public void setUp() throws Exception
	{
		index = InvertedIndex.getInstance(new URL("http://bookmart.vertykal/?cmd=rss"));
		assertNotNull(index);
	}

	@After
	public void tearDown() throws Exception
	{
		index = null;
	}

	@Test
	public void testGetDictionary()
	{
		Set<String> dictionary = index.getDictionary();
		assertNotNull(dictionary);
		assertTrue(dictionary.size() > 0);
		System.out.println(dictionary.size());
		Query query = new Query("Глибовець М.М., Олецький О.В.");
		System.out.println(query.getTerms());
		for (String term : query.getTerms())
		{
			assertTrue(dictionary.contains(term));
		}
		System.out.println(dictionary);
	}

	@Test
	public void testGetInstance() throws MalformedURLException
	{
		assertTrue(index == InvertedIndex.getInstance(new URL("http://bookmart.vertykal/?cmd=rss")));
	}

	@Test
	public void testGetDocumentFromCache()
	{
		Document document = index
				.getDocumentFromCache("http://bookmart.vertykal/index.php?cmd=fullbookinformation&book=1");
		assertNotNull(document);
		System.out.println(document.getExtraFields());
	}

	@Test
	public void testRetrieveDocuments()
	{
		Set<Document> results = index.retrieveDocuments(new Query("Ajax"));
		assertNotNull(results);
		assertTrue(results.size() > 0);
		System.out.println(results);
	}

}
