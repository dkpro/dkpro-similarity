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
package dkpro.similarity.experiments.sts2013baseline.util;

import static dkpro.similarity.experiments.sts2013baseline.Pipeline.DATASET_DIR;
import static dkpro.similarity.experiments.sts2013baseline.Pipeline.GOLDSTANDARD_DIR;
import static dkpro.similarity.experiments.sts2013baseline.Pipeline.MODELS_DIR;
import static dkpro.similarity.experiments.sts2013baseline.Pipeline.OUTPUT_DIR;
import static dkpro.similarity.experiments.sts2013baseline.Pipeline.EvaluationMetric.PearsonAll;
import static dkpro.similarity.experiments.sts2013baseline.Pipeline.EvaluationMetric.PearsonMean;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AddClassification;
import weka.filters.unsupervised.attribute.AddID;
import weka.filters.unsupervised.attribute.Remove;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Document;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import dkpro.similarity.experiments.sts2013baseline.Pipeline.Dataset;
import dkpro.similarity.experiments.sts2013baseline.Pipeline.EvaluationMetric;
import dkpro.similarity.experiments.sts2013baseline.Pipeline.Mode;
import dkpro.similarity.experiments.sts2013baseline.filter.LogFilter;
import dkpro.similarity.ml.io.SimilarityScoreWriter;
import dkpro.similarity.uima.annotator.SimilarityScorer;
import dkpro.similarity.uima.io.CombinationReader;
import dkpro.similarity.uima.io.CombinationReader.CombinationStrategy;
import dkpro.similarity.uima.io.SemEvalCorpusReader;
import dkpro.similarity.uima.resource.ml.LinearRegressionResource;


public class Evaluator
{
	public static final String LF = System.getProperty("line.separator");
	
	public static void runLinearRegression(Dataset train, Dataset... test)
		throws UIMAException, IOException
	{
		for (Dataset dataset : test)
		{
			CollectionReader reader = createReader(SemEvalCorpusReader.class,
					SemEvalCorpusReader.PARAM_INPUT_FILE, DATASET_DIR + "/test/STS.input." + dataset.toString() + ".txt",
					SemEvalCorpusReader.PARAM_LANGUAGE, "en",
					SemEvalCorpusReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
			
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
				    	LinearRegressionResource.PARAM_TRAIN_ARFF, MODELS_DIR + "/train/" + train.toString() + ".arff",
				    	LinearRegressionResource.PARAM_TEST_ARFF, MODELS_DIR + "/test/" + dataset.toString() + ".arff")
				    );
			
			AnalysisEngine writer = createEngine(SimilarityScoreWriter.class,
					SimilarityScoreWriter.PARAM_OUTPUT_FILE, OUTPUT_DIR + "/test/" + dataset.toString() + ".csv",
					SimilarityScoreWriter.PARAM_OUTPUT_SCORES_ONLY, true,
					SimilarityScoreWriter.PARAM_OUTPUT_GOLD_SCORES, false);
	
			SimplePipeline.runPipeline(reader, aggr_seg, scorer, writer);
		}
	}
	
	public static void runLinearRegressionCV(Mode mode, Dataset... datasets)
	    throws Exception
	{
		for (Dataset dataset : datasets)
		{
			// Set parameters
			int folds = 10;
			Classifier baseClassifier = new LinearRegression();
			
			// Set up the random number generator
	    	long seed = new Date().getTime();			
			Random random = new Random(seed);	
	    	
			// Add IDs to the instances
			AddID.main(new String[] {"-i", MODELS_DIR + "/" + mode.toString().toLowerCase() + "/" + dataset.toString() + ".arff",
 								 	 "-o", MODELS_DIR + "/" + mode.toString().toLowerCase() + "/" + dataset.toString() + "-plusIDs.arff" });

			String location = MODELS_DIR + "/" + mode.toString().toLowerCase() + "/" + dataset.toString() + "-plusIDs.arff";
			
	        Instances data = DataSource.read(location);
	        
	        if (data == null) {
	            throw new IOException("Could not load data from: " + location);
	        }
	        
	        data.setClassIndex(data.numAttributes() - 1);
						
	        // Instantiate the Remove filter
	        Remove removeIDFilter = new Remove();
        	removeIDFilter.setAttributeIndices("first");
			
			// Randomize the data
			data.randomize(random);
		
			// Perform cross-validation
		    Instances predictedData = null;
		    Evaluation eval = new Evaluation(data);
		    
		    for (int n = 0; n < folds; n++)
		    {
		    	Instances train = data.trainCV(folds, n, random);
		        Instances test = data.testCV(folds, n);
		        
		        // Apply log filter
			    Filter logFilter = new LogFilter();
		        logFilter.setInputFormat(train);
		        train = Filter.useFilter(train, logFilter);        
		        logFilter.setInputFormat(test);
		        test = Filter.useFilter(test, logFilter);
		        
		        // Copy the classifier
		        Classifier classifier = AbstractClassifier.makeCopy(baseClassifier);
		        	         		        
		        // Instantiate the FilteredClassifier
		        FilteredClassifier filteredClassifier = new FilteredClassifier();
		        filteredClassifier.setFilter(removeIDFilter);
		        filteredClassifier.setClassifier(classifier);
		        	 
		        // Build the classifier
		        filteredClassifier.buildClassifier(train);
		         
		        // Evaluate
		        eval.evaluateModel(classifier, test);
		        
		        // Add predictions
		        AddClassification filter = new AddClassification();
		        filter.setClassifier(classifier);
		        filter.setOutputClassification(true);
		        filter.setOutputDistribution(false);
		        filter.setOutputErrorFlag(true);
		        filter.setInputFormat(train);
		        Filter.useFilter(train, filter);  // trains the classifier
		        
		        Instances pred = Filter.useFilter(test, filter);  // performs predictions on test set
		        if (predictedData == null) {
                    predictedData = new Instances(pred, 0);
                }
		        for (int j = 0; j < pred.numInstances(); j++) {
                    predictedData.add(pred.instance(j));
                }		        
		    }
		    
		    // Prepare output scores
		    double[] scores = new double[predictedData.numInstances()];
		    
		    for (Instance predInst : predictedData)
		    {
		    	int id = new Double(predInst.value(predInst.attribute(0))).intValue() - 1;
		    	
		    	int valueIdx = predictedData.numAttributes() - 2;
		    	
		    	double value = predInst.value(predInst.attribute(valueIdx));
		    	
		    	scores[id] = value;
		    	
		    	// Limit to interval [0;5]
				if (scores[id] > 5.0) {
                    scores[id] = 5.0;
                }
				if (scores[id] < 0.0) {
                    scores[id] = 0.0;
                }
		    }
		    
		    // Output
		    StringBuilder sb = new StringBuilder();
		    for (Double score : scores) {
                sb.append(score.toString() + LF);
            }
		    
		    FileUtils.writeStringToFile(
		    	new File(OUTPUT_DIR + "/" + mode.toString().toLowerCase() + "/" + dataset.toString() + ".csv"),
		    	sb.toString());
		}
	}
	
	public static void runEvaluationMetric(Mode mode, EvaluationMetric metric, Dataset... datasets)
		throws IOException
	{
		StringBuilder sb = new StringBuilder();
		
		// Compute Pearson correlation for the specified datasets
		for (Dataset dataset : datasets)
		{
			computePearsonCorrelation(mode, dataset);
		}
		
		if (metric == PearsonAll)
		{
			List<Double> concatExp = new ArrayList<Double>();
			List<Double> concatGS = new ArrayList<Double>();
			
			// Concat the scores
			for (Dataset dataset : datasets)
			{
				File expScoresFile = new File(OUTPUT_DIR + "/" + mode.toString().toLowerCase() + "/" + dataset.toString() + ".csv");
				
				List<String> lines = FileUtils.readLines(expScoresFile);
				
				for (String line : lines) {
                    concatExp.add(Double.parseDouble(line));
                }
			}
			
			// Concat the gold standard
			for (Dataset dataset : datasets)
			{
				String gsScoresFilePath = GOLDSTANDARD_DIR + "/" + mode.toString().toLowerCase() + "/" + 
						"STS.gs." + dataset.toString() + ".txt";
				
				PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
		        Resource res = r.getResource(gsScoresFilePath);				
				File gsScoresFile = res.getFile();
				
				List<String> lines = FileUtils.readLines(gsScoresFile);
				
				for (String line : lines) {
                    concatGS.add(Double.parseDouble(line));
                }
			}
			
			double[] concatExpArray = ArrayUtils.toPrimitive(concatExp.toArray(new Double[concatExp.size()])); 
			double[] concatGSArray = ArrayUtils.toPrimitive(concatGS.toArray(new Double[concatGS.size()]));

			PearsonsCorrelation pearson = new PearsonsCorrelation();
			Double correl = pearson.correlation(concatExpArray, concatGSArray);
			
			sb.append(correl.toString());
		}
		else if (metric == PearsonMean)
		{
			List<Double> scores = new ArrayList<Double>();
			
			for (Dataset dataset : datasets)
			{
				File resultFile = new File(OUTPUT_DIR + "/" + mode.toString().toLowerCase() + "/" + dataset.toString() + ".txt");
				double score = Double.parseDouble(FileUtils.readFileToString(resultFile));
				
				scores.add(score);
			}
			
			double mean = 0.0;
			for (Double score : scores) {
                mean += score;
            }
			mean = mean / scores.size();
			
			sb.append(mean);
		}

		FileUtils.writeStringToFile(new File(OUTPUT_DIR + "/" + mode.toString().toLowerCase() + "/" + metric.toString() + ".txt"), sb.toString());
	}
	
	private static void computePearsonCorrelation(Mode mode, Dataset dataset)
		throws IOException
	{
		File expScoresFile = new File(OUTPUT_DIR + "/" + mode.toString().toLowerCase() + "/" + dataset.toString() + ".csv");
		
		String gsScoresFilePath = GOLDSTANDARD_DIR + "/" + mode.toString().toLowerCase() + "/" + 
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
		
		double[] expArray = ArrayUtils.toPrimitive(expScores.toArray(new Double[expScores.size()])); 
		double[] gsArray = ArrayUtils.toPrimitive(gsScores.toArray(new Double[gsScores.size()]));

		PearsonsCorrelation pearson = new PearsonsCorrelation();
		Double correl = pearson.correlation(expArray, gsArray);
		
		FileUtils.writeStringToFile(
				new File(OUTPUT_DIR + "/" + mode.toString().toLowerCase() + "/" + dataset.toString() + ".txt"),
				correl.toString());
	}
}
