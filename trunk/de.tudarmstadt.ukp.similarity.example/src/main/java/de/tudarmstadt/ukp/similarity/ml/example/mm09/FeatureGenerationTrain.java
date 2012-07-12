package de.tudarmstadt.ukp.similarity.ml.example.mm09;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitive;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.uimafit.factory.AggregateBuilder;
import org.uimafit.pipeline.SimplePipeline;

import com.sleepycat.je.utilint.LongStat;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Document;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerPosLemmaTT4J;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.ngrams.WordNGramContainmentMeasure;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.ngrams.WordNGramJaccardMeasure;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.GreedyStringTiling;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.JaroSecondStringComparator;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.JaroWinklerSecondStringComparator;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.LevenshteinComparator;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.LongestCommonSubsequenceComparator;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.LongestCommonSubsequenceNormComparator;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.LongestCommonSubstringComparator;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.MongeElkanSecondStringComparator;
import de.tudarmstadt.ukp.similarity.dkpro.annotator.SimilarityScorer;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.PlainTextCombinationReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.SemEvalCorpusReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.ShortAnswerGradingReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader.CombinationStrategy;
import de.tudarmstadt.ukp.similarity.dkpro.resource.SimpleTextSimilarityResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lexical.ngrams.WordNGramContainmentResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lexical.ngrams.WordNGramJaccardResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.structure.StopwordNGramContainmentMeasureResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.style.FunctionWordFrequenciesMeasureResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.vsm.VectorIndexSourceRelatednessResource;
import de.tudarmstadt.ukp.similarity.ml.FeatureConfig;
import de.tudarmstadt.ukp.similarity.ml.io.SimilarityScoreWriter;


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
				CollectionReader reader = createCollectionReader(SemEvalCorpusReader.class,
						SemEvalCorpusReader.PARAM_INPUT_FILE, "classpath:/datasets/semeval/train/STS.input.ALLcombined.txt",
						SemEvalCorpusReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
		
				AnalysisEngineDescription seg = createPrimitiveDescription(BreakIteratorSegmenter.class);
				
				AggregateBuilder builder = new AggregateBuilder();
				builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
				builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
				AnalysisEngine aggr_seg = builder.createAggregate();
				
				AnalysisEngineDescription tt = createPrimitiveDescription(
						TreeTaggerPosLemmaTT4J.class,
						TreeTaggerPosLemmaTT4J.PARAM_LANGUAGE_CODE, "en");		
				builder = new AggregateBuilder();
				builder.add(tt, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
				builder.add(tt, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
				AnalysisEngine aggr_tt = builder.createAggregate();
				
	//			AnalysisEngineDescription stopw = createPrimitiveDescription(
	//					StopwordFilter.class,
	//					StopwordFilter.PARAM_STOPWORD_LIST, new File("src/main/resources/stopwords_english_punctuation.txt").getAbsolutePath(),
	//					StopwordFilter.PARAM_ANNOTATION_TYPE_NAME, Lemma.class.getName(),
	//					StopwordFilter.PARAM_STRING_REPRESENTATION_METHOD_NAME, "getValue");
	//			builder = new AggregateBuilder();
	//			builder.add(stopw, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
	//			builder.add(stopw, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
	//			AnalysisEngineDescription aggr_stopw = builder.createAggregateDescription();
		
				AnalysisEngine scorer = createPrimitive(SimilarityScorer.class,
				    SimilarityScorer.PARAM_NAME_VIEW_1, CombinationReader.VIEW_1,
				    SimilarityScorer.PARAM_NAME_VIEW_2, CombinationReader.VIEW_2,
				    SimilarityScorer.PARAM_SEGMENT_FEATURE_PATH, config.getSegmentFeaturePath(),
				    SimilarityScorer.PARAM_TEXT_SIMILARITY_RESOURCE, config.getResource()
				    );
				
				AnalysisEngine writer = createPrimitive(SimilarityScoreWriter.class,
					SimilarityScoreWriter.PARAM_OUTPUT_FILE, OUTPUT_FEATURE_DIR + "/" + config.getTargetPath() + "/" + config.getMeasureName() + ".txt",
					SimilarityScoreWriter.PARAM_OUTPUT_SCORES_ONLY, true);
		
				SimplePipeline.runPipeline(reader, aggr_seg, aggr_tt, scorer, writer);
			}
		}
		
		// Read the output and print to the console
		System.out.println("The features were written to " + OUTPUT_FEATURE_DIR);
	}
}
