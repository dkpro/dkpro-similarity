package de.tudarmstadt.ukp.similarity.ml.example.mm09;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitive;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.uimafit.factory.AggregateBuilder;
import org.uimafit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.ngrams.WordNGramContainmentMeasure;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.ngrams.WordNGramJaccardMeasure;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.GreedyStringTiling;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.LongestCommonSubsequenceComparator;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.LongestCommonSubsequenceNormComparator;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.LongestCommonSubstringComparator;
import de.tudarmstadt.ukp.similarity.dkpro.annotator.SimilarityScorer;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.PlainTextCombinationReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.ShortAnswerGradingReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader.CombinationStrategy;
import de.tudarmstadt.ukp.similarity.dkpro.resource.SimpleTextSimilarityResource;
import de.tudarmstadt.ukp.similarity.ml.FeatureConfig;
import de.tudarmstadt.ukp.similarity.ml.FeatureConfig.SimilaritySegments;
import de.tudarmstadt.ukp.similarity.ml.io.SimilarityScoreWriter;


public class FeatureGeneration
{
	private static final String OUTPUT_FEATURE_DIR = "src/main/resources/mm09-features"; 
	
	public static void main(String[] args)
		throws Exception
	{
		// Define the features
		List<FeatureConfig> configs = new ArrayList<FeatureConfig>();
		
		// String features
		configs.add(new FeatureConfig(
				new GreedyStringTiling(3),
				SimilaritySegments.STRING,
				false,
				"content/string"));
		
		configs.add(new FeatureConfig(
				new LongestCommonSubsequenceComparator(),
				SimilaritySegments.STRING,
				false,
				"content/string"));
		
		configs.add(new FeatureConfig(
				new LongestCommonSubsequenceNormComparator(),
				SimilaritySegments.STRING,
				false,
				"content/string"));
		
		configs.add(new FeatureConfig(
				new LongestCommonSubstringComparator(),
				SimilaritySegments.STRING,
				false,
				"content/string"));
		
		// N-Grams
		configs.add(new FeatureConfig(
				new WordNGramContainmentMeasure(1),
				SimilaritySegments.TOKENS,
				false,
				"content/word-ngrams"));
		
		configs.add(new FeatureConfig(
				new WordNGramContainmentMeasure(2),
				SimilaritySegments.TOKENS,
				false,
				"content/word-ngrams")); 
		
		configs.add(new FeatureConfig(
				new WordNGramJaccardMeasure(2),
				SimilaritySegments.TOKENS,
				false,
				"content/word-ngrams"));
		
		configs.add(new FeatureConfig(
				new WordNGramJaccardMeasure(4),
				SimilaritySegments.TOKENS,
				false,
				"content/word-ngrams"));
		
		// Run the pipeline		
		for (FeatureConfig config : configs)
		{			
			CollectionReader reader = createCollectionReader(ShortAnswerGradingReader.class,
					ShortAnswerGradingReader.PARAM_INPUT_DIR, "classpath:/datasets/mm09",
					ShortAnswerGradingReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
	
			AnalysisEngineDescription seg = createPrimitiveDescription(BreakIteratorSegmenter.class);
			
			AggregateBuilder builder = new AggregateBuilder();
			builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
			builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
			AnalysisEngine aggr_seg = builder.createAggregate();
	
			AnalysisEngine scorer = createPrimitive(SimilarityScorer.class,
			    SimilarityScorer.PARAM_NAME_VIEW_1, CombinationReader.VIEW_1,
			    SimilarityScorer.PARAM_NAME_VIEW_2, CombinationReader.VIEW_2,
			    SimilarityScorer.PARAM_SEGMENT_FEATURE_PATH, config.getSegmentFeaturePath(),
			    SimilarityScorer.PARAM_TEXT_SIMILARITY_RESOURCE, createExternalResourceDescription(
			    	SimpleTextSimilarityResource.class,
			    	SimpleTextSimilarityResource.PARAM_MODE, "text",
			    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, config.getMeasure().getClass().getName())
			    );
			
			AnalysisEngine writer = createPrimitive(SimilarityScoreWriter.class,
				SimilarityScoreWriter.PARAM_OUTPUT_FILE, OUTPUT_FEATURE_DIR + "/" + config.getTargetPath() + "/" + config.getMeasure().getName() + ".txt",
				SimilarityScoreWriter.PARAM_OUTPUT_SCORES_ONLY, true);
	
			SimplePipeline.runPipeline(reader, aggr_seg, scorer, writer);
		}
		
		// Read the output and print to the console
		System.out.println("The features were written to " + OUTPUT_FEATURE_DIR);
	}
}
