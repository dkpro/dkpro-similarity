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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.springframework.util.CollectionUtils;
import org.uimafit.factory.AggregateBuilder;
import org.uimafit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader;
import de.tudarmstadt.ukp.similarity.experiments.coling2012.Pipeline.Dataset;


public class WordIdfValuesGenerator
{
	static final String LF = System.getProperty("line.separator");
	
	@SuppressWarnings("unchecked")
	public static void computeIdfScores(Dataset dataset)
		throws Exception
	{	
		File outputFile = new File(UTILS_DIR + "/word-idf/" + dataset.toString() + ".txt");
		
		System.out.println("Computing word idf values");
		
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
			
			// POS Tagging
			AnalysisEngineDescription pos = createPrimitiveDescription(
					OpenNlpPosTagger.class,
					OpenNlpPosTagger.PARAM_LANGUAGE, "en");		
			builder = new AggregateBuilder();
			builder.add(pos, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
			builder.add(pos, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
			AnalysisEngine aggr_pos = builder.createAggregate();
			
			// Lemmatization
			AnalysisEngineDescription lem = createPrimitiveDescription(
					StanfordLemmatizer.class);		
			builder = new AggregateBuilder();
			builder.add(lem, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
			builder.add(lem, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
			AnalysisEngine aggr_lem = builder.createAggregate();
			
			// Output Writer
			AnalysisEngine writer = createPrimitive(WordIdfValuesGeneratorWriter.class,
					WordIdfValuesGeneratorWriter.PARAM_OUTPUT_FILE, outputFile.getAbsolutePath());
	
			SimplePipeline.runPipeline(reader, aggr_seg, aggr_pos, aggr_lem, writer);

			// Now we have the text format lemma1###lemma2###...###lemman
			List<String> lines = FileUtils.readLines(outputFile);
		
			Map<String,Double> idfValues = new HashMap<String,Double>();
		
			// Build up token representations of texts
			Set<List<String>> docs = new HashSet<List<String>>();
		
			for (String line : lines)
			{
				List<String> doc = CollectionUtils.arrayToList(line.split("###"));
				
				docs.add(doc);
			}
			
			// Get the shared token list
			Set<String> tokens = new HashSet<String>();
			for (List<String> doc : docs)
				tokens.addAll(doc);
			
			// Get the idf numbers
			for (String token : tokens)
			{
				double count = 0;
				for (List<String> doc : docs)
				{
					if (doc.contains(token))
						count++;
				}
				idfValues.put(token, count);
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
