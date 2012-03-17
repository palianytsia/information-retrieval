package com.iretrieval;


public class TrainingExample
{
	public TrainingExample(String documentGuid, String term, boolean relevant)
	{
		this.documentGuid = documentGuid;
		this.term = term;
		this.relevant = relevant;
	}

	public String getDocumentGuid()
	{
		return documentGuid;
	}

	public String getTerm()
	{
		return term;
	}

	/**
	 * @return TRUE if the example's document is relevant to the example's term,
	 * FALSE otherwise
	 */
	public boolean isRelevant()
	{
		return relevant;
	}
	
	private String documentGuid;
	private boolean relevant;
	private String term;
}
