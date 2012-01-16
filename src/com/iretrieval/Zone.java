package com.iretrieval;

import java.util.HashMap;
import java.util.Map;

public class Zone
{

	public Zone(ZoneName name, String content)
	{
		this.name = name;
		this.content = content;
		this.termFrequencies = new HashMap<String, Integer>();
	}

	public String getContent()
	{
		return content;
	}

	public ZoneName getName()
	{
		return name;
	}

	public int getTermFrequency(Query query)
	{
		int termFrequency = 0;
		for (String term : query.getTerms())
		{
			termFrequency += getTermFrequency(term);
		}
		return termFrequency;
	}

	public int getTermFrequency(String term)
	{
		Integer termFrequency = termFrequencies.get(term);
		if (termFrequency == null)
		{
			termFrequency = Utils.countMatches("\\b" + term + "\\b", getContent());
			if (termFrequency > 0)
			{
				termFrequencies.put(term, termFrequency);
			}
		}
		return termFrequency.intValue();
	}

	private String content;
	private ZoneName name;
	private Map<String, Integer> termFrequencies = null;

}
