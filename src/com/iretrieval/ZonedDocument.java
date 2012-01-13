package com.iretrieval;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ZonedDocument extends Document
{
	/**
	 * Simply calls {@link com.iretrieval.Document#Document(String) superclass
	 * constructor}. Zones will be created on setters calls.
	 */
	public ZonedDocument(String guid)
	{
		super(guid);
	}
	
	/**
	 * Convert Document to ZonedDocument using this constructor
	 */
	public ZonedDocument (Document document) {
		super(document.getGuid());
		super.setCategories(document.getCategories());
		super.setDescription(document.getDescription());
		super.setExtraFields(document.getExtraFields());
		super.setLink(document.getLink());
		super.setPubDate(document.getPubDate());
		super.setTitle(document.getTitle());
	}

	@Override
	public boolean addCategory(String category)
	{
		boolean success = super.addCategory(category);
		addZone(ZoneName.Categories, categoriesToString());
		return success;
	}

	@Override
	public String addExtraField(String groupName, String fieldName, String fieldValue)
	{
		String oldFieldValue = super.addExtraField(groupName, fieldName, fieldValue);
		addZone(ZoneName.ExtraFields, extraFieldsToString());
		return oldFieldValue;
	}

	@Override
	public int getTermFrequency(String term)
	{
		int termFrequency = 0;
		for (Zone zone : getZones())
		{
			termFrequency += zone.getTermFrequency(term);
		}
		return termFrequency;
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
	public double getWeightedZoneScore(Query query)
	{
		double score = 0.0;
		for (String term : query.getTerms())
		{
			score += getWeightedZoneScore(term);
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
	public double getWeightedZoneScore(String term)
	{
		double score = 0.0;
		for (Zone zone : getZones())
		{
			score += zone.getTermFrequency(term) * Zone.getWeight(zone.getName());
		}
		return score;
	}

	/**
	 * Retrieves zone by the name given
	 * 
	 * @param name
	 * Zone name, check {@link com.iretrieval.ZoneName names enumeration} for
	 * appropriate name
	 * 
	 * @return Zone object or null if document has no content for zone with such
	 * name
	 */
	public Zone getZone(ZoneName name)
	{
		return zones.get(name);
	}

	/**
	 * Retrieves zones contained by the document
	 * 
	 * @return Unmodifiable Collection of zones
	 */
	public Collection<Zone> getZones()
	{
		return Collections.unmodifiableCollection(zones.values());
	}

	@Override
	public void setCategories(Collection<String> categories)
	{
		super.setCategories(categories);
		addZone(ZoneName.Categories, categoriesToString());
	}

	@Override
	public void setDescription(String content)
	{
		super.setDescription(content);
		addZone(ZoneName.Description, getDescription());
	}

	@Override
	public void setTitle(String title)
	{
		super.setTitle(title);
		addZone(ZoneName.Title, getTitle());
	}
	
	/**
	 * Adds new zone to the document. If document already contains zone with
	 * such name replaces it with a new one.
	 * 
	 * @param name
	 * Zone name, check {@link com.iretrieval.ZoneName names enumeration} for
	 * appropriate name
	 * 
	 * @param content
	 * Textual content of new zone
	 * 
	 * @return The previous value of zone, or null if there was no zone set with
	 * such name
	 */
	private Zone addZone(ZoneName name, String content)
	{
		Zone newZone = new Zone(name, content.toLowerCase());
		return zones.put(name, newZone);
	}

	private Map<ZoneName, Zone> zones = new HashMap<ZoneName, Zone>();

}
