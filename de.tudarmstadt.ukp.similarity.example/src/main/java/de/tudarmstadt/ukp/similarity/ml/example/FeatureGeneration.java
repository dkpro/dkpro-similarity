package de.tudarmstadt.ukp.similarity.ml.example;

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
import de.tudarmstadt.ukp.similarity.algorithms.api.resource.TextSimilarityDefaultResource;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.ngrams.WordNGramContainmentMeasure;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.ngrams.WordNGramJaccardMeasure;
import de.tudarmstadt.ukp.similarity.dkpro.annotator.SimilarityScorer;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.PlainTextCombinationReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader.CombinationStrategy;
import de.tudarmstadt.ukp.similarity.ml.FeatureConfig;
import de.tudarmstadt.ukp.similarity.ml.FeatureConfig.SimilaritySegments;
import de.tudarmstadt.ukp.similarity.ml.io.SimilarityScoreWriter;


public class FeatureGeneration
{
	private static final String OUTPUT_FEATURE_DIR = "target/features"; 
	
	public static void main(String[] args)
		throws Exception
	{
		// Define the features
		List<FeatureConfig> configs = new ArrayList<FeatureConfig>();
		
		configs.add(new FeatureConfig(
				new WordNGramContainmentMeasure(),
				SimilaritySegments.TOKENS,
				false,
				"content/word-ngrams"));
		
		configs.add(new FeatureConfig(
			new WordNGramJaccardMeasure(),
			SimilaritySegments.TOKENS,
			false,
			"content/word-ngrams"));
		
		// Run the pipeline		
		for (FeatureConfig config : configs)
		{			
			CollectionReader reader = createCollectionReader(PlainTextCombinationReader.class,
					PlainTextCombinationReader.PARAM_INPUT_DIR, "classpath:/datasets/test/plaintext",
					PlainTextCombinationReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
	
			AnalysisEngineDescription seg = createPrimitiveDescription(BreakIteratorSegmenter.class);
			
			AggregateBuilder builder = new AggregateBuilder();
			builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
			builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
			AnalysisEngine aggr_seg = builder.createAggregate();
	
			AnalysisEngine scorer = createPrimitive(SimilarityScorer.class,
			    SimilarityScorer.PARAM_NAME_VIEW_1, CombinationReader.VIEW_1,
			    SimilarityScorer.PARAM_NAME_VIEW_2, CombinationReader.VIEW_2,
			    SimilarityScorer.PARAM_SEGMENT_FEATURE_PATH, config.getSegmentFeaturePath(),
			    SimilarityScorer.PARAM_TEXT_SIMILARITY_MEASURE, createExternalResourceDescription(
			    	TextSimilarityDefaultResource.class,
			    	TextSimilarityDefaultResource.PARAM_TEXT_SIMILARITY_MEASURE, config.getMeasure().getClass().getName())
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
