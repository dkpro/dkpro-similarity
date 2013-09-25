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
package de.tudarmstadt.ukp.similarity.experiments.coling2012.util;

import static de.tudarmstadt.ukp.similarity.experiments.coling2012.Pipeline.UTILS_DIR;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitive;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.uimafit.factory.AggregateBuilder;
import org.uimafit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.ngrams.CharacterNGramMeasure;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader;
import de.tudarmstadt.ukp.similarity.experiments.coling2012.Pipeline.Dataset;


public class CharacterNGramIdfValuesGenerator
{
	static final String LF = System.getProperty("line.separator");
	
	@SuppressWarnings("unchecked")
	public static void computeIdfScores(Dataset dataset, int n)
		throws Exception
	{					
		File outputFile = new File(UTILS_DIR + "/character-ngrams-idf/" + n + "/" + dataset.toString() + ".txt");
		
		System.out.println("Computing character " + n + "-grams");
		
		if (outputFile.exists())
		{
			System.out.println(" - skipping, already exists");
		}
		else
		{	
			System.out.println(" - this may take a while...");
		
			CollectionReader reader = ColingUtils.getCollectionReader(dataset);
			
			// Tokenization
			AnalysisEngineDescription seg = createPrimitiveDescription(
					BreakIteratorSegmenter.class);
			AggregateBuilder builder = new AggregateBuilder();
			builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
			builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
			AnalysisEngine aggr_seg = builder.createAggregate();
			
			// Output Writer
			AnalysisEngine writer = createPrimitive(CharacterNGramIdfValuesGeneratorWriter.class,
					CharacterNGramIdfValuesGeneratorWriter.PARAM_OUTPUT_FILE, outputFile.getAbsolutePath());
	
			SimplePipeline.runPipeline(reader, aggr_seg, writer);

			// We now have plain text format
			List<String> lines = FileUtils.readLines(outputFile);
		
			Map<String,Double> idfValues = new HashMap<String,Double>();
		
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
