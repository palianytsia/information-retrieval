package com.iretrieval;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.iretrieval.index.Index;
import com.iretrieval.index.IndexFactory;
import com.iretrieval.index.IndexType;

public class SearchEngine
{
	public static void main(String[] args)
	{
		Map<String, String> argsMap = parseArgs(args);

		// Define the index type from user input
		// Set it to the most primitive index type if user hasn't specified
		// index type or has specified it incorrectly.
		IndexType indexType = null;
		try
		{
			indexType = IndexType.valueOf(argsMap.get("-t"));
		}
		catch (IllegalArgumentException iae)
		{
			System.err.println(argsMap.get("-t") + " is not a valid index type. Valid types are: "
					+ Arrays.toString(IndexType.values()) + ". "
					+ IndexType.BASIC.getReadableName() + " index will be build instead.");
			indexType = IndexType.BASIC;
		}
		catch (NullPointerException ne)
		{
			indexType = IndexType.BASIC;
		}

		// Create index factory based on user input
		// and try to obtain the index of the desired type.
		IndexFactory indexFactory = new IndexFactory();
		try
		{
			indexFactory.setDocuments(argsMap.get("-s"));
		}
		catch (IOException e)
		{
			System.err.println("Failed to build index from the specified source."
					+ " Program will terminate now.");
			System.exit(-1);
		}
		try
		{
			indexFactory.setExamples(argsMap.get("-e"));
		}
		catch (IOException e)
		{
			System.err.println("The file you have specified as one containnig "
					+ "training examples is either invalid or not found. "
					+ "Examples won't be used for weights adjustment.");
		}
		Index index = indexFactory.getIndex(indexType);
		System.out.println("Index has been built. Now you are able to run "
				+ "queries and retrieve documents. Type exit to quit.");

		// If we got to this point we are ready to handle queries :)
		Scanner in = new Scanner(System.in);
		String command = "";
		while (!command.equals("exit"))
		{
			System.out.print("Query> ");
			if (in.hasNextLine())
			{
				command = in.nextLine();
				if (!command.equals("exit"))
				{
					int i = 0;
					for (Document document : index.retrieveDocuments(new Query(command)))
					{
						System.out.println(++i + ") " + document.toString());
					}
				}
			}
		}
	}

	private static Map<String, String> parseArgs(String[] args)
	{
		Map<String, String> argsMap = new HashMap<String, String>();
		for (int i = 0; i < args.length - 1; i++)
		{
			if (args[i].startsWith("-") && !args[i + 1].startsWith("-"))
			{
				argsMap.put(args[i], args[i + 1]);
			}
		}
		return argsMap;
	}

}