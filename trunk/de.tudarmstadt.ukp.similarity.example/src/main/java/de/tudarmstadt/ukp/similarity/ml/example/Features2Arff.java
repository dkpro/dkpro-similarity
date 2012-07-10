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
		// We use the same feature set that FeatureGeneration.java is also using
		List<File> files = new ArrayList<File>();
		files.add(new File("src/main/resources/features/content/word-ngrams/NGramContainmentMeasure_3grams.txt"));
		files.add(new File("src/main/resources/features/content/word-ngrams/NGramJaccardMeasure_3grams.txt"));
		
		String arff = ArffConverter.toArffString(files, null);
		
		FileUtils.writeStringToFile(new File("src/main/resources/models/train.arff"), arff);
	}
}
