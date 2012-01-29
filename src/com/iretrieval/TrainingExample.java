package com.iretrieval;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Node;

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
	
	/**
	 * Loads training examples from XML file for learning zone weights.
	 * 
	 * @param xmlFileLocation
	 * Location of the XML file containing examples description. All examples
	 * should contain valid document references within index source specified
	 * for examples set, otherwise they will be ignored when adjusting weights.
	 * 
	 * @return Set of training examples or empty set if no valid examples where
	 * present in the XML file that was given as a parameter.
	 */
	public static Set<TrainingExample> loadExamples(String xmlFileLocation)
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
				examples.add(new TrainingExample(guid.getText().trim(), term.getText(), Boolean
						.parseBoolean(relevance.getText())));

			}
		}
		return examples;
	}

	private String documentGuid;
	private boolean relevant;
	private String term;
}
