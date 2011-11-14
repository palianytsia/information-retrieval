package com.iretrieval;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Document
{
	public Document(String guid)
	{
		this.guid = guid;
	}

	/**
	 * @param category - Name of the category
	 * @return true if categories set did not already contain the specified
	 *         element
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
	 * @param groupName - Title for the group of extra fields (namespace)
	 * @param fieldName - Name of the field, will be used as a key, should be
	 *        unique within the single group
	 * @param fieldValue - extra field value
	 * 
	 * @return the previous fieldValue associated with fieldName within the
	 *         current group, or null if there was no mapping for fieldName
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

	public Set<String> getCategories()
	{
		return categories;
	}

	public String getDescription()
	{
		return description;
	}

	public Map<String, Map<String, String>> getExtraFields()
	{
		return extraFields;
	}

	public String getGuid()
	{
		return guid;
	}

	public String getLink()
	{
		return link;
	}

	public Date getPubDate()
	{
		return pubDate;
	}

	public String getTitle()
	{
		return title;
	}

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

		if (categories != null)
		{
			for (String category : categories)
			{
				builder.append(category);
				builder.append(" ");
			}
		}

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

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		return result;
	}

	public void setCategories(Set<String> categories)
	{
		this.categories = categories;
	}

	public void setDescription(String content)
	{
		this.description = content;
	}

	public void setExtraFields(Map<String, Map<String, String>> extraFields)
	{
		this.extraFields = extraFields;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	public void setPubDate(Date pubDate)
	{
		this.pubDate = pubDate;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String toString()
	{
		return "Document[guid=" + guid + ",title=" + title + "]";
	}

	private Set<String> categories = new HashSet<String>();
	private String description;
	private Map<String, Map<String, String>> extraFields = new HashMap<String, Map<String, String>>();
	private String guid;
	private String link;
	private Date pubDate;
	private String title;

}