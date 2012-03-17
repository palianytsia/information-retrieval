package com.iretrieval.index;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jdom.Element;

import com.iretrieval.Document;
import com.iretrieval.TrainingExample;
import com.iretrieval.ZonedDocument;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * @author Ivan Palianytsia
 * 
 */
public final class IndexFactory
{

	/**
	 * Parses documents from RSS feed and adds them to factory's documents' set.
	 * Duplicates are ignored. When next time Zoned index is built updated set
	 * is passed to be indexed.
	 * 
	 * @param source
	 * String representation of the URL of RSS feed containing items (documents)
	 * to be indexed. If <code>null</code> is given the initial set of documents
	 * remains untouched.
	 * 
	 * @throws IOException
	 * In case source is not valid URL or RSS feed cannot be parsed.
	 */
	public void addDocuments(String source) throws IOException
	{
		assert documents != null;
		try
		{
			URL feedUrl = new URL(source);
			SyndFeed sf = new SyndFeedInput().build(new XmlReader(feedUrl));
			for (Object itemObject : sf.getEntries())
			{
				SyndEntry item = (SyndEntry) itemObject;
				String guid = item.getUri();
				if (guid != null && !guid.isEmpty())
				{
					Document document = new Document(guid);

					String title = item.getTitle();
					if (title != null && !title.isEmpty())
					{
						document.setTitle(title);
					}

					SyndContent description = item.getDescription();
					if (description != null && description.getValue() != null
							&& !description.getValue().isEmpty())
					{
						document.setBody(description.getValue());
					}

					String link = item.getLink();
					if (link != null && !link.isEmpty())
					{
						document.setLink(link);
					}

					Date pubDate = item.getPublishedDate();
					if (pubDate != null)
					{
						document.setPubDate(pubDate);
					}

					Collection<?> categories = item.getCategories();
					if (categories != null)
					{
						for (Object categoryObject : categories)
						{
							SyndCategory category = (SyndCategory) categoryObject;
							if (category != null && category.getName() != null
									&& !category.getName().isEmpty())
							{
								document.addCategory(category.getName());
							}
						}
					}

					List<?> extraFields = (List<?>) item.getForeignMarkup();
					for (Object extraFieldObject : extraFields)
					{
						Element extraField = (Element) extraFieldObject;
						if (extraField != null && extraField.getNamespaceURI() != null
								&& extraField.getName() != null && extraField.getValue() != null
								&& !extraField.getNamespaceURI().isEmpty()
								&& !extraField.getName().isEmpty()
								&& !extraField.getValue().isEmpty())
						{
							document.addExtraField(extraField.getNamespaceURI(),
									extraField.getName(), extraField.getValue());
						}
					}

					documents.add(document);
				}
			}
		}
		catch (Exception e)
		{
			logger.log(Level.WARNING, "Exception occured while trying to add documents: {0}.", e);
			IOException ioe = new IOException(e);
			logger.throwing(IndexFactory.class.getName(), "addDocuments", ioe);
			throw ioe;
		}
	}

	/**
	 * Loads training examples from XML file and adds them to set of already
	 * known examples. Duplicates are ignored. When next time Zoned index is
	 * built updated set is used to learn zone weights.
	 * 
	 * @param xmlFileLocation
	 * Path to the XML file containing examples description. All examples should
	 * contain valid document references within index source specified for
	 * examples set, otherwise they will be ignored when adjusting weights. If
	 * <code>null</code> is given the initial set of examples remains untouched.
	 * 
	 * @throws IOException
	 * In case file is not found or can't be read.
	 */
	public void addExamples(String xmlFileLocation) throws IOException
	{
		assert examples != null;
		if (xmlFileLocation != null)
		{
			try
			{
				org.dom4j.Document examplesSource = new SAXReader().read(xmlFileLocation);
				List<?> nodes = examplesSource.selectNodes("//ExampleSet/Example");
				for (Object nodeObj : nodes)
				{
					Node node = (Node) nodeObj;
					Node term = node.selectSingleNode("Term");
					Node guid = node.selectSingleNode("Guid");
					Node relevance = node.selectSingleNode("Relevance");
					if (term != null && guid != null && relevance != null)
					{
						examples.add(new TrainingExample(guid.getText().trim(), term.getText(),
								Boolean.parseBoolean(relevance.getText())));
					}
				}
			}
			catch (DocumentException de)
			{
				logger.log(Level.WARNING, "Exception occured while trying to add examples: {0}.",
						de);
				IOException ioe = new IOException(de);
				logger.throwing(IndexFactory.class.getName(), "addExamples", ioe);
				throw ioe;
			}
		}
	}

	/**
	 * Constructs an index of a given type based on the current factory state
	 * (available documents and examples).
	 * 
	 * @param type
	 * Index type. Supported types are ZONED, INVERTED, VECTOR_SPACE and BASIC.
	 * 
	 * @return Newly created index.
	 * 
	 * @throws UnsupportedOperationException
	 * In case the given type is not supported by this method.
	 */
	public Index getIndex(IndexType type) throws UnsupportedOperationException
	{
		assert documents != null;
		Index index = null;
		switch (type)
		{
		case ZONED:
			Collection<ZonedDocument> zonedDocuments = ZonedIndex.convertDocuments(documents);
			index = new ZonedIndex(zonedDocuments, examples);
			break;
		case INVERTED:
			index = new InvertedIndex(documents);
			break;
		case VECTOR_SPACE:
			index = new VectorSpaceIndex(documents);
			break;
		case BASIC:
			index = new Index(documents);
			break;
		default:
			UnsupportedOperationException e = new UnsupportedOperationException("Index of type "
					+ type + " cannot be obtained.");
			logger.throwing(IndexFactory.class.getName(), "getIndex", e);
			throw e;
		}
		assert index != null;
		logger.log(Level.INFO, "{0} index has been successfully built.", type.getReadableName());
		return index;
	}

	/**
	 * Loads documents from RSS feed to factory's documents' set. Old documents
	 * are removed from the set.
	 * 
	 * @param source
	 * String representation of the URL of RSS feed containing items (documents)
	 * to be indexed. If <code>null</code> is given or RSS feed contains no
	 * documents the initial set of documents is truncated.
	 * 
	 * @throws IOException
	 * In case source is not valid URL or RSS feed cannot be parsed.
	 */
	public void setDocuments(String source) throws IOException
	{
		assert documents != null;
		documents.clear();
		addDocuments(source);
	}

	/**
	 * Loads training examples from XML file to factory's examples' set. Old
	 * examples are removed from the set.
	 * 
	 * @param xmlFileLocation
	 * Path to the XML file containing examples description. All examples should
	 * contain valid document references within index source specified for
	 * examples set, otherwise they will be ignored when adjusting weights. If
	 * <code>null</code> is given or the file contains no examples the initial
	 * set of examples is truncated.
	 * 
	 * @throws IOException
	 * In case file is not found or can't be read.
	 */
	public void setExamples(String xmlFileLocation) throws IOException
	{
		assert examples != null;
		examples.clear();
		addExamples(xmlFileLocation);
	}

	private Set<Document> documents = new HashSet<Document>();
	private Set<TrainingExample> examples = new HashSet<TrainingExample>();

	private static final Logger logger = Logger.getLogger("com.iretrieval.index");
}
