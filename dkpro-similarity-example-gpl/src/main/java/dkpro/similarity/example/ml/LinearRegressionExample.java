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
package dkpro.similarity.example.ml;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Document;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import dkpro.similarity.ml.io.SimilarityScoreWriter;
import dkpro.similarity.uima.annotator.SimilarityScorer;
import dkpro.similarity.uima.io.CombinationReader;
import dkpro.similarity.uima.io.CombinationReader.CombinationStrategy;
import dkpro.similarity.uima.io.ShortAnswerGradingReader;
import dkpro.similarity.uima.resource.ml.LinearRegressionResource;

public class LinearRegressionExample
{
	private static final String OUTPUT_FILE = "target/output.txt";

	public static void main(String[] args)
		throws Exception
	{
		CollectionReader reader = createReader(ShortAnswerGradingReader.class,
				ShortAnswerGradingReader.PARAM_INPUT_DIR, "classpath:/datasets/mm09",
				ShortAnswerGradingReader.PARAM_DOCUMENT_IDS, "sequential",
				ShortAnswerGradingReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
		
//		CollectionReader reader = createCollectionReader(PlainTextCombinationReader.class,
//				PlainTextCombinationReader.PARAM_INPUT_DIR, "classpath:/datasets/test/plaintext",
//				PlainTextCombinationReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());

		AnalysisEngineDescription seg = createEngineDescription(BreakIteratorSegmenter.class);
		
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
		builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
		AnalysisEngine aggr_seg = builder.createAggregate();

		AnalysisEngine scorer = createEngine(SimilarityScorer.class,
			    SimilarityScorer.PARAM_NAME_VIEW_1, CombinationReader.VIEW_1,
			    SimilarityScorer.PARAM_NAME_VIEW_2, CombinationReader.VIEW_2,
			    SimilarityScorer.PARAM_SEGMENT_FEATURE_PATH, Document.class.getName(),
			    SimilarityScorer.PARAM_TEXT_SIMILARITY_RESOURCE, createExternalResourceDescription(
			    	LinearRegressionResource.class,
			    	LinearRegressionResource.PARAM_TRAIN_ARFF, "classpath:models/semeval-train-all-combined.arff",
			    	LinearRegressionResource.PARAM_TEST_ARFF, "classpath:models/mm09.arff")
			    );
		
		AnalysisEngine writer = createEngine(SimilarityScoreWriter.class,
				SimilarityScoreWriter.PARAM_OUTPUT_FILE, OUTPUT_FILE,
				SimilarityScoreWriter.PARAM_OUTPUT_SCORES_ONLY, true,
				SimilarityScoreWriter.PARAM_OUTPUT_GOLD_SCORES, true);

		SimplePipeline.runPipeline(reader, aggr_seg, scorer, writer);
		
		// Read the output and print to the console
		File out = new File(OUTPUT_FILE);
		String output = FileUtils.readFileToString(out);
		System.out.println(output);
	}

}
