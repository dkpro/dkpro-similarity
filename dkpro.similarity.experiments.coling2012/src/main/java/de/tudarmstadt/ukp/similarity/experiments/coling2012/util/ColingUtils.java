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

import static de.tudarmstadt.ukp.similarity.experiments.coling2012.Pipeline.DATASET_DIR;
import static de.tudarmstadt.ukp.similarity.experiments.coling2012.Pipeline.GOLDSTANDARD_DIR;
import static de.tudarmstadt.ukp.similarity.experiments.coling2012.Pipeline.UTILS_DIR;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.similarity.dkpro.io.CloughCorpusReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader.CombinationStrategy;
import de.tudarmstadt.ukp.similarity.dkpro.io.MeterCorpusReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.SemEvalCorpusReader;
import de.tudarmstadt.ukp.similarity.experiments.coling2012.Pipeline.Dataset;


public class ColingUtils
{
	public static File getDatasetDir(Dataset dataset)
	{
		switch(dataset)
		{
			case WikipediaRewriteCorpus:
				return new File(DATASET_DIR.replace("###", "wp-rewrite"));
			case MeterCorpus:
				return new File(DATASET_DIR.replace("###", "meter"));
			case WebisCrowdParaphraseCorpus:
				return new File(DATASET_DIR.replace("###", "webis-cpc"));
			default:
				return null;
		}
	}
	
	public static File getGoldstandard(Dataset dataset)
	{
		switch(dataset)
		{
			case WikipediaRewriteCorpus:
				return new File(GOLDSTANDARD_DIR.replace("###", "wp-rewrite") + "/wp-rewrite-corpus_gold.txt");
			case MeterCorpus:
				return new File(GOLDSTANDARD_DIR.replace("###", "meter") + "/meter_gold_binary.txt");
			case WebisCrowdParaphraseCorpus:
				return new File(GOLDSTANDARD_DIR.replace("###", "webis-cpc") + "/webis-cpc.txt");
			default:
				return null;
		}
	}
	
	public static CollectionReader getCollectionReader(Dataset dataset)
		throws ResourceInitializationException
	{
		switch(dataset)
		{
		case WikipediaRewriteCorpus:
			CollectionReader clough = CollectionReaderFactory.createCollectionReader(
				CloughCorpusReader.class,
				CloughCorpusReader.PARAM_INPUT_DIR, getDatasetDir(dataset).getAbsolutePath(),
				CloughCorpusReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
			return clough;
		case MeterCorpus:
			CollectionReader meter = CollectionReaderFactory.createCollectionReader(
				MeterCorpusReader.class,
				MeterCorpusReader.PARAM_INPUT_DIR, getDatasetDir(dataset).getAbsolutePath(),
				MeterCorpusReader.PARAM_COLLECTION, MeterCorpusReader.MeterCorpusCollection.SINGLE_SOURCE_SUBSET.toString(),
				MeterCorpusReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
			return meter; 
		case WebisCrowdParaphraseCorpus:
			CollectionReader webis = CollectionReaderFactory.createCollectionReader(
				SemEvalCorpusReader.class,
				SemEvalCorpusReader.PARAM_INPUT_FILE, getDatasetDir(dataset).getAbsolutePath() + "/webis-cpc.txt",
				SemEvalCorpusReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
			return webis;
		default:
			return null;
		}
	}
	
	public static void generateDocumentOrder(Dataset dataset)
		throws UIMAException, IOException
	{
		CollectionReader reader = getCollectionReader(dataset);
		
		AnalysisEngine writer = AnalysisEngineFactory.createPrimitive(
				DocumentOrderWriter.class,
				DocumentOrderWriter.PARAM_OUTPUT_FILE, UTILS_DIR + "/doc-order/" + dataset.toString() + ".txt");
		
		SimplePipeline.runPipeline(reader, writer);
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> readGoldstandard(Dataset dataset)
		throws IOException
	{
		List<String> gold = new ArrayList<String>();
		
		if (dataset.equals(Dataset.MeterCorpus))
		{
			List<String> originalLines = FileUtils.readLines(getGoldstandard(dataset));
			originalLines.remove(0);	// remove header
		
			List<String> order = FileUtils.readLines(new File(UTILS_DIR + "/doc-order/" + dataset.toString() + ".txt"));
			
			// Process documents in the correct order
			for (String line : order)
			{
				String[] linesplit = line.split("\t");
				String docID = linesplit[0].substring(linesplit[0].indexOf("/newspapers/") + 1) + ".txt";
				
				// Look up document in the original gold standard file
				for (String origLine : originalLines)
				{
					String[] origLineSplit = origLine.split("\t");
					if (origLineSplit[0].equals(docID))
					{
						gold.add(origLineSplit[4]);
						break;
					}
				}
			}
		}
		else if (dataset.equals(Dataset.WikipediaRewriteCorpus))
		{
			List<String> originalLines = FileUtils.readLines(getGoldstandard(dataset));
			originalLines.remove(0);	// remove header
			
			List<String> order = FileUtils.readLines(new File(UTILS_DIR + "/doc-order/" + dataset.toString() + ".txt"));
			
			// Process documents in the correct order
			for (String line : order)
			{
				String[] linesplit = line.split("\t");
				String docID = linesplit[0] + ".txt";
				
				// Look up document in the original gold standard file
				for (String origLine : originalLines)
				{
					String[] origLineSplit = origLine.split("\t");
					if (origLineSplit[0].equals(docID))
					{
						gold.add(origLineSplit[4]);
						break;
					}
				}
			}
		}		
		else if (dataset.equals(Dataset.WebisCrowdParaphraseCorpus))
		{
			gold = FileUtils.readLines(getGoldstandard(dataset));			
		}
		
		return gold;
	}
}
