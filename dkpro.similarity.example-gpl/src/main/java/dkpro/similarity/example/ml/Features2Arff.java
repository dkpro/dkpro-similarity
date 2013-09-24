package dkpro.similarity.example.ml;

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
		// TRAIN
		
		String F_BASE_PATH = "/home/danielb/Projekte/Similarity/workspace/de.tudarmstadt.ukp.similarity-asl/de.tudarmstadt.ukp.similarity.example";
		String G_BASE_PATH = "/home/danielb/Projekte/Similarity/workspace/de.tudarmstadt.ukp.similarity-asl/de.tudarmstadt.ukp.similarity.dkpro.data-asl";
		
		File inputDir = new File(F_BASE_PATH + "/src/main/resources/semeval-train-all-combined-features");
		
		String arff = ArffConverter.toArffString(
				listFiles(inputDir, ".txt", true),
				new File(G_BASE_PATH + "/src/main/resources/goldstandards/semeval/train/STS.gs.ALLcombined.txt"));
		
		FileUtils.writeStringToFile(new File("src/main/resources/models/semeval-train-all-combined.arff"), arff);
		
		// TEST
		
		String BASE_PATH = "/home/danielb/Projekte/Similarity/workspace/de.tudarmstadt.ukp.similarity-asl/de.tudarmstadt.ukp.similarity.example";
		inputDir = new File(BASE_PATH + "/src/main/resources/mm09-features");
		
		arff = ArffConverter.toArffString(
				listFiles(inputDir, ".txt", true),
				null);
		
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
