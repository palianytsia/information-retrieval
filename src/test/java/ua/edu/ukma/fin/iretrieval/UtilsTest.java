package ua.edu.ukma.fin.iretrieval;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilsTest
{
	@Test
	public void testCountTerms()
	{
		assertEquals(8, Utils.countTerms("metadata", plain));
		assertEquals(8, Utils.countTerms("MetaData", plain));
	}

	@Test
	public void testNormalize()
	{
		assertEquals(plain, Utils.normalize(html));
	}

	private final String html = "<p>First, we want to make our systems highly configurable. "
			+ "Not just things such as screen colors and prompt text, but deeply "
			+ "ingrained items such as the choice of algorithms, database products, "
			+ "middleware technology, and user-interface style. These items should "
			+ "be implemented as configuration options, not through integration "
			+ "or engineering.</p><p><strong>Use <em>metadata</em> to describe "
			+ "configuration options for an application: tuning parameters, user "
			+ "preferences, the installation directory, and so on.</strong></p>"
			+ "<p>What exactly is metadata? Strictly speaking, metadata is data "
			+ "about data. The most common example is probably a database schema "
			+ "or data dictionary. A schema contains data that describes fields "
			+ "(columns) in terms of names, storage lengths, and other attributes. "
			+ "You should be able to access and manipulate this information just "
			+ "as you would any other data in the database.</p><p>We use the term "
			+ "in its broadest sense. Metadata is any data that describes the "
			+ "application—how it should run, what resources it should use, and "
			+ "so on. Typically, metadata is accessed and used at runtime, not "
			+ "at compile time. You use metadata all the time—at least your programs "
			+ "do. Suppose you click on an option to hide the toolbar on your "
			+ "Web browser. The browser will store that preference, as metadata, "
			+ "in some sort of internal database.</p><p>This database might be "
			+ "in a proprietary format, or it might use a standard mechanism. "
			+ "Under Windows, either an initialization file (using the suffix .ini) "
			+ "or entries in the system Registry are typical. Under Unix, the "
			+ "X Window System provides similar functionality using Application "
			+ "Default files. Java uses Property files. In all of these environments, "
			+ "you specify a key to retrieve a value. Alternatively, more powerful "
			+ "and flexible implementations of metadata use an embedded scripting language.</p>";

	private final String plain = "first we want to make our systems highly configurable "
			+ "not just things such as screen colors and prompt text but deeply "
			+ "ingrained items such as the choice of algorithms database products "
			+ "middleware technology and user interface style these items should "
			+ "be implemented as configuration options not through integration "
			+ "or engineering use metadata to describe configuration options for "
			+ "an application tuning parameters user preferences the installation "
			+ "directory and so on what exactly is metadata strictly speaking "
			+ "metadata is data about data the most common example is probably "
			+ "database schema or data dictionary schema contains data that describes "
			+ "fields columns in terms of names storage lengths and other attributes "
			+ "you should be able to access and manipulate this information just "
			+ "as you would any other data in the database we use the term in "
			+ "its broadest sense metadata is any data that describes the application "
			+ "how it should run what resources it should use and so on typically "
			+ "metadata is accessed and used at runtime not at compile time you "
			+ "use metadata all the time at least your programs do suppose you "
			+ "click on an option to hide the toolbar on your web browser the "
			+ "browser will store that preference as metadata in some sort of "
			+ "internal database this database might be in proprietary format "
			+ "or it might use standard mechanism under windows either an initialization "
			+ "file using the suffix ini or entries in the system registry are "
			+ "typical under unix the window system provides similar functionality "
			+ "using application default files java uses property files in all "
			+ "of these environments you specify key to retrieve value alternatively "
			+ "more powerful and flexible implementations of metadata use an embedded scripting language";

}
