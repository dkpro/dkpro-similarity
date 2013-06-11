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
package de.tudarmstadt.ukp.similarity.experiments.coling2012;

import static de.tudarmstadt.ukp.similarity.experiments.coling2012.Pipeline.FEATURES_DIR;
import static de.tudarmstadt.ukp.similarity.experiments.coling2012.Pipeline.UTILS_DIR;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitive;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.uimafit.factory.AggregateBuilder;
import org.uimafit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DKProContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Document;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.JaroSecondStringComparator;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.LongestCommonSubsequenceComparator;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.LongestCommonSubsequenceNormComparator;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.LongestCommonSubstringComparator;
import de.tudarmstadt.ukp.similarity.dkpro.annotator.SimilarityScorer;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader;
import de.tudarmstadt.ukp.similarity.dkpro.resource.SimpleTextSimilarityResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lexical.ngrams.CharacterNGramResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lexical.ngrams.WordNGramJaccardResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lexical.string.GreedyStringTilingMeasureResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lsr.ResnikRelatednessResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lsr.aggregate.MCS06AggregateResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.structure.StopwordNGramContainmentMeasureResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.structure.TokenPairOrderingResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.style.FunctionWordFrequenciesMeasureResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.style.MTLDResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.style.TokenRatioResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.vsm.VectorIndexSourceRelatednessResource;
import de.tudarmstadt.ukp.similarity.experiments.coling2012.Pipeline.Dataset;
import de.tudarmstadt.ukp.similarity.experiments.coling2012.util.CharacterNGramIdfValuesGenerator;
import de.tudarmstadt.ukp.similarity.experiments.coling2012.util.ColingUtils;
import de.tudarmstadt.ukp.similarity.experiments.coling2012.util.StopwordFilter;
import de.tudarmstadt.ukp.similarity.experiments.coling2012.util.WordIdfValuesGenerator;
import de.tudarmstadt.ukp.similarity.ml.FeatureConfig;
import de.tudarmstadt.ukp.similarity.ml.io.SimilarityScoreWriter;


/**
 * Pipeline that generates the text similarity features. 
 */
public class FeatureGeneration
{
	public static void generateFeatures(Dataset... datasets)
		throws Exception
	{
		for (Dataset dataset : datasets)
			generateFeatures(dataset);
	}
	
	public static void generateFeatures(Dataset dataset)
		throws Exception
	{
		// Define the features
		List<FeatureConfig> configs = new ArrayList<FeatureConfig>();
		 
		// Prerequisites
		int[] ngrams_n = new int[] { 2, 3, 4, 5 };
		for (int n : ngrams_n)
			CharacterNGramIdfValuesGenerator.computeIdfScores(dataset, n);
		
		WordIdfValuesGenerator.computeIdfScores(dataset);
		
		if (dataset.equals(Dataset.WikipediaRewriteCorpus))
		{
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	SimpleTextSimilarityResource.class,
					    	SimpleTextSimilarityResource.PARAM_MODE, "text",
					    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, LongestCommonSubsequenceComparator.class.getName()),
					Document.class.getName(),
					false,
					"string",
					"LongestCommonSubsequenceComparator"
					));
			
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	StopwordNGramContainmentMeasureResource.class,
					    	StopwordNGramContainmentMeasureResource.PARAM_N, "10",
					    	StopwordNGramContainmentMeasureResource.PARAM_STOPWORD_LIST_LOCATION, "classpath:/stopwords/stopwords-bnc-stamatatos.txt"),
					Token.class.getName(),
					false,
					"structure",
					"StopwordNGramContainmentMeasure_10"
					));
			
			ngrams_n = new int[] { 5 };
			for (int n : ngrams_n)
			{
				configs.add(new FeatureConfig(
						createExternalResourceDescription(
								CharacterNGramResource.class,
								CharacterNGramResource.PARAM_N, new Integer(n).toString(),
								CharacterNGramResource.PARAM_IDF_VALUES_FILE, UTILS_DIR + "/character-ngrams-idf/" + n + "/" + dataset.toString() + ".txt"),
						Document.class.getName(),
						false,
						"n-grams",
						"CharacterNGramMeasure_" + n
						));
			}
		}
		else if (dataset.equals(Dataset.MeterCorpus))
		{
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	GreedyStringTilingMeasureResource.class,
					    	GreedyStringTilingMeasureResource.PARAM_MIN_MATCH_LENGTH, "3"),
					Document.class.getName(),
					false,
					"string",
					"GreedyStringTiling_3"
					));
			
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	StopwordNGramContainmentMeasureResource.class,
					    	StopwordNGramContainmentMeasureResource.PARAM_N, "12",
					    	StopwordNGramContainmentMeasureResource.PARAM_STOPWORD_LIST_LOCATION, "classpath:/stopwords/stopwords-bnc-stamatatos.txt"),
					Token.class.getName(),
					false,
					"structure",
					"StopwordNGramContainmentMeasure_12"
					));
			
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	MTLDResource.class),
					Document.class.getName(),
					false,
					"style",
					"SequentialTTR"
					));
		}
		else if (dataset.equals(Dataset.WebisCrowdParaphraseCorpus))
		{
			// Content
			
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	GreedyStringTilingMeasureResource.class,
					    	GreedyStringTilingMeasureResource.PARAM_MIN_MATCH_LENGTH, "3"),
					Document.class.getName(),
					false,
					"string",
					"GreedyStringTiling_3"
					));
			
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	SimpleTextSimilarityResource.class,
					    	SimpleTextSimilarityResource.PARAM_MODE, "text",
					    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, LongestCommonSubsequenceComparator.class.getName()),
					Document.class.getName(),
					false,
					"string",
					"LongestCommonSubsequenceComparator"
					));
			
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	SimpleTextSimilarityResource.class,
					    	SimpleTextSimilarityResource.PARAM_MODE, "text",
					    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, LongestCommonSubsequenceNormComparator.class.getName()),
					Document.class.getName(),
					false,
					"string",
					"LongestCommonSubsequenceNormComparator"
					));
			
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	SimpleTextSimilarityResource.class,
					    	SimpleTextSimilarityResource.PARAM_MODE, "text",
					    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, LongestCommonSubstringComparator.class.getName()),
					Document.class.getName(),    	
					false,
					"string",
					"LongestCommonSubstringComparator"
					));
			
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	SimpleTextSimilarityResource.class,
					    	SimpleTextSimilarityResource.PARAM_MODE, "text",
					    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, JaroSecondStringComparator.class.getName()),
					Document.class.getName(),    	
					false,
					"string",
					"Jaro"
					));
			
			ngrams_n = new int[] { 6, 14, 15 };
			for (int n : ngrams_n)
			{
				configs.add(new FeatureConfig(
						createExternalResourceDescription(
						    	WordNGramJaccardResource.class,
						    	WordNGramJaccardResource.PARAM_N, new Integer(n).toString()),
						Token.class.getName(),
						false,
						"n-grams",
						"WordNGramJaccardMeasure_" + n
						));	
			}
			
			// Structure
			
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	TokenPairOrderingResource.class),
					Lemma.class.getName() + "/value",
					false,
					"structure",
					"TokenPairOrdering"
					));
			
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	StopwordNGramContainmentMeasureResource.class,
					    	StopwordNGramContainmentMeasureResource.PARAM_N, "6",
					    	StopwordNGramContainmentMeasureResource.PARAM_STOPWORD_LIST_LOCATION, "classpath:/stopwords/stopwords-bnc-stamatatos.txt"),
					Token.class.getName(),
					false,
					"structure",
					"StopwordNGramContainmentMeasure_6"
					));
			
			// Style
			
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	FunctionWordFrequenciesMeasureResource.class,
					    	FunctionWordFrequenciesMeasureResource.PARAM_FUNCTION_WORD_LIST_LOCATION, "classpath:/stopwords/function-words-mosteller-wallace.txt"),
					Document.class.getName(),
					false,
					"style",
					"FunctionWordFrequencies"
					));
			
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	MTLDResource.class),
					Document.class.getName(),
					false,
					"style",
					"SequentialTTR"
					));
			
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	TokenRatioResource.class),
					Document.class.getName(),
					false,
					"style",
					"TokenRatio"
					));
			
			// Content, again
				
			/* TODO: If you plan to use the following measures, make sure that you have the
			 * necessary resources installed. 
			 * Details on obtaining and installing them can be found here:
			 * http://code.google.com/p/dkpro-similarity-asl/wiki/SettingUpTheResources
			 */
			
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	VectorIndexSourceRelatednessResource.class,
					    	VectorIndexSourceRelatednessResource.PARAM_MODEL_LOCATION, DKProContext.getContext().getWorkspace().getAbsolutePath() + "/ESA/VectorIndexes/wordnet_eng_lem_nc_c"),
					Lemma.class.getName() + "/value",
					false,
					"esa",
					"ESA_WordNet"
					));
			
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	VectorIndexSourceRelatednessResource.class,
					    	VectorIndexSourceRelatednessResource.PARAM_MODEL_LOCATION, DKProContext.getContext().getWorkspace().getAbsolutePath() + "/ESA/VectorIndexes/wordnet_eng_lem_nc_c"),
					Lemma.class.getName() + "/value",
					true,
					"esa",
					"ESA_WordNet_stopword-filtered"
					));
			
			// Resnik word similarity measure, aggregated according to Mihalcea et al. (2006)
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	MCS06AggregateResource.class,
					    	MCS06AggregateResource.PARAM_TERM_SIMILARITY_RESOURCE, createExternalResourceDescription(
					    			ResnikRelatednessResource.class,
					    			ResnikRelatednessResource.PARAM_RESOURCE_NAME, "wordnet",
					    			ResnikRelatednessResource.PARAM_RESOURCE_LANGUAGE, "en"
					    			),
					    	MCS06AggregateResource.PARAM_IDF_VALUES_FILE, UTILS_DIR + "/word-idf/" + dataset.toString() + ".txt"),
					Lemma.class.getName() + "/value",
					false,
					"word-sim",
					"MCS06_Resnik_WordNet"
					));
		}

		
		// Run the pipeline		
		for (FeatureConfig config : configs)
		{			
			System.out.println(config.getMeasureName());
			
			File outputFile = new File(FEATURES_DIR + "/" + dataset.toString() + "/" + config.getTargetPath() + "/" + config.getMeasureName() + ".txt");
			
			if (outputFile.exists())
			{
				System.out.println(" - skipped, feature already generated");
			} 
			else
			{			
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
				
				// Stopword Filter (if applicable)
				AnalysisEngineDescription stopw = createPrimitiveDescription(
						StopwordFilter.class,
						StopwordFilter.PARAM_STOPWORD_LIST, "classpath:/stopwords/stopwords_english_punctuation.txt",
						StopwordFilter.PARAM_ANNOTATION_TYPE_NAME, Lemma.class.getName(),
						StopwordFilter.PARAM_STRING_REPRESENTATION_METHOD_NAME, "getValue");
				builder = new AggregateBuilder();
				builder.add(stopw, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
				builder.add(stopw, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
				AnalysisEngine aggr_stopw = builder.createAggregate();
		
				// Similarity Scorer
				AnalysisEngine scorer = createPrimitive(SimilarityScorer.class,
				    SimilarityScorer.PARAM_NAME_VIEW_1, CombinationReader.VIEW_1,
				    SimilarityScorer.PARAM_NAME_VIEW_2, CombinationReader.VIEW_2,
				    SimilarityScorer.PARAM_SEGMENT_FEATURE_PATH, config.getSegmentFeaturePath(),
				    SimilarityScorer.PARAM_TEXT_SIMILARITY_RESOURCE, config.getResource()
				    );
				
				// Output Writer
				AnalysisEngine writer = createPrimitive(SimilarityScoreWriter.class,
					SimilarityScoreWriter.PARAM_OUTPUT_FILE, outputFile.getAbsolutePath(),
					SimilarityScoreWriter.PARAM_OUTPUT_SCORES_ONLY, true);
		
				if (config.filterStopwords())
					SimplePipeline.runPipeline(reader, aggr_seg, aggr_pos, aggr_lem, aggr_stopw, scorer, writer);
				else
					SimplePipeline.runPipeline(reader, aggr_seg, aggr_pos, aggr_lem, scorer, writer);
				
				System.out.println(" - done");
			}
		}
		
		System.out.println("Successful.");
	}
}
