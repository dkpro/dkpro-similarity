/**
 * Copyright 2012-2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package dkpro.similarity.example.ml.mm09;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Document;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import dkpro.similarity.algorithms.lexical.string.JaroSecondStringComparator;
import dkpro.similarity.algorithms.lexical.string.JaroWinklerSecondStringComparator;
import dkpro.similarity.algorithms.lexical.string.LevenshteinComparator;
import dkpro.similarity.algorithms.lexical.string.LongestCommonSubsequenceComparator;
import dkpro.similarity.algorithms.lexical.string.LongestCommonSubsequenceNormComparator;
import dkpro.similarity.algorithms.lexical.string.LongestCommonSubstringComparator;
import dkpro.similarity.algorithms.lexical.string.MongeElkanSecondStringComparator;
import dkpro.similarity.algorithms.lexical.uima.ngrams.WordNGramContainmentResource;
import dkpro.similarity.algorithms.lexical.uima.ngrams.WordNGramJaccardResource;
import dkpro.similarity.algorithms.structure.uima.StopwordNGramContainmentMeasureResource;
import dkpro.similarity.algorithms.style.uima.FunctionWordFrequenciesMeasureResource;
import dkpro.similarity.algorithms.vsm.uima.VectorIndexSourceRelatednessResource;
import dkpro.similarity.ml.FeatureConfig;
import dkpro.similarity.ml.io.SimilarityScoreWriter;
import dkpro.similarity.uima.annotator.SimilarityScorer;
import dkpro.similarity.uima.io.CombinationReader;
import dkpro.similarity.uima.io.CombinationReader.CombinationStrategy;
import dkpro.similarity.uima.io.SemEvalCorpusReader;
import dkpro.similarity.uima.resource.SimpleTextSimilarityResource;


public class FeatureGenerationTrain
{
	private static final String OUTPUT_FEATURE_DIR = "src/main/resources/semeval-train-all-combined-features"; 
	
	public static void main(String[] args)
		throws Exception
	{
		// Define the features
		List<FeatureConfig> configs = new ArrayList<FeatureConfig>();
		
		// String features
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
				    	SimpleTextSimilarityResource.class,
				    	SimpleTextSimilarityResource.PARAM_MODE, "text",
				    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, JaroSecondStringComparator.class.getName()),
				Document.class.getName(),
				false,
				"content/string",
				"Jaro"
				));
		
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
				    	SimpleTextSimilarityResource.class,
				    	SimpleTextSimilarityResource.PARAM_MODE, "text",
				    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, JaroWinklerSecondStringComparator.class.getName()),
				Document.class.getName(),
				false,
				"content/string",
				"JaroWinkler"
				));
		
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
				    	SimpleTextSimilarityResource.class,
				    	SimpleTextSimilarityResource.PARAM_MODE, "text",
				    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, MongeElkanSecondStringComparator.class.getName()),
				Document.class.getName(),
				false,
				"content/string",
				"MongeElkan"
				));
		
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
				    	SimpleTextSimilarityResource.class,
				    	SimpleTextSimilarityResource.PARAM_MODE, "text",
				    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, LevenshteinComparator.class.getName()),
				Document.class.getName(),
				false,
				"content/string",
				"Levenshtein"
				));
		
//		configs.add(new FeatureConfig(
//				createExternalResourceDescription(
//				    	GreedyStringTilingMeasureResource.class,
//				    	GreedyStringTilingMeasureResource.PARAM_MIN_MATCH_LENGTH, "3"),
//				Document.class.getName(),
//				false,
//				"content/string",
//				"GreedyStringTiling_3"
//				));
		
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
				    	SimpleTextSimilarityResource.class,
				    	SimpleTextSimilarityResource.PARAM_MODE, "text",
				    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, LongestCommonSubsequenceComparator.class.getName()),
				Document.class.getName(),
				false,
				"content/string",
				"LongestCommonSubsequenceComparator"
				));
		
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
				    	SimpleTextSimilarityResource.class,
				    	SimpleTextSimilarityResource.PARAM_MODE, "text",
				    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, LongestCommonSubsequenceNormComparator.class.getName()),
				Document.class.getName(),
				false,
				"content/string",
				"LongestCommonSubsequenceNormComparator"
				));

		configs.add(new FeatureConfig(
				createExternalResourceDescription(
				    	SimpleTextSimilarityResource.class,
				    	SimpleTextSimilarityResource.PARAM_MODE, "text",
				    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, LongestCommonSubstringComparator.class.getName()),
				Document.class.getName(),    	
				false,
				"content/string",
				"LongestCommonSubstringComparator"
				));

		// N-Grams
		for (int i = 1; i <= 5; i++)
		{
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	WordNGramContainmentResource.class,
					    	WordNGramContainmentResource.PARAM_N, new Integer(i).toString()),
					Token.class.getName(),
					false,
					"content/n-grams",
					"WordNGramContainmentMeasure_" + i
					));
			
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	WordNGramJaccardResource.class,
					    	WordNGramJaccardResource.PARAM_N, new Integer(i).toString()),
					Token.class.getName(),
					false,
					"content/n-grams",
					"WordNGramJaccardMeasure_" + i
					));
		}
		
		// ESA
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
				    	VectorIndexSourceRelatednessResource.class,
				    	VectorIndexSourceRelatednessResource.PARAM_MODEL_LOCATION, "/home/danielb/Projekte/DKPro/Resources/ESA/VectorIndexes/wp_eng_lem_nc_c"),
				Lemma.class.getName() + "/value",
				false,
				"content/esa",
				"ESA_WP"
				));
		
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
				    	VectorIndexSourceRelatednessResource.class,
				    	VectorIndexSourceRelatednessResource.PARAM_MODEL_LOCATION, "/home/danielb/Projekte/DKPro/Resources/ESA/VectorIndexes/wiktionary_en"),
				Lemma.class.getName() + "/value",
				false,
				"content/esa",
				"ESA_WK"
				));
		
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
				    	VectorIndexSourceRelatednessResource.class,
				    	VectorIndexSourceRelatednessResource.PARAM_MODEL_LOCATION, "/home/danielb/Projekte/DKPro/Resources/ESA/VectorIndexes/wordnet_eng_lem_nc_c"),
				Lemma.class.getName() + "/value",
				false,
				"content/esa",
				"ESA_WN"
				));
		
		// Structure
		
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
				    	StopwordNGramContainmentMeasureResource.class,
				    	StopwordNGramContainmentMeasureResource.PARAM_N, "3",
				    	StopwordNGramContainmentMeasureResource.PARAM_STOPWORD_LIST_LOCATION, "/home/danielb/Projekte/Similarity/workspace/de.tudarmstadt.ukp.similarity-asl/de.tudarmstadt.ukp.similarity.dkpro.data-asl/src/main/resources/stopwords/stopwords-bnc-stamatatos.txt"),
				Lemma.class.getName() + "/value",
				false,
				"structure",
				"StopwordNGramContainmentMeasure_3_stamatatos"
				));
		
		// Style
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
				    	FunctionWordFrequenciesMeasureResource.class,
				    	FunctionWordFrequenciesMeasureResource.PARAM_FUNCTION_WORD_LIST_LOCATION, "/home/danielb/Projekte/Similarity/workspace/de.tudarmstadt.ukp.similarity-asl/de.tudarmstadt.ukp.similarity.dkpro.data-asl/src/main/resources/stopwords/function-words-mosteller-wallace.txt"),
				Lemma.class.getName() + "/value",
				false,
				"style",
				"FunctionWordFrequenciesMeasure"
				));
		
		
		
		// Run the pipeline		
		for (FeatureConfig config : configs)
		{			
			if (new File(OUTPUT_FEATURE_DIR + "/" + config.getTargetPath() + "/" + config.getMeasureName() + ".txt").exists())
			{
				System.out.println("Skipping: " + config.getMeasureName());
			} else {			
				CollectionReader reader = createReader(SemEvalCorpusReader.class,
						SemEvalCorpusReader.PARAM_INPUT_FILE, "classpath:/datasets/semeval/train/STS.input.ALLcombined.txt",
						SemEvalCorpusReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
		
				AnalysisEngineDescription seg = createEngineDescription(
						BreakIteratorSegmenter.class);
				
				AggregateBuilder builder = new AggregateBuilder();
				builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
				builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
				AnalysisEngine aggr_seg = builder.createAggregate();
				
//				AnalysisEngineDescription tt = createPrimitiveDescription(
//						TreeTaggerPosLemmaTT4J.class,
//						TreeTaggerPosLemmaTT4J.PARAM_LANGUAGE_CODE, "en");		
//				builder = new AggregateBuilder();
//				builder.add(tt, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
//				builder.add(tt, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
//				AnalysisEngine aggr_tt = builder.createAggregate();
				
	//			AnalysisEngineDescription stopw = createPrimitiveDescription(
	//					StopwordFilter.class,
	//					StopwordFilter.PARAM_STOPWORD_LIST, new File("src/main/resources/stopwords_english_punctuation.txt").getAbsolutePath(),
	//					StopwordFilter.PARAM_ANNOTATION_TYPE_NAME, Lemma.class.getName(),
	//					StopwordFilter.PARAM_STRING_REPRESENTATION_METHOD_NAME, "getValue");
	//			builder = new AggregateBuilder();
	//			builder.add(stopw, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
	//			builder.add(stopw, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
	//			AnalysisEngineDescription aggr_stopw = builder.createAggregateDescription();
		
				AnalysisEngine scorer = createEngine(SimilarityScorer.class,
				    SimilarityScorer.PARAM_NAME_VIEW_1, CombinationReader.VIEW_1,
				    SimilarityScorer.PARAM_NAME_VIEW_2, CombinationReader.VIEW_2,
				    SimilarityScorer.PARAM_SEGMENT_FEATURE_PATH, config.getSegmentFeaturePath(),
				    SimilarityScorer.PARAM_TEXT_SIMILARITY_RESOURCE, config.getResource()
				    );
				
				AnalysisEngine writer = createEngine(SimilarityScoreWriter.class,
					SimilarityScoreWriter.PARAM_OUTPUT_FILE, OUTPUT_FEATURE_DIR + "/" + config.getTargetPath() + "/" + config.getMeasureName() + ".txt",
					SimilarityScoreWriter.PARAM_OUTPUT_SCORES_ONLY, true);
		
				SimplePipeline.runPipeline(reader, aggr_seg, scorer, writer);
			}
		}
		
		// Read the output and print to the console
		System.out.println("The features were written to " + OUTPUT_FEATURE_DIR);
	}
}
