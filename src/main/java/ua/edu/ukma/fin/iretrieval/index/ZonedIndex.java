package ua.edu.ukma.fin.iretrieval.index;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;

import ua.edu.ukma.fin.iretrieval.Document;
import ua.edu.ukma.fin.iretrieval.Query;
import ua.edu.ukma.fin.iretrieval.TrainingExample;
import ua.edu.ukma.fin.iretrieval.Zone;
import ua.edu.ukma.fin.iretrieval.ZoneName;
import ua.edu.ukma.fin.iretrieval.ZonedDocument;

public class ZonedIndex extends Index
{
	public ZonedIndex(Collection<ZonedDocument> documents)
	{
		this(documents, null);
	}

	public ZonedIndex(Collection<ZonedDocument> documents, Collection<TrainingExample> examples)
	{
		super(documents);
		zonesWeights = new HashMap<ZoneName, Double>();
		for (ZoneName name : ZoneName.values())
		{
			zonesWeights.put(name, (1.0 / ZoneName.values().length));
		}
		if (examples != null && examples.size() > 0)
		{
			if (!adjustWeights(examples))
			{
				logger.log(Level.INFO, "Zones' weights were adjusted. New weights: {0}.",
						zonesWeights);
			}
			else
			{
				logger.log(Level.WARNING, "Examples weren't good enough to adjust zones' weights. "
						+ "Weights remain the same: {0}.", zonesWeights);
			}
		}
	}

	@Override
	public ZonedDocument getDocumentFromCache(String guid)
	{
		return (ZonedDocument) super.getDocumentFromCache(guid);
	}

	@Override
	protected Comparator<Document> getDocumentComparator(final Query query)
	{
		return new Comparator<Document>()
		{
			public int compare(Document a, Document b)
			{
				return Double.valueOf(getWeightedZoneScore((ZonedDocument) a, query)).compareTo(
						Double.valueOf(getWeightedZoneScore((ZonedDocument) b, query)));
			}
		};
	}

	/**
	 * Calculates compound weighted zone score for all terms in a query. Simply
	 * calls {@link #getWeightedZoneScore(String term) getWeightedZoneScore} on
	 * each term and adds the result to the return value
	 * 
	 * @param query
	 * Query object, can't be null, should contain set of query terms
	 * 
	 * @return Compound weighted zone score
	 */
	protected double getWeightedZoneScore(ZonedDocument document, Query query)
	{
		double score = 0.0;
		for (String term : query.getTerms())
		{
			score += getWeightedZoneScore(document, term);
		}
		return score;
	}

	/**
	 * Calculates document's weighted zone score according to the specified term
	 * 
	 * @see "Introduction to information retrieval. 6.1.1 Weighted zone scoring"
	 * 
	 * @param term
	 * Term to calculate weighted zone score for
	 * 
	 * @return Weighted zone score
	 */
	protected double getWeightedZoneScore(ZonedDocument document, String term)
	{
		double score = 0.0;
		for (Zone zone : document.getZones())
		{
			score += zone.getTermFrequency(term) * getZoneWeight(zone.getName());
		}
		return score;
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
	protected double getZoneWeight(ZoneName name)
	{
		double weight = 0.0;
		if (zonesWeights.containsKey(name))
		{
			weight = zonesWeights.get(name);
		}
		return weight;
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
	 * @return TRUE if weights were adjusted, FALSE otherwise
	 */
	private boolean adjustWeights(Collection<TrainingExample> examples)
	{
		Map<ZoneName, Double> oldZonesWeights = zonesWeights;
		for (int i = 0; i < ZoneName.values().length - 1; i++)
		{
			for (int j = i + 1; j < ZoneName.values().length; j++)
			{
				ZoneName a = ZoneName.values()[i];
				ZoneName b = ZoneName.values()[j];
				double oldTotalWeight = getZoneWeight(a) + getZoneWeight(b);
				int n01r = 0;
				int n01n = 0;
				int n10r = 0;
				int n10n = 0;
				for (TrainingExample example : examples)
				{
					ZonedDocument document = getDocumentFromCache(example.getDocumentGuid());
					if (document == null)
					{
						System.err.printf("Document [%s] is not present in the index cache.%n",
								example.getDocumentGuid());
					}
					else
					{
						Zone zoneA = document.getZone(a);
						Zone zoneB = document.getZone(b);
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
				}
				if (n10r != 0 || n10n != 0 || n01r != 0 || n01n != 0)
				{
					double g = Double.valueOf(n10r + n01n)
							/ Double.valueOf(n10r + n10n + n01r + n01n);
					if (g > 0 && g < 1)
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
		if (oldZonesWeights.equals(zonesWeights))
		{
			return false;
		}
		return true;
	}

	public static Collection<ZonedDocument> convertDocuments(Collection<Document> documents)
	{
		Collection<ZonedDocument> zonedDocuments = new HashSet<ZonedDocument>();
		for (Document document : documents)
		{
			zonedDocuments.add(new ZonedDocument(document));
		}
		return zonedDocuments;
	}

	private Map<ZoneName, Double> zonesWeights = null;

}
