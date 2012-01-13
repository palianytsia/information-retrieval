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
	@SuppressWarnings("unchecked")
	public static Set<TrainingExample> loadExamples(String xmlFileLocation)
	{
		Set<TrainingExample> examples = new HashSet<TrainingExample>();
		org.dom4j.Document document = Utils.getDom4jDocument(xmlFileLocation);
		Node rootNode = document.selectSingleNode( "//ExampleSet" );
		String indexSource = rootNode.valueOf("indexSource");
		URL feedURL;
		try
		{
			feedURL = new URL(indexSource);
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return examples;
		}
		ZonedIndex index = (ZonedIndex) SearchEngine.getIndex(feedURL, IndexType.ZonedIndex); 
		List<Node> nodes = document.selectNodes("//ExampleSet/Example");
		for (Node node : nodes)
		{
			Node term = node.selectSingleNode("Term");
			Node guid = node.selectSingleNode("Guid");
			Node relevance = node.selectSingleNode("Relevance");
			if (term != null && guid != null && relevance != null) {
				examples.add(new TrainingExample(index.getDocumentFromCache(guid.getText() ), term.getText(), Boolean.parseBoolean(relevance.getText())));
			}
		}

		return examples;
	}
	
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
