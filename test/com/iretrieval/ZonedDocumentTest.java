package com.iretrieval;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.iretrieval.index.IndexType;
import com.iretrieval.index.ZonedIndex;

public class ZonedDocumentTest
{

	private final String rssLocation = "http://bookmart.pragmatictips.com/?cmd=rss";
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
	public void testGetZone()
	{
		ZonedDocument document = index.getDocumentFromCache("http://bookmart.pragmatictips.com/"
						+ "index.php?cmd=fullbookinformation&book=" + 397);
		System.out.println(document.getZones());
		for(ZoneName zoneName: ZoneName.values()) {
			Zone zone = document.getZone(zoneName);
			assertNotNull(zone);
		}
	}
}
