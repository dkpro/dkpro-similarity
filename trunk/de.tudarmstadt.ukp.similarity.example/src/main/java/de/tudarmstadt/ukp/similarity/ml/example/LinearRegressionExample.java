package de.tudarmstadt.ukp.similarity.ml.example;

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

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Document;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.similarity.dkpro.annotator.SimilarityScorer;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.PlainTextCombinationReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader.CombinationStrategy;
import de.tudarmstadt.ukp.similarity.dkpro.resource.TextSimilarityResourceBase;
import de.tudarmstadt.ukp.similarity.dkpro.resource.ml.LinearRegressionResource;

public class LinearRegressionExample
{
	private static final String OUTPUT_FILE = "target/output.txt";

	public static void main(String[] args)
		throws Exception
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
			    SimilarityScorer.PARAM_SEGMENT_FEATURE_PATH, Document.class.getName(),
			    SimilarityScorer.PARAM_TEXT_SIMILARITY_RESOURCE, createExternalResourceDescription(
			    	LinearRegressionResource.class,
			    	LinearRegressionResource.PARAM_MODE, TextSimilarityResourceBase.TextSimilarityResourceMode.jcas.toString(),
			    	LinearRegressionResource.PARAM_TRAIN_ARFF, "classpath:models/train.arff",
			    	LinearRegressionResource.PARAM_TEST_ARFF, "classpath:models/test.arff")
			    );
		
		AnalysisEngine writer = createPrimitive(CASDumpWriter.class,
		    CASDumpWriter.PARAM_OUTPUT_FILE, OUTPUT_FILE);

		SimplePipeline.runPipeline(reader, aggr_seg, scorer, writer);
		
		// Read the output and print to the console
		File out = new File(OUTPUT_FILE);
		String output = FileUtils.readFileToString(out);
		System.out.println(output);
	}

}
