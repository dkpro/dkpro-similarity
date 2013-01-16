package de.tudarmstadt.ukp.similarity.experiments.rte.util;

import static de.tudarmstadt.ukp.similarity.experiments.rte.Pipeline.UTILS_DIR;
import static de.tudarmstadt.ukp.similarity.experiments.rte.Pipeline.DATASET_DIR;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.ngrams.CharacterNGramMeasure;
import de.tudarmstadt.ukp.similarity.experiments.rte.Pipeline.Dataset;


public class CharacterNGramIdfValuesGenerator
{
	static final String LF = System.getProperty("line.separator");
	
	@SuppressWarnings("unchecked")
	public static void computeIdfScores(Dataset dataset, int n)
		throws Exception
	{	
		System.out.println("Computing character " + n + "-grams");
			
		File outputFile = new File(UTILS_DIR + "/character-ngrams-idf/" + n + "/" + RteUtil.getCommonDatasetName(dataset) + ".txt");
		
		if (outputFile.exists())
		{
			System.out.println(" - skipping, already exists");
		}
		else
		{
			// Input data
			File inputDir = new File(UTILS_DIR + "/plaintexts/" + RteUtil.getCommonDatasetName(dataset));
			
			Collection<File> files = FileUtils.listFiles(
					inputDir,
					new String[] { "txt" },
					false);

			// Map to hold the idf values
			Map<String, Double> idfValues = new HashMap<String, Double>();
			
			CharacterNGramMeasure measure = new CharacterNGramMeasure(n, new HashMap<String, Double>());
			
			// Get n-gram representations of texts
			List<Set<String>> docs = new ArrayList<Set<String>>();
			
			for (File file : files)
			{			
				Set<String> ngrams = measure.getNGrams(FileUtils.readFileToString(file));
				
				docs.add(ngrams);
			}
			
			// Get all ngrams
			Set<String> allNGrams = new HashSet<String>();
			for (Set<String> doc : docs)
				allNGrams.addAll(doc);
			
			// Compute idf values			
			for (String ngram : allNGrams)
			{
				double count = 0;
				for (Set<String> doc : docs)
				{					
					if (doc.contains(ngram))
						count++;
				}
				idfValues.put(ngram, count);
			}
			
			// Compute the idf
			for (String lemma : idfValues.keySet())
			{
				double idf = Math.log10(files.size() / idfValues.get(lemma));
				idfValues.put(lemma, idf);
			}
			
			// Store persistently
			StringBuilder sb = new StringBuilder();
			for (String key : idfValues.keySet())
			{
				sb.append(key + "\t" + idfValues.get(key) + LF);
			}
			FileUtils.writeStringToFile(outputFile, sb.toString());
			
			System.out.println(" - done");
		}
	}
}
