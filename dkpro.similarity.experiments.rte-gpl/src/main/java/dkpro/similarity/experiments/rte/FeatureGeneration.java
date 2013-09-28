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
package dkpro.similarity.experiments.rte;

import static dkpro.similarity.experiments.rte.Pipeline.DATASET_DIR;
import static dkpro.similarity.experiments.rte.Pipeline.FEATURES_DIR;
import static dkpro.similarity.experiments.rte.Pipeline.UTILS_DIR;
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

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Document;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.gate.GateLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import dkpro.similarity.algorithms.lexical.string.LongestCommonSubsequenceComparator;
import dkpro.similarity.algorithms.lexical.string.LongestCommonSubsequenceNormComparator;
import dkpro.similarity.algorithms.lexical.string.LongestCommonSubstringComparator;
import dkpro.similarity.algorithms.lexical.uima.ngrams.CharacterNGramResource;
import dkpro.similarity.algorithms.lexical.uima.ngrams.WordNGramContainmentResource;
import dkpro.similarity.algorithms.lexical.uima.ngrams.WordNGramJaccardResource;
import dkpro.similarity.algorithms.lexical.uima.string.CosineSimilarityResource;
import dkpro.similarity.algorithms.lexical.uima.string.GreedyStringTilingMeasureResource;
import dkpro.similarity.algorithms.lexsub.uima.TWSISubstituteWrapperResource;
import dkpro.similarity.algorithms.lsr.uima.aggregate.MCS06AggregateResource;
import dkpro.similarity.algorithms.lsr.uima.path.ResnikRelatednessResource;
import dkpro.similarity.algorithms.sspace.uima.LatentSemanticAnalysisResource;
import dkpro.similarity.algorithms.structure.uima.PosNGramContainmentResource;
import dkpro.similarity.algorithms.structure.uima.PosNGramJaccardResource;
import dkpro.similarity.algorithms.structure.uima.StopwordNGramContainmentMeasureResource;
import dkpro.similarity.algorithms.structure.uima.TokenPairDistanceResource;
import dkpro.similarity.algorithms.structure.uima.TokenPairOrderingResource;
import dkpro.similarity.algorithms.style.uima.AvgCharactersPerTokenResource;
import dkpro.similarity.algorithms.style.uima.AvgTokensPerSentenceResource;
import dkpro.similarity.algorithms.style.uima.FunctionWordFrequenciesMeasureResource;
import dkpro.similarity.algorithms.style.uima.MTLDResource;
import dkpro.similarity.algorithms.style.uima.SentenceRatioResource;
import dkpro.similarity.algorithms.style.uima.TokenRatioResource;
import dkpro.similarity.algorithms.style.uima.TypeTokenRatioResource;
import dkpro.similarity.algorithms.vsm.uima.VectorIndexSourceRelatednessResource;
import dkpro.similarity.experiments.rte.Pipeline.Dataset;
import dkpro.similarity.experiments.rte.util.CharacterNGramIdfValuesGenerator;
import dkpro.similarity.experiments.rte.util.ConvertToPlainText;
import dkpro.similarity.experiments.rte.util.RteUtil;
import dkpro.similarity.experiments.rte.util.StopwordFilter;
import dkpro.similarity.experiments.rte.util.WordIdfValuesGenerator;
import dkpro.similarity.ml.FeatureConfig;
import dkpro.similarity.ml.io.SimilarityScoreWriter;
import dkpro.similarity.uima.annotator.SimilarityScorer;
import dkpro.similarity.uima.io.CombinationReader;
import dkpro.similarity.uima.io.CombinationReader.CombinationStrategy;
import dkpro.similarity.uima.io.RTECorpusReader;
import dkpro.similarity.uima.resource.SimpleTextSimilarityResource;
//import de.tudarmstadt.ukp.similarity.experiments.semeval2013.util.CharacterNGramIdfValuesGenerator;
//import de.tudarmstadt.ukp.similarity.experiments.semeval2013.util.WordIdfValuesGenerator;


/**
 * Pipline for the text similarity feature generation.
 */
public class FeatureGeneration
{
	public static void generateFeatures(Dataset dataset)
		throws Exception
	{
		// Define the features
		List<FeatureConfig> configs = new ArrayList<FeatureConfig>();
		 
		// ** PREREQUISITES **
		
		// Convert texts to plain text (may be omitted depending on the nature of the dataset)
		ConvertToPlainText.convert(dataset);
		
		// Generate character n-gram idf values
		int[] ngrams_n = new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		for (int n : ngrams_n) {
            CharacterNGramIdfValuesGenerator.computeIdfScores(dataset, n);
        }
		
		// Generate word idf values
		WordIdfValuesGenerator.computeIdfScores(dataset);
		
		// ** FEATURES **
		
		// String features
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
				    	CosineSimilarityResource.class),
				Lemma.class.getName() + "/value",
				false,
				"string",
				"CosineSimilarity"
				));
		
		// n-gram models
		ngrams_n = new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10 };
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
		
		ngrams_n = new int[] { 1, 2, 3, 4, 5 };
		for (int n : ngrams_n)
		{
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	WordNGramContainmentResource.class,
					    	WordNGramContainmentResource.PARAM_N, new Integer(n).toString()),
					Token.class.getName(),
					false,
					"n-grams",
					"WordNGramContainmentMeasure_" + n
					));
		}
		
		ngrams_n = new int[] { 1, 2, 3, 4, 5 };
		for (int n : ngrams_n)
		{
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	WordNGramContainmentResource.class,
					    	WordNGramContainmentResource.PARAM_N, new Integer(n).toString()),
					Token.class.getName(),
					true,
					"n-grams",
					"WordNGramContainmentMeasure_" + n + "_stopword-filtered"
					));
		}
		
		ngrams_n = new int[] { 1, 2, 3, 4, 5 };
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
		
		ngrams_n = new int[] { 1, 2, 3, 4, 5 };
		for (int n : ngrams_n)
		{
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
					    	WordNGramJaccardResource.class,
					    	WordNGramJaccardResource.PARAM_N, new Integer(n).toString()),
					Token.class.getName(),
					true,
					"n-grams",
					"WordNGramJaccardMeasure_" + n + "_stopword-filtered"
					));			
		}
		
		/* TODO: If you plan to use the following measures, make sure that you have the
		 * necessary resources installed. 
		 * Details on obtaining and installing them can be found here:
		 * http://code.google.com/p/dkpro-similarity-asl/wiki/SettingUpTheResources
		 */
		
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
		
		// Lexical Substitution System wrapper for 
		// Resnik word similarity measure, aggregated according to Mihalcea et al. (2006)
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
						TWSISubstituteWrapperResource.class,
						TWSISubstituteWrapperResource.PARAM_TEXT_SIMILARITY_RESOURCE, createExternalResourceDescription(
						    	MCS06AggregateResource.class,
						    	MCS06AggregateResource.PARAM_TERM_SIMILARITY_RESOURCE, createExternalResourceDescription(
						    			ResnikRelatednessResource.class,
						    			ResnikRelatednessResource.PARAM_RESOURCE_NAME, "wordnet",
						    			ResnikRelatednessResource.PARAM_RESOURCE_LANGUAGE, "en"
						    			),
						    	MCS06AggregateResource.PARAM_IDF_VALUES_FILE, UTILS_DIR + "/word-idf/" + dataset.toString() + ".txt")),
				"word-sim",
				"TWSI_MCS06_Resnik_WordNet"
				));
				
		// Explicit Semantic Analysis
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
				    	VectorIndexSourceRelatednessResource.class,
				    	VectorIndexSourceRelatednessResource.PARAM_MODEL_LOCATION, DkproContext.getContext().getWorkspace().getAbsolutePath() + "/ESA/VectorIndexes/wordnet_eng_lem_nc_c"),
				Lemma.class.getName() + "/value",
				false,
				"esa",
				"ESA_WordNet"
				));
		
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
				    	VectorIndexSourceRelatednessResource.class,
				    	VectorIndexSourceRelatednessResource.PARAM_MODEL_LOCATION, DkproContext.getContext().getWorkspace().getAbsolutePath() + "/ESA/VectorIndexes/wiktionary_en"),
				Lemma.class.getName() + "/value",
				false,
				"esa",
				"ESA_Wiktionary"
				));
		
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
				    	VectorIndexSourceRelatednessResource.class,
				    	VectorIndexSourceRelatednessResource.PARAM_MODEL_LOCATION, DkproContext.getContext().getWorkspace().getAbsolutePath() + "/ESA/VectorIndexes/wp_eng_lem_nc_c"),
				Lemma.class.getName() + "/value",
				false,
				"esa",
				"ESA_Wikipedia"
				));
		
		// LSA
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
				    	LatentSemanticAnalysisResource.class,
				    	LatentSemanticAnalysisResource.PARAM_INPUT_DIR, UTILS_DIR + "/plaintexts/" + dataset.toString()),
				Token.class.getName(),
				false,
				"lsa",
				"LSA"
				));
		
		// ** Structure **
		
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
						TokenPairDistanceResource.class),
				Token.class.getName(),
				false,
				"structure",
				"TokenPairDistanceMeasure"
				));
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
						TokenPairOrderingResource.class),
				Token.class.getName(),
				false,
				"structure",
				"TokenPairOrderingMeasure"
				));
		
		for (int n = 2; n <= 7; n++) {
            configs.add(new FeatureConfig(
					createExternalResourceDescription(
							StopwordNGramContainmentMeasureResource.class,
							StopwordNGramContainmentMeasureResource.PARAM_N, new Integer(n).toString(),
							StopwordNGramContainmentMeasureResource.PARAM_STOPWORD_LIST_LOCATION, "classpath:/stopwords/stopwords_english_punctuation.txt"),
					Token.class.getName(),
					false,
					"structure",
					"StopwordNGramContainmentMeasure_" + n + "_english-punctuation"
					));
        }
		
		for (int n = 2; n <= 7; n++) {
            configs.add(new FeatureConfig(
					createExternalResourceDescription(
							StopwordNGramContainmentMeasureResource.class,
							StopwordNGramContainmentMeasureResource.PARAM_N, new Integer(n).toString(),
							StopwordNGramContainmentMeasureResource.PARAM_STOPWORD_LIST_LOCATION, "classpath:/stopwords/function-words-mosteller-wallace.txt"),
					Token.class.getName(),
					false,
					"structure",
					"StopwordNGramContainmentMeasure_" + n + "_mosteller-wallace"
					));
        }
		
		for (int n = 2; n <= 7; n++) {
            configs.add(new FeatureConfig(
					createExternalResourceDescription(
							StopwordNGramContainmentMeasureResource.class,
							StopwordNGramContainmentMeasureResource.PARAM_N, new Integer(n).toString(),
							StopwordNGramContainmentMeasureResource.PARAM_STOPWORD_LIST_LOCATION, "classpath:/stopwords/stopwords-bnc-stamatatos.txt"),
					Token.class.getName(),
					false,
					"structure",
					"StopwordNGramContainmentMeasure_" + n + "_stamatatos"
					));
        }
		
		for (int n = 1; n <= 7; n++)
		{
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
							PosNGramJaccardResource.class,
							PosNGramJaccardResource.PARAM_N, new Integer(n).toString()),
					POS.class.getName(),
					false,
					"structure",
					"PosNGramJaccardMeasure_" + n
					));
		}
			
		for (int n = 1; n <= 7; n++)
		{
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
							PosNGramContainmentResource.class,
							PosNGramContainmentResource.PARAM_N, new Integer(n).toString()),
					POS.class.getName(),
					false,
					"structure",
					"PosNGramContainmentMeasure_" + n
					));
		}
		
		// ** Style **

		configs.add(new FeatureConfig(
				createExternalResourceDescription(
						MTLDResource.class),
				null,
				false,
				"style",
				"MTLDComparator"
				));
		
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
						TypeTokenRatioResource.class),
				null,
				false,
				"style",
				"TypeTokenRatioComparator"
				));
		
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
						AvgCharactersPerTokenResource.class),
				null,
				false,
				"style",
				"AvgCharactersPerTokenComparator"
				));
		
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
						AvgTokensPerSentenceResource.class),
				null,
				false,
				"style",
				"AvgTokensPerSentenceComparator"
				));
		
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
						SentenceRatioResource.class),
				null,
				false,
				"style",
				"SentenceRatioComparator"
				));
		
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
						TokenRatioResource.class),
				null,
				false,
				"style",
				"TokenRatioComparator"
				));
		
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
						FunctionWordFrequenciesMeasureResource.class,
						FunctionWordFrequenciesMeasureResource.PARAM_FUNCTION_WORD_LIST_LOCATION, "classpath:/stopwords/stopwords_english_punctuation.txt"),
				Token.class.getName(),
				false,
				"style",
				"FunctionWordFrequenciesMeasure_english-punctuation"
				));
		
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
						FunctionWordFrequenciesMeasureResource.class,
						FunctionWordFrequenciesMeasureResource.PARAM_FUNCTION_WORD_LIST_LOCATION, "classpath:/stopwords/function-words-mosteller-wallace.txt"),
				Token.class.getName(),
				false,
				"style",
				"FunctionWordFrequenciesMeasure_mosteller-wallace"
				));
		
		configs.add(new FeatureConfig(
				createExternalResourceDescription(
						FunctionWordFrequenciesMeasureResource.class,
						FunctionWordFrequenciesMeasureResource.PARAM_FUNCTION_WORD_LIST_LOCATION, "classpath:/stopwords/stopwords-bnc-stamatatos.txt"),
				Token.class.getName(),
				false,
				"style",
				"FunctionWordFrequenciesMeasure_stamatatos"
				));

		
		// Run the pipeline		
		for (FeatureConfig config : configs)
		{			
			System.out.println("[" + dataset.toString() + "]" + config.getMeasureName());
			
			File outputFile = new File(FEATURES_DIR + "/" + RteUtil.getCommonDatasetName(dataset) + "/" + config.getTargetPath() + "/" + config.getMeasureName() + ".txt");
			
			if (outputFile.exists())
			{
				System.out.println(" - skipped, feature already generated");
			} 
			else
			{			
				CollectionReader reader = createReader(
						 RTECorpusReader.class,
			             RTECorpusReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY,
			             RTECorpusReader.PARAM_INPUT_FILE, RteUtil.getInputFilePathForDataset(DATASET_DIR, dataset));
		
				// Tokenization
				AnalysisEngineDescription seg = createEngineDescription(
						BreakIteratorSegmenter.class);
				AggregateBuilder builder = new AggregateBuilder();
				builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
				builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
				AnalysisEngine aggr_seg = builder.createAggregate();
				
				// POS Tagging
				AnalysisEngineDescription pos = createEngineDescription(
						OpenNlpPosTagger.class,
						OpenNlpPosTagger.PARAM_LANGUAGE, "en");		
				builder = new AggregateBuilder();
				builder.add(pos, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
				builder.add(pos, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
				AnalysisEngine aggr_pos = builder.createAggregate();
				
				// Lemmatization
				AnalysisEngineDescription lem = createEngineDescription(
						GateLemmatizer.class);
				builder = new AggregateBuilder();
				builder.add(lem, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
				builder.add(lem, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
				AnalysisEngine aggr_lem = builder.createAggregate();
				
				// Stopword Filter (if applicable)
				AnalysisEngineDescription stopw = createEngineDescription(
						StopwordFilter.class,
						StopwordFilter.PARAM_STOPWORD_LIST, "classpath:/stopwords/stopwords_english_punctuation.txt",
						StopwordFilter.PARAM_ANNOTATION_TYPE_NAME, Lemma.class.getName(),
						StopwordFilter.PARAM_STRING_REPRESENTATION_METHOD_NAME, "getValue");
				builder = new AggregateBuilder();
				builder.add(stopw, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
				builder.add(stopw, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
				AnalysisEngine aggr_stopw = builder.createAggregate();
		
				// Similarity Scorer
				AnalysisEngine scorer = createEngine(SimilarityScorer.class,
				    SimilarityScorer.PARAM_NAME_VIEW_1, CombinationReader.VIEW_1,
				    SimilarityScorer.PARAM_NAME_VIEW_2, CombinationReader.VIEW_2,
				    SimilarityScorer.PARAM_SEGMENT_FEATURE_PATH, config.getSegmentFeaturePath(),
				    SimilarityScorer.PARAM_TEXT_SIMILARITY_RESOURCE, config.getResource()
				    );
				
				// Output Writer
				AnalysisEngine writer = createEngine(SimilarityScoreWriter.class,
					SimilarityScoreWriter.PARAM_OUTPUT_FILE, outputFile.getAbsolutePath(),
					SimilarityScoreWriter.PARAM_OUTPUT_SCORES_ONLY, true);
		
				if (config.filterStopwords()) {
                    SimplePipeline.runPipeline(reader, aggr_seg, aggr_pos, aggr_lem, aggr_stopw, scorer, writer);
                }
                else {
                    SimplePipeline.runPipeline(reader, aggr_seg, aggr_pos, aggr_lem, scorer, writer);
                }
				
				System.out.println(" - done");
			}
		}
		
		System.out.println("Successful.");
	}
	
//	@SuppressWarnings("unchecked")
//	public static void combineFeatureSets(Mode mode, Dataset target, Dataset... sources)
//			throws IOException
//	{	
//		String outputFolderName = target.toString();
//		
//		System.out.println("Combining feature sets");
//		
//		// Check if target directory exists. If so, delete it.
//		File targetDir = new File(FEATURES_DIR + "/" + mode.toString().toLowerCase() + "/" + target.toString());
//		if (targetDir.exists())
//		{
//			System.out.println(" - cleaned target directory");
//			FileUtils.deleteDirectory(targetDir);
//		}
//		
//		String featurePathOfFirstSet = FEATURES_DIR + "/" + mode.toString().toLowerCase() + "/" + sources[0].toString();
//		
//		Collection<File> features = FileUtils.listFiles(new File(featurePathOfFirstSet), new String[] { "txt" }, true);
//		
//		for (File feature : features)
//		{
//			if (!feature.isDirectory())
//			{
//				// Check that feature exists for all 
//				boolean shared = true;
//				
//				for (int i = 1; i < sources.length; i++)
//				{
//					if (!new File(feature.getAbsolutePath().replace(sources[0].toString(), sources[i].toString())).exists())
//						shared = false;
//				}
//				
//				if (shared)
//				{					
//					System.out.println(" - processing " + feature.getName());
//					
//					String concat = FileUtils.readFileToString(feature);
//					
//					for (int i = 1; i < sources.length; i++)
//					{
//						File nextFile = new File(feature.getAbsolutePath().replaceAll(sources[0].toString(), sources[i].toString()));
//						
//						concat += FileUtils.readFileToString(nextFile);
//					}
//					
//					File outputFile = new File(feature.getAbsolutePath().replace(sources[0].toString(), outputFolderName));
//					
//					FileUtils.writeStringToFile(outputFile, concat);
//				}
//			}
//		}
//		
//		System.out.println(" - done");
//	}
}
