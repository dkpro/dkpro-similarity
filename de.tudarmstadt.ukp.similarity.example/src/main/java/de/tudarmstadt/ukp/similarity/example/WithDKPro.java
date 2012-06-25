package de.tudarmstadt.ukp.similarity.example;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitive;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.uimafit.component.xwriter.CASDumpWriter;
import org.uimafit.factory.AggregateBuilder;
import org.uimafit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.similarity.dkpro.annotator.SimilarityScorer;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.PlainTextCombinationReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader.CombinationStrategy;
import de.tudarmstadt.ukp.similarity.dkpro.resource.TextSimilarityDefaultResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lexical.WordNGramContainmentResource;


public class WithDKPro
{
	private static final String OUTPUT_FILE = "target/output.txt"; 
	
	public static void main(String[] args)
		throws Exception
	{
		// Run the pipeline with different similarity measures
		for (int i = 1; i <= 2; i++)
		{	
			CollectionReader reader = createCollectionReader(PlainTextCombinationReader.class,
					PlainTextCombinationReader.PARAM_INPUT_DIR, "classpath:/datasets/test/plaintext",
					PlainTextCombinationReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
	
			AnalysisEngineDescription seg = createPrimitiveDescription(BreakIteratorSegmenter.class);
			
			AggregateBuilder builder = new AggregateBuilder();
			builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
			builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
			AnalysisEngine aggr_seg = builder.createAggregate();
	
			AnalysisEngine scorer = getSimilarityScorer(i);
			
			AnalysisEngine writer = createPrimitive(CASDumpWriter.class,
			    CASDumpWriter.PARAM_OUTPUT_FILE, OUTPUT_FILE);
	
			SimplePipeline.runPipeline(reader, aggr_seg, scorer, writer);
			
			// Read the output and print to the console
			File out = new File(OUTPUT_FILE);
			String output = FileUtils.readFileToString(out);
			System.out.println(output);
		}
	}
	
	private static AnalysisEngine getSimilarityScorer(int i)
		throws Exception
	{
		switch (i)
		{
		case 1:
			// Uses default constructor of a word n-gram similarity measure
			// (operates on trigrams, in this case)
			AnalysisEngine scorer = createPrimitive(SimilarityScorer.class,
			    SimilarityScorer.PARAM_NAME_VIEW_1, CombinationReader.VIEW_1,
			    SimilarityScorer.PARAM_NAME_VIEW_2, CombinationReader.VIEW_2,
			    SimilarityScorer.PARAM_SEGMENT_FEATURE_PATH, "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
			    SimilarityScorer.PARAM_TEXT_SIMILARITY_MEASURE, createExternalResourceDescription(
			    	TextSimilarityDefaultResource.class,
			    	TextSimilarityDefaultResource.PARAM_TEXT_SIMILARITY_MEASURE, "de.tudarmstadt.ukp.similarity.algorithms.lexical.ngrams.WordNGramContainmentMeasure")
			    );
			return scorer;
		case 2:
			// Uses designated external resource for word n-gram similarity measures
			// (also operates on trigrams, in this case) 
			scorer = createPrimitive(SimilarityScorer.class,
			    SimilarityScorer.PARAM_NAME_VIEW_1, CombinationReader.VIEW_1,
			    SimilarityScorer.PARAM_NAME_VIEW_2, CombinationReader.VIEW_2,
			    SimilarityScorer.PARAM_SEGMENT_FEATURE_PATH, "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
			    SimilarityScorer.PARAM_TEXT_SIMILARITY_MEASURE, createExternalResourceDescription(
			    	WordNGramContainmentResource.class,
			    	WordNGramContainmentResource.PARAM_N, "3")
			    );
			return scorer;
		}
		return null;
	}
}
