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
package dkpro.similarity.experiments.sts2013baseline.util;

import static dkpro.similarity.experiments.sts2013baseline.Pipeline.DATASET_DIR;
import static dkpro.similarity.experiments.sts2013baseline.Pipeline.UTILS_DIR;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

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
import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import dkpro.similarity.experiments.sts2013baseline.Pipeline.Dataset;
import dkpro.similarity.experiments.sts2013baseline.Pipeline.Mode;


public class WordIdfValuesGenerator
{
	static final String LF = System.getProperty("line.separator");
	
	public static void computeIdfScores(Mode mode, Dataset dataset)
		throws Exception
	{	
		URL inputUrl = ResourceUtils.resolveLocation(DATASET_DIR + "/" + mode.toString().toLowerCase() + "/STS.input." + dataset.toString() + ".txt");
		List<String> lines = IOUtils.readLines(inputUrl.openStream(), "utf-8");
		
		Map<String,Double> idfValues = new HashMap<String,Double>();
		
		File outputFile = new File(UTILS_DIR + "/word-idf/" + mode.toString().toLowerCase() + "/" + dataset.toString() + ".txt");
		
		System.out.println("Computing word idf values");
		
		if (outputFile.exists())
		{
			System.out.println(" - skipping, already exists");
		}
		else
		{	
			System.out.println(" - this may take a while...");
			
			// Build up token representations of texts
			Set<List<String>> docs = new HashSet<List<String>>();
		
			for (String line : lines)
			{
				List<String> doc = new ArrayList<String>();
				
				Collection<Lemma> lemmas = getLemmas(line);
				
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
						StanfordLemmatizer.class)
				);    	
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);
		
		JCas jcas = ae.newJCas();
		jcas.setDocumentLanguage("en");
		jcas.setDocumentText(fileContents);
		
		ae.process(jcas);
		
		Collection<Lemma> lemmas = JCasUtil.select(jcas, Lemma.class);
		
		return lemmas;
	}
}
