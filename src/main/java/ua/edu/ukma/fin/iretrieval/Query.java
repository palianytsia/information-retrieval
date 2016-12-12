package ua.edu.ukma.fin.iretrieval;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class Query implements TermStatistics
{
	/**
	 * Constructs new query for retrieving documents
	 * 
	 * @param queryString
	 * String representation of the information need (query text). Before being
	 * stored is processed by {@link ua.edu.ukma.fin.iretrieval.Utils#normalize(String)
	 * normalization function}.
	 */
	public Query(String queryString)
	{
		this.queryString = Utils.normalize(queryString);
		StringTokenizer st = new StringTokenizer(this.queryString);
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
			termFrequency = Utils.countTerms(term, queryString);
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
