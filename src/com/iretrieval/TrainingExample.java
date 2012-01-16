package com.iretrieval;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Node;

import com.iretrieval.index.IndexType;
import com.iretrieval.index.ZonedIndex;

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

	/**
	 * @return TRUE if the example's document is relevant to the example's term,
	 * FALSE otherwise
	 */
	public boolean isRelevant()
	{
		return relevant;
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
	public static Set<TrainingExample> loadExamples(String xmlFileLocation)
	{
		Set<TrainingExample> examples = new HashSet<TrainingExample>();
		org.dom4j.Document document = Utils.getDom4jDocument(xmlFileLocation);
		Node rootNode = document.selectSingleNode("//ExampleSet");
		String indexSource = rootNode.valueOf("@indexSource");
		URL feedURL;
		try
		{
			feedURL = new URL(indexSource);
		}
		catch (MalformedURLException e)
		{
			System.err.println("Index source of examples set is not a valid URL: [" + indexSource
					+ "]. Examples won't be loaded.");
			return examples;
		}
		ZonedIndex index = (ZonedIndex) SearchEngine.getIndex(feedURL, IndexType.ZonedIndex);
		List<?> nodes = document.selectNodes("//ExampleSet/Example");
		for (Object nodeObj : nodes)
		{
			Node node = (Node) nodeObj;
			Node term = node.selectSingleNode("Term");
			Node guid = node.selectSingleNode("Guid");
			Node relevance = node.selectSingleNode("Relevance");
			if (term != null && guid != null && relevance != null)
			{
				ZonedDocument zonedDocument = index.getDocumentFromCache(guid.getText().trim());
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

	private ZonedDocument document;
	private boolean relevant;
	private String term;
}
