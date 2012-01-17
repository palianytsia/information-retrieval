package com.iretrieval;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class Query implements TermStatistics
{
	public Query(String queryString)
	{
		this.queryString = queryString;
		StringTokenizer st = new StringTokenizer(Utils.normalize(queryString));
		while (st.hasMoreTokens())
		{
			String term = st.nextToken();
			terms.add(term);
		}
	}

	public String getQueryString()
	{
		return queryString;
	}

	public int getTermFrequency(String term)
	{
		Integer termFrequency = termFrequencies.get(term);
		if (termFrequency == null)
		{
			termFrequency = Utils.countMatches("\\b" + term + "\\b", queryString);
			termFrequencies.put(term, termFrequency);
		}
		return termFrequency.intValue();
	}

	public Set<String> getTerms()
	{
		return Collections.unmodifiableSet(terms);
	}

	public void setQueryString(String queryString)
	{
		this.queryString = queryString;
	}

	public void setTerms(Collection<String> terms)
	{
		this.terms.clear();
		if (terms != null)
		{
			this.terms.addAll(terms);
		}
	}

	private String queryString;

	private Map<String, Integer> termFrequencies = new HashMap<String, Integer>();

	private Set<String> terms = new HashSet<String>();
}
