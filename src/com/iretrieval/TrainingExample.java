package com.iretrieval;

public class TrainingExample
{
	public TrainingExample(ZonedDocument document, String term, boolean relevant)
	{
		this.document = document;
		this.term = term;
		this.relevant = relevant;
	}

	public ZonedDocument getDocument()
	{
		return document;
	}

	public String getTerm()
	{
		return term;
	}

	public boolean isRelevant()
	{
		return relevant;
	}

	private ZonedDocument document;
	private boolean relevant;
	private String term;
}
