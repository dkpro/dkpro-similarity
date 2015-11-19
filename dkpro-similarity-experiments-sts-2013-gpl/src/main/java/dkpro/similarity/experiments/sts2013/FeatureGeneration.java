package dkpro.similarity.experiments.sts2013;

import static dkpro.similarity.experiments.sts2013.Pipeline.DATASET_DIR;
import static dkpro.similarity.experiments.sts2013.Pipeline.FEATURES_DIR;
import static dkpro.similarity.experiments.sts2013.Pipeline.UTILS_DIR;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.pipeline.SimplePipeline;

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
import dkpro.similarity.algorithms.lexical.uima.string.GreedyStringTilingMeasureResource;
import dkpro.similarity.algorithms.lexsub.uima.TWSISubstituteWrapperResource;
import dkpro.similarity.algorithms.lsr.uima.aggregate.MCS06AggregateResource;
import dkpro.similarity.algorithms.lsr.uima.path.ResnikRelatednessResource;
import dkpro.similarity.algorithms.vsm.uima.VectorIndexSourceRelatednessResource;
import dkpro.similarity.experiments.sts2013.Pipeline.Dataset;
import dkpro.similarity.experiments.sts2013.Pipeline.Mode;
import dkpro.similarity.experiments.sts2013.util.CharacterNGramIdfValuesGenerator;
import dkpro.similarity.experiments.sts2013.util.StopwordFilter;
import dkpro.similarity.experiments.sts2013.util.WordIdfValuesGenerator;
import dkpro.similarity.ml.FeatureConfig;
import dkpro.similarity.ml.io.SimilarityScoreWriter;
import dkpro.similarity.uima.annotator.SimilarityScorer;
import dkpro.similarity.uima.io.CombinationReader;
import dkpro.similarity.uima.io.CombinationReader.CombinationStrategy;
import dkpro.similarity.uima.io.SemEvalCorpusReader;
import dkpro.similarity.uima.resource.SimpleTextSimilarityResource;


/**
 * Pipline for generating the text similarity features.
 */
public class FeatureGeneration
{
	public static void generateFeatures(Dataset dataset, Mode mode)
		throws Exception
	{
		// Define the features
		List<FeatureConfig> configs = new ArrayList<FeatureConfig>();
		 
		// Prerequisites
		int[] ngrams_n = new int[] { 2, 3, 4 };
		for (int n : ngrams_n) {
            CharacterNGramIdfValuesGenerator.computeIdfScores(mode, dataset, n);
        }
		
		WordIdfValuesGenerator.computeIdfScores(mode, dataset);
		
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
		
		ngrams_n = new int[] { 2, 3, 4 };
		for (int n : ngrams_n)
		{
			configs.add(new FeatureConfig(
					createExternalResourceDescription(
							CharacterNGramResource.class,
							CharacterNGramResource.PARAM_N, new Integer(n).toString(),
							CharacterNGramResource.PARAM_IDF_VALUES_FILE, UTILS_DIR + "/character-ngrams-idf/" + mode.toString().toLowerCase() + "/" + n + "/" + dataset.toString() + ".txt"),
					Document.class.getName(),
					false,
					"n-grams",
					"CharacterNGramMeasure_" + n
					));
		}
		
		ngrams_n = new int[] { 1, 2 };
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
		
		ngrams_n = new int[] { 1, 3, 4 };
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
		
		ngrams_n = new int[] { 2, 4 };
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
				    	MCS06AggregateResource.PARAM_IDF_VALUES_FILE, UTILS_DIR + "/word-idf/" + mode.toString().toLowerCase() + "/" + dataset.toString() + ".txt"),
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
						    	MCS06AggregateResource.PARAM_IDF_VALUES_FILE, UTILS_DIR + "/word-idf/" + mode.toString().toLowerCase() + "/" + dataset.toString() + ".txt")),
				"word-sim",
				"TWSI_MCS06_Resnik_WordNet"
				));
		
		// Bing SMT wrapper for 
		// Resnik word similarity measure, aggregated according to Mihalcea et al. (2006)
		// In our original system, we used MOSES for machine translation.
//		configs.add(new FeatureConfig(
//				createExternalResourceDescription(
//						BingSMTWrapperResource.class,
//						BingSMTWrapperResource.PARAM_TEXT_SIMILARITY_RESOURCE, createExternalResourceDescription(
//						    	MCS06AggregateResource.class,
//						    	MCS06AggregateResource.PARAM_TERM_SIMILARITY_RESOURCE, createExternalResourceDescription(
//						    			ResnikRelatednessResource.class,
//						    			ResnikRelatednessResource.PARAM_RESOURCE_NAME, "wordnet",
//						    			ResnikRelatednessResource.PARAM_RESOURCE_LANGUAGE, "en"
//						    			),
//						    	MCS06AggregateResource.PARAM_IDF_VALUES_FILE, UTILS_DIR + "/word-idf/" + mode.toString().toLowerCase() + "/" + dataset.toString() + ".txt"
//						    	),
//						BingSMTWrapperResource.PARAM_ORIGINAL_LANGUAGE, Language.EN.toString(),
//						BingSMTWrapperResource.PARAM_BRIDGE_LANGUAGE, Language.ES.toString()),
//				"word-sim",
//				"BingSMT_MCS06_Resnik_WordNet"
//				));
				
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

		
		// Run the pipeline		
		for (FeatureConfig config : configs)
		{			
			System.out.println(config.getMeasureName());
			
			File outputFile = new File(FEATURES_DIR + "/" + mode.toString().toLowerCase() + "/" + dataset.toString() + "/" + config.getTargetPath() + "/" + config.getMeasureName() + ".txt");
			
			if (outputFile.exists())
			{
				System.out.println(" - skipped, feature already generated");
			} 
			else
			{			
				CollectionReader reader = createReader(SemEvalCorpusReader.class,
						SemEvalCorpusReader.PARAM_INPUT_FILE, DATASET_DIR + "/" + mode.toString().toLowerCase() + "/STS.input." + dataset.toString() + ".txt",
						SemEvalCorpusReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
		
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
//						StanfordLemmatizer.class);
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
	
	public static void combineFeatureSets(Mode mode, Dataset target, Dataset... sources)
			throws IOException
	{	
		String outputFolderName = target.toString();
		
		System.out.println("Combining feature sets");
		
		// Check if target directory exists. If so, delete it.
		File targetDir = new File(FEATURES_DIR + "/" + mode.toString().toLowerCase() + "/" + target.toString());
		if (targetDir.exists())
		{
			System.out.println(" - cleaned target directory");
			FileUtils.deleteDirectory(targetDir);
		}
		
		String featurePathOfFirstSet = FEATURES_DIR + "/" + mode.toString().toLowerCase() + "/" + sources[0].toString();
		
		Collection<File> features = FileUtils.listFiles(new File(featurePathOfFirstSet), new String[] { "txt" }, true);
		
		for (File feature : features)
		{
			if (!feature.isDirectory())
			{
				// Check that feature exists for all 
				boolean shared = true;
				
				for (int i = 1; i < sources.length; i++)
				{
					if (!new File(feature.getAbsolutePath().replace(sources[0].toString(), sources[i].toString())).exists()) {
                        shared = false;
                    }
				}
				
				if (shared)
				{					
					System.out.println(" - processing " + feature.getName());
					
					String concat = FileUtils.readFileToString(feature);
					
					for (int i = 1; i < sources.length; i++)
					{
						File nextFile = new File(feature.getAbsolutePath().replaceAll(sources[0].toString(), sources[i].toString()));
						
						concat += FileUtils.readFileToString(nextFile);
					}
					
					File outputFile = new File(feature.getAbsolutePath().replace(sources[0].toString(), outputFolderName));
					
					FileUtils.writeStringToFile(outputFile, concat);
				}
			}
		}
		
		System.out.println(" - done");
	}
}
