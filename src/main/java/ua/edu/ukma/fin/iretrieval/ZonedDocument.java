package ua.edu.ukma.fin.iretrieval;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ZonedDocument extends Document
{
	/**
	 * Converts Document to ZonedDocument
	 */
	public ZonedDocument(Document document)
	{
		super(document.getGuid());
		setCategories(document.getCategories());
		setBody(document.getBody());
		setExtraFields(document.getExtraFields());
		setLink(document.getLink());
		setPubDate(document.getPubDate());
		setTitle(document.getTitle());
	}

	/**
	 * Simply calls {@link ua.edu.ukma.fin.iretrieval.Document#Document(String) superclass
	 * constructor}. Zones will be created on setters calls.
	 */
	public ZonedDocument(String guid)
	{
		super(guid);
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
	 * Retrieves zone by the name given
	 * 
	 * @param name
	 * Zone name, check {@link ua.edu.ukma.fin.iretrieval.ZoneName names enumeration} for
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
	public void setBody(String content)
	{
		super.setBody(content);
		addZone(ZoneName.Description, getBody());
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
	 * Zone name, check {@link ua.edu.ukma.fin.iretrieval.ZoneName names enumeration} for
	 * appropriate name
	 * 
	 * @param content
	 * Textual content for a zone. If null given an empty string will be used
	 * instead.
	 * 
	 * @return The previous value of zone, or null if there was no zone set with
	 * such name
	 */
	private Zone addZone(ZoneName name, String content)
	{
		if (content == null)
		{
			content = "";
		}
		Zone newZone = new Zone(name, content);
		return zones.put(name, newZone);
	}

	private Map<ZoneName, Zone> zones = new HashMap<ZoneName, Zone>();

}
