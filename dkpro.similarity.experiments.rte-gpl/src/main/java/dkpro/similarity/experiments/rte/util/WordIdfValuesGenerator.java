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
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.gate.GateLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import dkpro.similarity.experiments.rte.Pipeline.Dataset;


public class WordIdfValuesGenerator
{
	static final String LF = System.getProperty("line.separator");
	
	public static void computeIdfScores(Dataset dataset)
		throws Exception
	{							
		File outputFile = new File(UTILS_DIR + "/word-idf/" + RteUtil.getCommonDatasetName(dataset) + ".txt");
		
		System.out.println("Computing word idf values");
		
		if (outputFile.exists())
		{
			System.out.println(" - skipping, already exists");
		}
		else
		{	
			System.out.println(" - this may take a while...");
			
			// Input data
			File inputDir = new File(UTILS_DIR + "/plaintexts/" + RteUtil.getCommonDatasetName(dataset));
			
			Collection<File> files = FileUtils.listFiles(
					inputDir,
					new String[] { "txt" },
					false);
			
			// Map to hold the idf values
			Map<String,Double> idfValues = new HashMap<String,Double>();
			
			// Build up token representations of texts
			Set<List<String>> docs = new HashSet<List<String>>();
		
			for (File file : files)
			{
				List<String> doc = new ArrayList<String>();
				
				Collection<Lemma> lemmas = getLemmas(FileUtils.readFileToString(file));
				
				for (Lemma lemma : lemmas)
				{
					try
					{
						String token = lemma.getValue().toLowerCase();			
						doc.add(token);
					}
					catch (NullPointerException e)
					{
						System.err.println(" - unparsable token: " + lemma.getCoveredText());
					}
				}
				
				docs.add(doc);
			}
			
			// Get the shared token list
			Set<String> tokens = new HashSet<String>();
			for (List<String> doc : docs) {
                tokens.addAll(doc);
            }
			
			// Get the idf numbers
			for (String token : tokens)
			{
				double count = 0;
				for (List<String> doc : docs)
				{
					if (doc.contains(token)) {
                        count++;
                    }
				}
				idfValues.put(token, count);
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
	
	private static Collection<Lemma> getLemmas(String fileContents)
		throws Exception
	{
		AnalysisEngineDescription aed = createEngineDescription(
				createEngineDescription(
						BreakIteratorSegmenter.class),
				createEngineDescription(
						OpenNlpPosTagger.class,
						OpenNlpPosTagger.PARAM_LANGUAGE, "en"),
				createEngineDescription(
						GateLemmatizer.class)
				);    	
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);
		
		JCas jcas = ae.newJCas();
		jcas.setDocumentText(fileContents);
		
		ae.process(jcas);
		
		Collection<Lemma> lemmas = JCasUtil.select(jcas, Lemma.class);
		
		return lemmas;
	}
}
