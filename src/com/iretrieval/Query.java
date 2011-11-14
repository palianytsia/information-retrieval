package com.iretrieval;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class Query
{
	private String queryString;
	private List<String> terms = new LinkedList<String>();

	public Query(String queryString)
	{
		this.queryString = queryString;
		StringTokenizer st = new StringTokenizer(queryString);
		String term = st.nextToken();
		if (!terms.contains(term))
		{
			terms.add(term);
		}
	}

	public String getQueryString()
	{
		return queryString;
	}

	public List<String> getTerms()
	{
		return Collections.unmodifiableList(terms);
	}

	public void setQueryString(String queryString)
	{
		this.queryString = queryString;
	}

	public void setTerms(List<String> terms)
	{
		this.terms = terms;
	}
}
