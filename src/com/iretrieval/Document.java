package com.iretrieval;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Document
{

	/**
	 * Constructs a document from GUID.
	 * 
	 * @param guid
	 * <a href="http://en.wikipedia.org/wiki/Globally_Unique_Identifier"
	 * >Globally Unique Identifier</a> of the document.
	 */
	public Document(String guid)
	{
		this.guid = guid;
	}

	/**
	 * Adds a category to document's categories' set.
	 * 
	 * @param category
	 * Name of the category.
	 * 
	 * @return TRUE if categories set did not already contain the specified
	 * element.
	 */
	public boolean addCategory(String category)
	{
		if (categories == null)
		{
			categories = new HashSet<String>();
		}
		return categories.add(category);
	}

	/**
	 * Adds extra field data to the document's extra fields collection.
	 * 
	 * @param groupName
	 * Title for the group of extra fields (namespace).
	 * 
	 * @param fieldName
	 * Name of the field, will be used as a key, should be unique within the
	 * single group.
	 * 
	 * @param fieldValue
	 * Extra field value.
	 * 
	 * @return The previous fieldValue associated with fieldName within the
	 * current group, or null if there was no mapping for fieldName.
	 */
	public String addExtraField(String groupName, String fieldName, String fieldValue)
	{
		if (extraFields == null)
		{
			extraFields = new HashMap<String, Map<String, String>>();
		}
		if (!extraFields.containsKey(groupName))
		{
			extraFields.put(groupName, new HashMap<String, String>());
		}
		Map<String, String> group = extraFields.get(groupName);
		return group.put(fieldName, fieldValue);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Document other = (Document) obj;
		if (guid == null)
		{
			if (other.guid != null) return false;
		}
		else if (!guid.equals(other.guid)) return false;
		return true;
	}

	/**
	 * Gets the document's categories.
	 * 
	 * @return Unmodifiable set of categories.
	 */
	public Set<String> getCategories()
	{
		return Collections.unmodifiableSet(categories);
	}

	/**
	 * Gets main textual content field of the document. In RSS format it is
	 * called description field, so the name is from there.
	 * 
	 * @return Document's main content field.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Gets extra fields of the document.
	 * 
	 * @return Map where extra fields are grouped by fieldGroupName, which is
	 * equivalent of the namespace in RSS. Each fieldGroupName points to a Map
	 * where key is fieldName and value is fieldValue.
	 */
	public Map<String, Map<String, String>> getExtraFields()
	{
		Map<String, Map<String, String>> extraFields = new HashMap<String, Map<String, String>>();
		for (String extraGroupName : this.extraFields.keySet())
		{
			extraFields.put(extraGroupName,
					Collections.unmodifiableMap(this.extraFields.get(extraGroupName)));
		}
		return Collections.unmodifiableMap(extraFields);
	}

	/**
	 * Gets document's GIUD.
	 * 
	 * @return <a
	 * href="http://en.wikipedia.org/wiki/Globally_Unique_Identifier"> Globally
	 * Unique Identifier</a> of the document.
	 */
	public String getGuid()
	{
		return guid;
	}

	/**
	 * Gets a link to the document on the Web.
	 * 
	 * @return Document's link.
	 */
	public String getLink()
	{
		return link;
	}

	/**
	 * Gets the date document was published on.
	 * 
	 * @return Copy of document's publishing date.
	 */
	public Date getPubDate()
	{
		return (Date) pubDate.clone();
	}

	/**
	 * Goes through all the fields within document and gathers content from them
	 * to a string. Different fields' content is delimited with space character.
	 * 
	 * @return All the text contained by the document.
	 */
	public String getRawText()
	{
		StringBuilder builder = new StringBuilder();

		if (title != null)
		{
			builder.append(title);
			builder.append(" ");
		}

		if (description != null)
		{
			builder.append(description);
			builder.append(" ");
		}

		String categoriesText = categoriesToString();
		if (categoriesText != "")
		{
			builder.append(categoriesText);
			builder.append(" ");
		}

		String extraFieldsText = extraFieldsToString();
		if (extraFieldsText != "")
		{
			builder.append(extraFieldsText);
			builder.append(" ");
		}

		return builder.toString().trim();
	}

	/**
	 * Calculates compound term frequency for all terms in a query. Simply calls
	 * {@link #getTermFrequency(String term) getTermFrequency} on each term and
	 * adds the result to the return value
	 * 
	 * @param query
	 * Query object, can't be null, should contain set of query terms
	 * 
	 * @return Compound term frequency
	 */
	public int getTermFrequency(Query query)
	{
		int termFrequency = 0;
		for (String term : query.getTerms())
		{
			termFrequency += getTermFrequency(term);
		}
		return termFrequency;
	}

	/**
	 * Gets how many times term occurs in the document
	 * 
	 * @see "Introduction to information retrieval. 6.2 Term frequency and
	 * weighting"
	 * 
	 * @param term
	 * Term to calculate number of occurrences for
	 * 
	 * @return Term frequency
	 */
	public int getTermFrequency(String term)
	{
		// if text has been changed we cannot rely on cached frequencies
		if (getRawText().hashCode() != termFrequenciesVersion)
		{
			termFrequencies.clear();
			termFrequenciesVersion = getRawText().hashCode();
		}
		Integer termFrequency = termFrequencies.get(term);
		if (termFrequency == null)
		{
			termFrequency = Utils.countMatches("\\b" + term + "\\b", getRawText());
			termFrequencies.put(term, termFrequency);
		}
		return termFrequency.intValue();
	}

	/**
	 * Gets document's title.
	 * 
	 * @return Document's title.
	 */
	public String getTitle()
	{
		return title;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		return result;
	}

	/**
	 * Sets document's categories. In order to encapsulate extraFields Set,
	 * method doesn't assigns it to the parameter given, but clears and add all
	 * elements contained by parameter Collection.
	 * 
	 * @param categories
	 * New categories' Collection for the document to set.
	 */
	public void setCategories(Collection<String> categories)
	{
		this.categories.clear();
		if (categories != null)
		{
			this.categories.addAll(categories);
		}
	}

	/**
	 * Sets document's description field, which is the main textual content
	 * field of the document. In RSS format it is called description field, so
	 * the name is from there.
	 * 
	 * @param description
	 * A new value for main textual content field of the document.
	 */
	public void setDescription(String content)
	{
		this.description = content;
	}

	/**
	 * Sets document's extraFields. In order to encapsulate extraFields Map,
	 * method doesn't assigns it to the parameter given, but clears and calls
	 * {@link #addExtraField(String groupName, String fieldName, String fieldValue)
	 * addExtraField} on each extraField contained by parameter Map.
	 * 
	 * @param extraFields
	 * A new Map of extraFields for the document to set. Extra fields should be
	 * grouped by fieldGroupName, which is equivalent of the namespace in RSS.
	 * Each fieldGroupName should point to a Map where key is fieldName and
	 * value is fieldValue.
	 */
	public void setExtraFields(Map<String, Map<String, String>> extraFields)
	{
		this.extraFields.clear();
		for (String extraGroupName : extraFields.keySet())
		{
			Map<String, String> extraGroup = extraFields.get(extraGroupName);
			if (extraGroup != null)
			{
				for (String extraKey : extraGroup.keySet())
				{
					String extraValue = extraGroup.get(extraKey);
					if (extraKey != null && extraValue != null)
					{
						addExtraField(extraGroupName, extraKey, extraValue);
					}
				}
			}
		}
	}

	/**
	 * Sets document's link.
	 * 
	 * @param link
	 * A new link for the document to set.
	 */
	public void setLink(String link)
	{
		this.link = link;
	}

	/**
	 * Copies the value of given date to the document's publishing date.
	 * 
	 * @param pubDate
	 * A new publishing date value for the document to set.
	 */
	public void setPubDate(Date pubDate)
	{
		this.pubDate = (Date) pubDate.clone();
	}

	/**
	 * Sets document's title.
	 * 
	 * @param title
	 * A new title for the document to set.
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	@Override
	public String toString()
	{
		return "Document[guid=" + guid + ",title=" + title + "]";
	}

	protected String categoriesToString()
	{
		StringBuilder builder = new StringBuilder();
		if (categories != null)
		{
			for (String category : getCategories())
			{
				builder.append(category);
				builder.append(" ");
			}
		}
		return builder.toString().trim();
	}

	protected String extraFieldsToString()
	{
		StringBuilder builder = new StringBuilder();
		if (extraFields != null)
		{
			for (Map<String, String> extraFieldsGroup : extraFields.values())
			{
				if (extraFieldsGroup != null)
				{
					for (String extraFieldValue : extraFieldsGroup.values())
					{
						builder.append(extraFieldValue);
						builder.append(" ");
					}
				}
			}
		}
		return builder.toString().trim();
	}

	private Set<String> categories = new HashSet<String>();

	private String description;

	private Map<String, Map<String, String>> extraFields = new HashMap<String, Map<String, String>>();
	private String guid;
	private String link;
	private Date pubDate;
	private Map<String, Integer> termFrequencies = new HashMap<String, Integer>();

	/**
	 * if equal to getRawText() hashCode, termFrequencies cache is up to date
	 */
	private int termFrequenciesVersion = 0;

	private String title;

}