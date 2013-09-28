/*******************************************************************************
 * Copyright 2013
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 ******************************************************************************/
package dkpro.similarity.experiments.rte.util;

import static dkpro.similarity.experiments.rte.Pipeline.UTILS_DIR;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import dkpro.similarity.algorithms.lexical.ngrams.CharacterNGramMeasure;
import dkpro.similarity.experiments.rte.Pipeline.Dataset;


public class CharacterNGramIdfValuesGenerator
{
	static final String LF = System.getProperty("line.separator");
	
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
			for (Set<String> doc : docs) {
                allNGrams.addAll(doc);
            }
			
			// Compute idf values			
			for (String ngram : allNGrams)
			{
				double count = 0;
				for (Set<String> doc : docs)
				{					
					if (doc.contains(ngram)) {
                        count++;
                    }
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
