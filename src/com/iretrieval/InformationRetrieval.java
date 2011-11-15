package com.iretrieval;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class InformationRetrieval
{
	public static void main(String[] args)
	{
		String source = null;
		for (int i = 0; i < args.length - 1; i++)
		{
			if (args[i].equals("-s"))
			{
				source = args[i + 1];
				break;
			}
		}
		if (source == null)
		{
			System.err
					.println("You should launch the program with -s [feedURL], where feedURL is a link to RSS feed describing the documents to be indexed. Program will terminate now.");
			System.exit(-1);
		}
		try
		{
			URL feedURL = new URL(source);
			InvertedIndex index = InvertedIndex.getInstance(feedURL);
			System.out
					.println("Index is build. Now you are able to run queries and retrieve documents. Type exit to quit.");
			Scanner in = new Scanner(System.in);
			String command = "";
			while (!command.equals("exit"))
			{
				System.out.print("Query> ");
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
		catch (MalformedURLException e)
		{
			System.err.println("MalformedURL was given as source for the index: " + source
					+ ". Program will terminate now.");
			System.exit(-1);
		}
	}
}