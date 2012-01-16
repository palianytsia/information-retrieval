package com.iretrieval.index;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Node;

import com.iretrieval.Document;
import com.iretrieval.Query;
import com.iretrieval.TrainingExample;
import com.iretrieval.Utils;
import com.iretrieval.Zone;
import com.iretrieval.ZoneName;
import com.iretrieval.ZonedDocument;

public class ZonedIndex extends Index
{
	public static Collection<ZonedDocument> convertDocuments(Collection<Document> documents) {
		Collection<ZonedDocument> zonedDocuments = new HashSet<ZonedDocument>();
		for (Document document : documents)
		{
			zonedDocuments.add(new ZonedDocument(document));
		}
		return zonedDocuments;
	}
	
	public ZonedIndex(Collection<ZonedDocument> documents)
	{
		super(documents);
		zonesWeights = new HashMap<ZoneName, Double>();
		for (ZoneName name : ZoneName.values())
		{
			zonesWeights.put(name, (1.0 / ZoneName.values().length));
		}
	}

	public ZonedIndex(Collection<ZonedDocument> documents, String pathToExamples)
	{
		this(documents);
		if (pathToExamples != null)
		{
			Set<TrainingExample> examples = loadExamples(pathToExamples);
			adjustWeights(examples);
		}
	}

	@Override
	public ZonedDocument getDocumentFromCache(String guid)
	{
		return (ZonedDocument) super.getDocumentFromCache(guid);
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
	private Map<ZoneName, Double> adjustWeights(Collection<TrainingExample> examples)
	{
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
	 * Loads training examples from XML file for learning zone weights.
	 * 
	 * @param xmlFileLocation
	 * Location of the XML file containing examples description. All examples
	 * should contain valid document references within index source specified
	 * for examples set, otherwise they will be ignored.
	 * 
	 * @return Set of training examples or empty set if no valid examples where
	 * present in the XML file given as a parameter.
	 */
	private Set<TrainingExample> loadExamples(String xmlFileLocation)
	{
		Set<TrainingExample> examples = new HashSet<TrainingExample>();
		org.dom4j.Document document = Utils.getDom4jDocument(xmlFileLocation);
		List<?> nodes = document.selectNodes("//ExampleSet/Example");
		for (Object nodeObj : nodes)
		{
			Node node = (Node) nodeObj;
			Node term = node.selectSingleNode("Term");
			Node guid = node.selectSingleNode("Guid");
			Node relevance = node.selectSingleNode("Relevance");
			if (term != null && guid != null && relevance != null)
			{
				ZonedDocument zonedDocument = this.getDocumentFromCache(guid.getText().trim());
				if (zonedDocument != null)
				{
					examples.add(new TrainingExample(zonedDocument, term.getText(), Boolean
							.parseBoolean(relevance.getText())));
				}
				else
				{
					System.err.println("Document [" + guid.getText()
							+ "] is not present in the index cache");
				}
			}
		}
		return examples;
	}

	private Map<ZoneName, Double> zonesWeights = null;

}
