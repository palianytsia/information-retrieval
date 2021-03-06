package ua.edu.ukma.fin.iretrieval.index;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ua.edu.ukma.fin.iretrieval.TrainingExample;
import ua.edu.ukma.fin.iretrieval.ZoneName;

public class ZonedIndexTest extends IndexTest
{
	@Before
	public void setUp()
	{
		super.setUp();
		Set<TrainingExample> trainingExamples = new HashSet<TrainingExample>();
		trainingExamples.add(new TrainingExample("Document a", "policy", true));
		trainingExamples.add(new TrainingExample("Document b", "policy", false));
		trainingExamples.add(new TrainingExample("Document c", "policy", false));
		trainingExamples.add(new TrainingExample("Document a", "car", true));
		trainingExamples.add(new TrainingExample("Document b", "car", true));
		trainingExamples.add(new TrainingExample("Document c", "car", true));
		trainingExamples.add(new TrainingExample("Document a", "auto", true));
		trainingExamples.add(new TrainingExample("Document b", "auto", true));
		trainingExamples.add(new TrainingExample("Document c", "auto", true));
		trainingExamples.add(new TrainingExample("Document a", "tractor", false));
		trainingExamples.add(new TrainingExample("Document b", "tractor", false));
		trainingExamples.add(new TrainingExample("Document c", "tractor", false));
		zonedIndex = new ZonedIndex(ZonedIndex.convertDocuments(IndexTest.docs), trainingExamples);
		assertNotNull(zonedIndex);
	}
	
	@After
	public void tearDown() {
		super.tearDown();
		zonedIndex = null;
	}

	@Test
	public void testAdjustWeights()
	{
		assertTrue(zonedIndex.getZoneWeight(ZoneName.Title) > zonedIndex.getZoneWeight(ZoneName.Description));
	}
	
	@Test
	public void testGetWeightedZoneScoreZonedDocumentQuery()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetWeightedZoneScoreZonedDocumentString()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetZoneWeight()
	{
		double totalWeight = 0.0;
		System.out.println("==== Zones weights ====");
		for (ZoneName name : ZoneName.values())
		{
			Double weight = zonedIndex.getZoneWeight(name);
			assertTrue(weight > 0.0);
			totalWeight += weight;
			System.out.println(name.toString() + " " + weight);
		}
		System.out.println("=======================");
		assertTrue(1.0 == totalWeight);
	}
	
	private ZonedIndex zonedIndex = null;
}
