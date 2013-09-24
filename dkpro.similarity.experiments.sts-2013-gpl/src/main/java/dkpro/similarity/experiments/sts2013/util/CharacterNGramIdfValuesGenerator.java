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
package dkpro.similarity.experiments.sts2013.util;

import static dkpro.similarity.experiments.sts2013.Pipeline.DATASET_DIR;
import static dkpro.similarity.experiments.sts2013.Pipeline.UTILS_DIR;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import dkpro.similarity.algorithms.lexical.ngrams.CharacterNGramMeasure;
import dkpro.similarity.experiments.sts2013.Pipeline.Dataset;
import dkpro.similarity.experiments.sts2013.Pipeline.Mode;


public class CharacterNGramIdfValuesGenerator
{
	static final String LF = System.getProperty("line.separator");
	
	@SuppressWarnings("unchecked")
	public static void computeIdfScores(Mode mode, Dataset dataset, int n)
		throws Exception
	{					
		URL inputUrl = ResourceUtils.resolveLocation(DATASET_DIR + "/" + mode.toString().toLowerCase() + "/STS.input." + dataset.toString() + ".txt");
		List<String> lines = IOUtils.readLines(inputUrl.openStream(), "utf-8");

		System.out.println("Computing character " + n + "-grams");
			
		File outputFile = new File(UTILS_DIR + "/character-ngrams-idf/" + mode.toString().toLowerCase() + "/" + n + "/" + dataset.toString() + ".txt");
		
		if (outputFile.exists())
		{
			System.out.println(" - skipping, already exists");
		}
		else
		{			
			Map<String, Double> idfValues = new HashMap<String, Double>();
			
			CharacterNGramMeasure measure = new CharacterNGramMeasure(n, new HashMap<String, Double>());
			
			// Get n-gram representations of texts
			List<Set<String>> docs = new ArrayList<Set<String>>();
			
			for (String line : lines)
			{			
				Set<String> ngrams = measure.getNGrams(line);
				
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
				double idf = Math.log10(lines.size() / idfValues.get(lemma));
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
