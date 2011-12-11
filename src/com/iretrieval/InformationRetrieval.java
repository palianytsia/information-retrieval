package com.iretrieval;

import java.util.List;

public interface InformationRetrieval
{
	public List<Document> retrieveDocuments(final Query query);
}
