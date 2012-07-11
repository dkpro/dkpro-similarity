package de.tudarmstadt.ukp.similarity.ml.example;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import de.tudarmstadt.ukp.similarity.ml.util.ArffConverter;


public class Features2Arff
{
	public static void main(String[] args)
		throws Exception
	{
		String BASE_PATH = "/home/danielb/Projekte/Similarity/workspace/de.tudarmstadt.ukp.similarity-asl/de.tudarmstadt.ukp.similarity.example";
		File inputDir = new File(BASE_PATH + "/src/main/resources/mm09-features");
		
		List<File> files = listFiles(inputDir, ".txt", true);		

		String arff = ArffConverter.toArffString(files, null);
		
		FileUtils.writeStringToFile(new File("src/main/resources/models/mm09.arff"), arff);
	}
	
	private static List<File> listFiles(File folder, String suffix, boolean recursively)
	{
		List<File> files = new ArrayList<File>();
		
		String s = folder.getAbsolutePath();
		
		for (File file : folder.listFiles())
		{
			if (file.isDirectory())
			{
				if (recursively && !file.getName().startsWith("."))
					files.addAll(listFiles(file, suffix, recursively));
			} else {
				if (!file.getName().startsWith(".") && 
					file.getName().endsWith(suffix))
					files.add(file);
			}
		}
		
		return files;
	}
}
