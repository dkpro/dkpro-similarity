package de.tudarmstadt.ukp.similarity.semeval2013;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitive;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.uimafit.factory.AggregateBuilder;
import org.uimafit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Document;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.similarity.dkpro.annotator.SimilarityScorer;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.SemEvalCorpusReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.ShortAnswerGradingReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader.CombinationStrategy;
import de.tudarmstadt.ukp.similarity.dkpro.resource.ml.LinearRegressionResource;
import de.tudarmstadt.ukp.similarity.ml.io.SimilarityScoreWriter;
import de.tudarmstadt.ukp.similarity.semeval2013.SemEval2013Baseline.Dataset;
import de.tudarmstadt.ukp.similarity.semeval2013.SemEval2013Baseline.EvaluationMetric;
import de.tudarmstadt.ukp.similarity.semeval2013.SemEval2013Baseline.Mode;
import de.tudarmstadt.ukp.statistics.correlation.PearsonCorrelation;

import static de.tudarmstadt.ukp.similarity.semeval2013.SemEval2013Baseline.OUTPUT_DIR;
import static de.tudarmstadt.ukp.similarity.semeval2013.SemEval2013Baseline.MODELS_DIR;
import static de.tudarmstadt.ukp.similarity.semeval2013.SemEval2013Baseline.REPORT_FILE;


public class Evaluation
{
	public static void runLinearRegression(Dataset train, Dataset... test)
		throws UIMAException, IOException
	{
		for (Dataset dataset : test)
		{
			CollectionReader reader = createCollectionReader(SemEvalCorpusReader.class,
					SemEvalCorpusReader.PARAM_INPUT_FILE, "classpath:/datasets/semeval/test/STS.input." + dataset.toString() + ".txt",
					SemEvalCorpusReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
			
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
				    	LinearRegressionResource.PARAM_TRAIN_ARFF, MODELS_DIR + "/train/" + train.toString() + ".arff",
				    	LinearRegressionResource.PARAM_TEST_ARFF, MODELS_DIR + "/test/" + dataset.toString() + ".arff")
				    );
			
			AnalysisEngine writer = createPrimitive(SimilarityScoreWriter.class,
					SimilarityScoreWriter.PARAM_OUTPUT_FILE, OUTPUT_DIR + "/test/" + dataset.toString() + ".csv",
					SimilarityScoreWriter.PARAM_OUTPUT_SCORES_ONLY, true,
					SimilarityScoreWriter.PARAM_OUTPUT_GOLD_SCORES, false);
	
			SimplePipeline.runPipeline(reader, aggr_seg, scorer, writer);
		}
	}
	
	public static void runLinearRegressionCV(Dataset dataset)
	{
		
	}
	
	public static void runEvaluationMetrics(Mode mode, EvaluationMetric... metrics)
		throws IOException
	{
		StringBuilder sb = new StringBuilder();
		
		for (EvaluationMetric metric : metrics)
		{
			for (Dataset dataset : Dataset.values())
			{
				if (dataset.equals(Dataset.ALL))
					continue;
				
				File expScoresFile = new File(OUTPUT_DIR + "/" + mode.toString().toLowerCase() + "/" + dataset.toString() + ".csv");
				
				if (!expScoresFile.exists())
					continue;
				
				String gsScoresFilePath = "classpath:/goldstandards/semeval/" + mode.toString().toLowerCase() + "/" + 
						"STS.gs." + dataset.toString() + ".txt";
				
				PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
		        Resource res = r.getResource(gsScoresFilePath);				
				File gsScoresFile = res.getFile();
				
				List<Double> expScores = new ArrayList<Double>();
				List<Double> gsScores = new ArrayList<Double>();
				
				List<String> expLines = FileUtils.readLines(expScoresFile);
				List<String> gsLines = FileUtils.readLines(gsScoresFile);
				
				for (int i = 0; i < expLines.size(); i++)
				{
					expScores.add(Double.parseDouble(expLines.get(i)));
					gsScores.add(Double.parseDouble(gsLines.get(i)));
				}

				Double correl = PearsonCorrelation.computeCorrelation(expScores, gsScores);
				
				FileUtils.writeStringToFile(
						new File(OUTPUT_DIR + "/" + mode.toString().toLowerCase() + "/" + dataset.toString() + ".txt"),
						correl.toString());
			}
		}
		
		FileUtils.writeStringToFile(new File(OUTPUT_DIR + "/" + REPORT_FILE), sb.toString());
	}
}
