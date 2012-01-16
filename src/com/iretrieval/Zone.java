package com.iretrieval;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
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

	/**
	 * Given a set of training examples adjusts weights of known zones using
	 * machine learning techniques.
	 * 
	 * @see "Introduction to information retrieval. 6.1.2 Learning weights"
	 * 
	 * @param examples
	 * Collection of training examples to learn weights from
	 * 
	 * @return Unmodifiable map, where key is zone name and value is new,
	 * adjusted weight of a zone
	 */
	public static Map<ZoneName, Double> adjustWeights(Collection<TrainingExample> examples)
	{
		for (int i = 0; i < ZoneName.values().length - 1; i++)
		{
			for (int j = i + 1; j < ZoneName.values().length; j++)
			{
				ZoneName a = ZoneName.values()[i];
				ZoneName b = ZoneName.values()[j];
				double oldTotalWeight = getWeight(a) + getWeight(b);
				int n01r = 0;
				int n01n = 0;
				int n10r = 0;
				int n10n = 0;
				for (TrainingExample example : examples)
				{
					Zone zoneA = example.getDocument().getZone(a);
					Zone zoneB = example.getDocument().getZone(b);
					if ((zoneA == null || zoneA.getTermFrequency(example.getTerm()) == 0)
							&& (zoneB != null && zoneB.getTermFrequency(example.getTerm()) > 0)
							&& example.isRelevant())
					{
						n01r++;
					}
					if ((zoneA == null || zoneA.getTermFrequency(example.getTerm()) == 0)
							&& (zoneB != null && zoneB.getTermFrequency(example.getTerm()) > 0)
							&& !example.isRelevant())
					{
						n01n++;
					}
					if ((zoneA != null && zoneA.getTermFrequency(example.getTerm()) > 0)
							&& (zoneB == null || zoneB.getTermFrequency(example.getTerm()) == 0)
							&& example.isRelevant())
					{
						n10r++;
					}
					if ((zoneA != null && zoneA.getTermFrequency(example.getTerm()) > 0)
							&& (zoneB == null || zoneB.getTermFrequency(example.getTerm()) == 0)
							&& !example.isRelevant())
					{
						n10n++;
					}
				}
				if (n10r != 0 || n10n != 0 || n01r != 0 || n01n != 0)
				{
					double g = Double.valueOf(n10r + n01n)
							/ Double.valueOf(n10r + n10n + n01r + n01n);
					if (g != 0)
					{
						BigDecimal newWeightA = new BigDecimal(oldTotalWeight * g);
						newWeightA = newWeightA.setScale(5, BigDecimal.ROUND_HALF_UP);
						BigDecimal newWeightB = new BigDecimal(oldTotalWeight
								- newWeightA.doubleValue());
						newWeightB = newWeightB.setScale(5, BigDecimal.ROUND_HALF_UP);
						zonesWeights.put(a, newWeightA.doubleValue());
						zonesWeights.put(b, newWeightB.doubleValue());
					}
				}
			}
		}
		return Collections.unmodifiableMap(zonesWeights);
	}

	/**
	 * Retrieves a weight for a given zone
	 * 
	 * @param name
	 * Zone name, must be present in list returned by
	 * {@link #getRegisteredZones() getRegisteredZones} method
	 * 
	 * @return Zone weight, or 0 if there is no zone registered with such name
	 */
	public static double getWeight(ZoneName name)
	{
		double weight = 0.0;
		if (zonesWeights.containsKey(name))
		{
			weight = zonesWeights.get(name);
		}
		return weight;
	}

	private String content;
	private ZoneName name;
	private Map<String, Integer> termFrequencies = null;

	private static Map<ZoneName, Double> zonesWeights = null;

	static
	{
		if (zonesWeights == null)
		{
			zonesWeights = new HashMap<ZoneName, Double>();
			for (ZoneName name : ZoneName.values())
			{
				zonesWeights.put(name, (1.0 / ZoneName.values().length));
			}
		}
	}

}
