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
package dkpro.similarity.example;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.component.CasDumpWriter;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import dkpro.similarity.algorithms.lexical.ngrams.WordNGramContainmentMeasure;
import dkpro.similarity.algorithms.lexical.uima.ngrams.WordNGramContainmentResource;
import dkpro.similarity.algorithms.style.TypeTokenRatioComparator;
import dkpro.similarity.uima.annotator.SimilarityScorer;
import dkpro.similarity.uima.io.CombinationReader;
import dkpro.similarity.uima.io.CombinationReader.CombinationStrategy;
import dkpro.similarity.uima.io.PlainTextCombinationReader;
import dkpro.similarity.uima.resource.SimpleJCasTextSimilarityResource;
import dkpro.similarity.uima.resource.SimpleTextSimilarityResource;
//import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerPosLemmaTT4J;


public class WithDKPro
{
	private static final String OUTPUT_FILE = "target/output.txt"; 
	
	public static void main(String[] args)
		throws Exception
	{
		// Run the pipeline with different similarity measures
		for (int i = 1; i <= 3; i++)
		{	
			CollectionReader reader = createReader(PlainTextCombinationReader.class,
					PlainTextCombinationReader.PARAM_INPUT_DIR, "classpath:/datasets/test/plaintext",
					PlainTextCombinationReader.PARAM_LANGUAGE, "en",
					PlainTextCombinationReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
	
			AnalysisEngineDescription seg = createEngineDescription(
					BreakIteratorSegmenter.class);
			AggregateBuilder builder = new AggregateBuilder();
			builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
			builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
			AnalysisEngine aggr_seg = builder.createAggregate();
			
//			AnalysisEngineDescription tt = createPrimitiveDescription(
//					TreeTaggerPosLemmaTT4J.class,
//					TreeTaggerPosLemmaTT4J.PARAM_LANGUAGE_CODE, "en");		
//			builder = new AggregateBuilder();
//			builder.add(tt, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
//			builder.add(tt, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
//			AnalysisEngine aggr_tt = builder.createAggregate();
	
			AnalysisEngine scorer = getSimilarityScorer(i);
			
			AnalysisEngine writer = createEngine(CasDumpWriter.class,
			    CasDumpWriter.PARAM_OUTPUT_FILE, OUTPUT_FILE);
	
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
			AnalysisEngine scorer = createEngine(SimilarityScorer.class,
			    SimilarityScorer.PARAM_NAME_VIEW_1, CombinationReader.VIEW_1,
			    SimilarityScorer.PARAM_NAME_VIEW_2, CombinationReader.VIEW_2,
			    SimilarityScorer.PARAM_SEGMENT_FEATURE_PATH, "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
			    SimilarityScorer.PARAM_TEXT_SIMILARITY_RESOURCE, createExternalResourceDescription(
			    	SimpleTextSimilarityResource.class,
			    	SimpleTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, WordNGramContainmentMeasure.class.getName())
			    );
			return scorer;
		case 2:
			// Uses designated external resource for word n-gram similarity measures
			// (also operates on trigrams, in this case) 
			scorer = createEngine(SimilarityScorer.class,
			    SimilarityScorer.PARAM_NAME_VIEW_1, CombinationReader.VIEW_1,
			    SimilarityScorer.PARAM_NAME_VIEW_2, CombinationReader.VIEW_2,
			    SimilarityScorer.PARAM_SEGMENT_FEATURE_PATH, "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
			    SimilarityScorer.PARAM_TEXT_SIMILARITY_RESOURCE, createExternalResourceDescription(
			    	WordNGramContainmentResource.class,
			    	WordNGramContainmentResource.PARAM_N, "3")
			    );
			return scorer;
		case 3:
			scorer = createEngine(SimilarityScorer.class,
				SimilarityScorer.PARAM_NAME_VIEW_1, CombinationReader.VIEW_1,
			    SimilarityScorer.PARAM_NAME_VIEW_2, CombinationReader.VIEW_2,
			    SimilarityScorer.PARAM_TEXT_SIMILARITY_RESOURCE, createExternalResourceDescription(
			    	SimpleJCasTextSimilarityResource.class,
			    	SimpleJCasTextSimilarityResource.PARAM_TEXT_SIMILARITY_MEASURE, TypeTokenRatioComparator.class.getName())
			    );
			return scorer;
		}
		return null;
	}
}
