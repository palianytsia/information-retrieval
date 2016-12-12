package ua.edu.ukma.fin.iretrieval.index;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import ua.edu.ukma.fin.iretrieval.Document;
import ua.edu.ukma.fin.iretrieval.Query;
import ua.edu.ukma.fin.iretrieval.TermStatistics;

public class VectorSpaceIndex extends InvertedIndex
{
	/**
	 * Simply calls the {@link InvertedIndex#InvertedIndex(Collection)
	 * superclass constructor}
	 * 
	 * @param documents
	 * Collection of the documents to be indexed, duplicates will be ignored
	 */
	public VectorSpaceIndex(Collection<? extends Document> documents)
	{
		super(documents);
	}

	/**
	 * When documents are represented as vectors we can compute the the
	 * similarity between them as cosine of the angle between two vectors that
	 * represent these documents.
	 * 
	 * @see "Introduction retrieval. 6.3.1 Dot products"
	 * 
	 * @param a
	 * Document for the first vector.
	 * 
	 * @param b
	 * Document for the second vector.
	 * 
	 * @return {@link #getCosineTeta(double[], double[]) Cosine of the angle}
	 * between documents' vectors.
	 */
	protected double getCosineSimilarity(Document a, Document b)
	{
		double[] componentsA = getVectorComponents(a, getDictionary());
		double[] componentsB = getVectorComponents(b, getDictionary());
		return getCosineTeta(componentsA, componentsB);
	}

	/**
	 * We can view a query as a very small document, and that gives us a
	 * possibility to compute cosine similarity between query and document like
	 * we do it for {@link #getCosineSimilarity(Document, Document) two
	 * documents}.
	 * 
	 * @see "Introduction retrieval. 6.3.2 Queries as vectors"
	 * 
	 * @param document
	 * Document for the first vector.
	 * 
	 * @param query
	 * Query for the second vector.
	 * 
	 * @return {@link #getCosineTeta(double[], double[]) Cosine of the angle}
	 * between document's and query's vectors.
	 */
	protected double getCosineSimilarity(Document document, Query query)
	{
		Set<String> terms = new HashSet<String>();
		terms.addAll(getDictionary());
		terms.addAll(query.getTerms());
		double[] componentsA = getVectorComponents(document, terms);
		double[] componentsB = getVectorComponents(query, terms);
		return getCosineTeta(componentsA, componentsB);
	}

	@Override
	protected Comparator<Document> getDocumentComparator(final Query query)
	{
		return new Comparator<Document>()
		{
			public int compare(Document a, Document b)
			{
				return Double.valueOf(getCosineSimilarity(a, query)).compareTo(
						Double.valueOf(getCosineSimilarity(b, query)));
			}
		};
	}

	/**
	 * When documents are represented as vectors we can measure how close they
	 * are by computing the Euclidean distance between the two documents'
	 * vectors.
	 * 
	 * @param a
	 * Document for the first vector.
	 * 
	 * @param b
	 * Document for the second vector.
	 * 
	 * @return The results of {@link #getEuclideanDistance(double[], double[])
	 * Euclidean Distance computation}.
	 */
	protected double getEuclideanDistance(Document a, Document b)
	{
		double[] componentsA = getVectorComponents(a, getDictionary());
		double[] componentsB = getVectorComponents(b, getDictionary());
		return getEuclideanDistance(componentsA, componentsB);
	}

	/**
	 * Computes the cosine of the <a href=
	 * "http://chemistry.about.com/od/workedchemistryproblems/a/scalar-product-vectors-problem.htm"
	 * >angle between two vectors</a>.
	 * 
	 * @param componentsA
	 * The components of the first vector.
	 * 
	 * @param componentsB
	 * The components of the second vector.
	 * 
	 * @return Cosine of the angle between two vectors or zero if components
	 * arrays have different dimensions or are empty.
	 */
	private double getCosineTeta(double[] componentsA, double[] componentsB)
	{
		double cosineTeta = 0.0;
		if (componentsA.length == componentsB.length)
		{
			for (int i = 0; i < componentsA.length; i++)
			{

				cosineTeta += componentsA[i] * componentsB[i];
			}
		}
		cosineTeta = cosineTeta
				/ (getEuclideanLength(componentsA) * getEuclideanLength(componentsB));
		return cosineTeta;
	}

	/**
	 * Computes the <a
	 * href="http://en.wikipedia.org/wiki/Euclidean_distance">Euclidean
	 * Distance</a> between two vectors.
	 * 
	 * @param componentsA
	 * The components of the first vector.
	 * 
	 * @param componentsB
	 * The components of the second vector.
	 * 
	 * @return Euclidean Distance between two vectors or zero if components
	 * arrays have different dimensions or are empty.
	 */
	private double getEuclideanDistance(double[] componentsA, double[] componentsB)
	{
		double euclideanDistance = 0.0;
		if (componentsA.length == componentsB.length)
		{
			for (int i = 0; i < componentsA.length; i++)
			{
				euclideanDistance = Math.pow(componentsA[i] - componentsB[i], 2);
			}
		}
		euclideanDistance = Math.sqrt(euclideanDistance);
		return euclideanDistance;
	}

	/**
	 * Computes the intuitive notion of length of the vector known as <a
	 * href="http://en.wikipedia.org/wiki/Euclidean_norm#Euclidean_norm"
	 * >Euclidean length (Euclidean norm)</a> that is used for other
	 * computations.
	 * 
	 * In information retrieval it used for normalization of documents vector
	 * length in order to reduce impact of higher term frequency in longer
	 * documents.
	 * 
	 * @param components
	 * The components of the vector.
	 * 
	 * @return Euclidean length of the vector or zero if components array is
	 * empty.
	 */
	private double getEuclideanLength(double[] components)
	{
		double euclideanLength = 0.0;
		for (int i = 0; i < components.length; i++)
		{
			euclideanLength += Math.pow(components[i], 2);
		}
		euclideanLength = Math.sqrt(euclideanLength);
		return euclideanLength;
	}

	/**
	 * We can model a vector space where each axis will a term and coordinates
	 * an such an axis will be term frequencies for this term. So any object
	 * (e.g. document) that provide term statistics can be treated as a vector
	 * in this model. This method returns vector components (coordinates) for
	 * such an object.
	 * 
	 * @param statistics
	 * Object to be transformed to a vector.
	 * 
	 * @param spaceAxes
	 * Collection of terms to be used as axes in vector space.
	 * 
	 * @return Components (coordinates) of the vector.
	 */
	private double[] getVectorComponents(TermStatistics statistics, Collection<String> spaceAxes)
	{
		double[] components = new double[spaceAxes.size()];
		int i = 0;
		for (String term : spaceAxes)
		{
			components[i] = statistics.getTermFrequency(term);
			i++;
		}
		return components;
	}
}
