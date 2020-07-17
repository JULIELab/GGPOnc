package de.julielab.dictionaryhandling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class CreateLargeDictionary
{
	public static void main(String[] args) throws IOException// , UIMAException
	{
		Path umlsDir = Paths.get("src", "main", "resources", "UMLS-Dictionaries");

		Stream<Path> fileList = Files
				.walk(umlsDir)
				.filter(Files::isRegularFile)
				.sorted();

		String globalDictionary = "";
		
		for (Iterator<Path> iterator = fileList.iterator(); iterator.hasNext();)
		{
			Path dictPath = iterator.next();
			System.out.println(dictPath);
			
			String fileName = dictPath.getFileName().toString();
			String[] part = fileName.split("-");
			String dictName = part[2];
			
			List<String> lines = Files.readAllLines(dictPath);
			for (int i = 0; i < lines.size(); i++)
			{
				String[] line = lines.get(i).split("\\|");
				String entry = line[0] + "\t" + dictName;
				globalDictionary = globalDictionary + entry + "\n";
			}
		}
		
		Path globalDicPath = Paths.get(umlsDir.toString(), "global_dictionary.txt");
		
		Files.write(globalDicPath, globalDictionary.getBytes());
	}
}

// TODO Gene Dictionary from https://zenodo.org/record/3874895#.XxG0Zh0aRhE