package de.tudarmstadt.ukp.similarity.experiments.semeval2013.util;

import static de.tudarmstadt.ukp.similarity.experiments.semeval2013.Pipeline.DATASET_DIR;
import static de.tudarmstadt.ukp.similarity.experiments.semeval2013.Pipeline.UTILS_DIR;

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
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.similarity.experiments.semeval2013.Pipeline.Dataset;
import de.tudarmstadt.ukp.similarity.experiments.semeval2013.Pipeline.Mode;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;


public class WordIdfValuesGenerator
{
	static final String LF = System.getProperty("line.separator");
	
	@SuppressWarnings("unchecked")
	public static void computeIdfScores(Mode mode, Dataset dataset)
		throws Exception
	{	
		URL inputUrl = ResourceUtils.resolveLocation(DATASET_DIR + "/" + mode.toString().toLowerCase() + "/STS.input." + dataset.toString() + ".txt");
		List<String> lines = FileUtils.readLines(new File(inputUrl.getPath()));
		
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
	
	private static Collection<Lemma> getLemmas(String fileContents)
		throws Exception
	{
		AnalysisEngineDescription aed = createAggregateDescription(
				createPrimitiveDescription(
						BreakIteratorSegmenter.class),
				createPrimitiveDescription(
						OpenNlpPosTagger.class,
						OpenNlpPosTagger.PARAM_LANGUAGE, "en"),
				createPrimitiveDescription(
						StanfordLemmatizer.class)
				);    	
		AnalysisEngine ae = AnalysisEngineFactory.createAggregate(aed);
		
		JCas jcas = ae.newJCas();
		jcas.setDocumentText(fileContents);
		
		ae.process(jcas);
		
		Collection<Lemma> lemmas = JCasUtil.select(jcas, Lemma.class);
		
		return lemmas;
	}
}
