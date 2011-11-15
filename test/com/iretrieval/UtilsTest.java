package com.iretrieval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.junit.Test;

public class UtilsTest
{

	@Test
	public void testLoadDocuments()
	{
		Set<Document> documents = null;
		try
		{
			documents = Utils.loadDocuments(new URL("http://bookmart.vertykal/?cmd=rss"));
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			fail("Malformed URL");
		}
		assertNotNull(documents);
		assertEquals(392, documents.size());
		assertTrue(documents.contains(new Document(
				"http://bookmart.vertykal/index.php?cmd=fullbookinformation&book=397")));
	}

}
