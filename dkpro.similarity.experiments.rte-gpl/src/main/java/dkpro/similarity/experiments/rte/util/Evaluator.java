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
package dkpro.similarity.experiments.rte.util;

import static dkpro.similarity.experiments.rte.Pipeline.GOLD_DIR;
import static dkpro.similarity.experiments.rte.Pipeline.MODELS_DIR;
import static dkpro.similarity.experiments.rte.Pipeline.OUTPUT_DIR;
import static dkpro.similarity.experiments.rte.Pipeline.EvaluationMetric.Accuracy;
import static dkpro.similarity.experiments.rte.Pipeline.EvaluationMetric.AveragePrecision;
import static dkpro.similarity.experiments.rte.Pipeline.EvaluationMetric.CWS;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.springframework.util.CollectionUtils;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.classifiers.evaluation.output.prediction.PlainText;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AddClassification;
import weka.filters.unsupervised.attribute.AddID;
import weka.filters.unsupervised.attribute.Remove;
import dkpro.similarity.algorithms.ml.ClassifierSimilarityMeasure;
import dkpro.similarity.algorithms.ml.ClassifierSimilarityMeasure.WekaClassifier;
import dkpro.similarity.experiments.rte.Pipeline.Dataset;
import dkpro.similarity.experiments.rte.Pipeline.EvaluationMetric;
//import de.tudarmstadt.ukp.similarity.experiments.rte.Pipeline.EvaluationMetric;
//import de.tudarmstadt.ukp.similarity.experiments.rte.Pipeline.Mode;
//import de.tudarmstadt.ukp.similarity.experiments.rte.filter.LogFilter;


public class Evaluator
{
	public static final String LF = System.getProperty("line.separator");
	
//	public static void runClassifier(Dataset train, Dataset test)
//		throws UIMAException, IOException
//	{
//		CollectionReader reader = createCollectionReader(
//				RTECorpusReader.class,
//				RTECorpusReader.PARAM_INPUT_FILE, RteUtil.getInputFilePathForDataset(DATASET_DIR, test),
//				RTECorpusReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
//		
//		AnalysisEngineDescription seg = createPrimitiveDescription(
//				BreakIteratorSegmenter.class);
//		
//		AggregateBuilder builder = new AggregateBuilder();
//		builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_1);
//		builder.add(seg, CombinationReader.INITIAL_VIEW, CombinationReader.VIEW_2);
//		AnalysisEngine aggr_seg = builder.createAggregate();
//
//		AnalysisEngine scorer = createPrimitive(
//				SimilarityScorer.class,
//			    SimilarityScorer.PARAM_NAME_VIEW_1, CombinationReader.VIEW_1,
//			    SimilarityScorer.PARAM_NAME_VIEW_2, CombinationReader.VIEW_2,
//			    SimilarityScorer.PARAM_SEGMENT_FEATURE_PATH, Document.class.getName(),
//			    SimilarityScorer.PARAM_TEXT_SIMILARITY_RESOURCE, createExternalResourceDescription(
//			    	ClassifierResource.class,
//			    	ClassifierResource.PARAM_CLASSIFIER, wekaClassifier.toString(),
//			    	ClassifierResource.PARAM_TRAIN_ARFF, MODELS_DIR + "/" + train.toString() + ".arff",
//			    	ClassifierResource.PARAM_TEST_ARFF, MODELS_DIR + "/" + test.toString() + ".arff")
//			    );
//		
//		AnalysisEngine writer = createPrimitive(
//				SimilarityScoreWriter.class,
//				SimilarityScoreWriter.PARAM_OUTPUT_FILE, OUTPUT_DIR + "/" + test.toString() + ".csv",
//				SimilarityScoreWriter.PARAM_OUTPUT_SCORES_ONLY, true,
//				SimilarityScoreWriter.PARAM_OUTPUT_GOLD_SCORES, false);
//
//		SimplePipeline.runPipeline(reader, aggr_seg, scorer, writer);
//	}
	
	public static void runClassifier(WekaClassifier wekaClassifier, Dataset trainDataset, Dataset testDataset)
			throws Exception
	{
		Classifier baseClassifier = ClassifierSimilarityMeasure.getClassifier(wekaClassifier);
		
		// Set up the random number generator
    	long seed = new Date().getTime();			
		Random random = new Random(seed);	
				
		// Add IDs to the train instances and get the instances
		AddID.main(new String[] {"-i", MODELS_DIR + "/" + trainDataset.toString() + ".arff",
							 	 "-o", MODELS_DIR + "/" + trainDataset.toString() + "-plusIDs.arff" });
		Instances train = DataSource.read(MODELS_DIR + "/" + trainDataset.toString() + "-plusIDs.arff");
		train.setClassIndex(train.numAttributes() - 1);	
		
		// Add IDs to the test instances and get the instances
		AddID.main(new String[] {"-i", MODELS_DIR + "/" + testDataset.toString() + ".arff",
							 	 "-o", MODELS_DIR + "/" + testDataset.toString() + "-plusIDs.arff" });
		Instances test = DataSource.read(MODELS_DIR + "/" + testDataset.toString() + "-plusIDs.arff");
		test.setClassIndex(test.numAttributes() - 1);		
		
		// Instantiate the Remove filter
        Remove removeIDFilter = new Remove();
    	removeIDFilter.setAttributeIndices("first");
				
		// Randomize the data
		test.randomize(random);
		
		// Apply log filter
//	    Filter logFilter = new LogFilter();
//	    logFilter.setInputFormat(train);
//	    train = Filter.useFilter(train, logFilter);        
//	    logFilter.setInputFormat(test);
//	    test = Filter.useFilter(test, logFilter);
        
        // Copy the classifier
        Classifier classifier = AbstractClassifier.makeCopy(baseClassifier);
        	
        // Instantiate the FilteredClassifier
        FilteredClassifier filteredClassifier = new FilteredClassifier();
        filteredClassifier.setFilter(removeIDFilter);
        filteredClassifier.setClassifier(classifier);
        	 
        // Build the classifier
        filteredClassifier.buildClassifier(train);
		
        // Prepare the output buffer 
        AbstractOutput output = new PlainText();
        output.setBuffer(new StringBuffer());
        output.setHeader(test);
        output.setAttributes("first");
        
		Evaluation eval = new Evaluation(train);
        eval.evaluateModel(filteredClassifier, test, output);
        
        // Convert predictions to CSV
        // Format: inst#, actual, predicted, error, probability, (ID)
        String[] scores = new String[new Double(eval.numInstances()).intValue()];
        double[] probabilities = new double[new Double(eval.numInstances()).intValue()];
        for (String line : output.getBuffer().toString().split("\n"))
        {
        	String[] linesplit = line.split("\\s+");

        	// If there's been an error, the length of linesplit is 6, otherwise 5,
        	// due to the error flag "+"
        	
        	int id;
        	String expectedValue, classification;
        	double probability;
        	
        	if (line.contains("+"))
        	{
        	   	id = Integer.parseInt(linesplit[6].substring(1, linesplit[6].length() - 1));
	        	expectedValue = linesplit[2].substring(2);
	        	classification = linesplit[3].substring(2);
	        	probability = Double.parseDouble(linesplit[5]);
        	} else {
        		id = Integer.parseInt(linesplit[5].substring(1, linesplit[5].length() - 1));
	        	expectedValue = linesplit[2].substring(2);
	        	classification = linesplit[3].substring(2);
	        	probability = Double.parseDouble(linesplit[4]);
        	}
        	
        	scores[id - 1] = classification;
        	probabilities[id - 1] = probability;
        }
                
        System.out.println(eval.toSummaryString());
	    System.out.println(eval.toMatrixString());
	    
	    // Output classifications
	    StringBuilder sb = new StringBuilder();
	    for (String score : scores)
	    	sb.append(score.toString() + LF);
	    
	    FileUtils.writeStringToFile(
	    	new File(OUTPUT_DIR + "/" + testDataset.toString() + "/" + wekaClassifier.toString() + "/" + testDataset.toString() + ".csv"),
	    	sb.toString());
	    
	    // Output probabilities
	    sb = new StringBuilder();
	    for (Double probability : probabilities)
	    	sb.append(probability.toString() + LF);
	    
	    FileUtils.writeStringToFile(
	    	new File(OUTPUT_DIR + "/" + testDataset.toString() + "/" + wekaClassifier.toString() + "/" + testDataset.toString() + ".probabilities.csv"),
	    	sb.toString());
	    
	    // Output predictions
	    FileUtils.writeStringToFile(
	    	new File(OUTPUT_DIR + "/" + testDataset.toString() + "/" + wekaClassifier.toString() + "/" + testDataset.toString() + ".predictions.txt"),
	    	output.getBuffer().toString());
	    
	    // Output meta information
	    sb = new StringBuilder();
	    sb.append(classifier.toString() + LF);
	    sb.append(eval.toSummaryString() + LF);
	    sb.append(eval.toMatrixString() + LF);
	    
	    FileUtils.writeStringToFile(
	    	new File(OUTPUT_DIR + "/" + testDataset.toString() + "/" + wekaClassifier.toString() + "/" + testDataset.toString() + ".meta.txt"),
	    	sb.toString());
	}
	
	public static void runClassifierCV(WekaClassifier wekaClassifier, Dataset dataset)
		throws Exception
	{
		// Set parameters
		int folds = 10;
		Classifier baseClassifier = ClassifierSimilarityMeasure.getClassifier(wekaClassifier);
		
		// Set up the random number generator
    	long seed = new Date().getTime();			
		Random random = new Random(seed);	
    	
		// Add IDs to the instances
		AddID.main(new String[] {"-i", MODELS_DIR + "/" + dataset.toString() + ".arff",
							 	 "-o", MODELS_DIR + "/" + dataset.toString() + "-plusIDs.arff" });
		Instances data = DataSource.read(MODELS_DIR + "/" + dataset.toString() + "-plusIDs.arff");
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
//		    Filter logFilter = new LogFilter();
//	        logFilter.setInputFormat(train);
//	        train = Filter.useFilter(train, logFilter);        
//	        logFilter.setInputFormat(test);
//	        test = Filter.useFilter(test, logFilter);
	        
	        // Copy the classifier
	        Classifier classifier = AbstractClassifier.makeCopy(baseClassifier);
	        	         		        
	        // Instantiate the FilteredClassifier
	        FilteredClassifier filteredClassifier = new FilteredClassifier();
	        filteredClassifier.setFilter(removeIDFilter);
	        filteredClassifier.setClassifier(classifier);
	        	 
	        // Build the classifier
	        filteredClassifier.buildClassifier(train);
	        
	        // Evaluate
	        eval.evaluateModel(filteredClassifier, test);
	        
	        // Add predictions
	        AddClassification filter = new AddClassification();
	        filter.setClassifier(classifier);
	        filter.setOutputClassification(true);
	        filter.setOutputDistribution(false);
	        filter.setOutputErrorFlag(true);
	        filter.setInputFormat(train);
	        Filter.useFilter(train, filter);  // trains the classifier
	        
	        Instances pred = Filter.useFilter(test, filter);  // performs predictions on test set
	        if (predictedData == null)
	        	predictedData = new Instances(pred, 0);
	        for (int j = 0; j < pred.numInstances(); j++)
	        	predictedData.add(pred.instance(j));		        
	    }
	    
	    System.out.println(eval.toSummaryString());
	    System.out.println(eval.toMatrixString());
	    
	    // Prepare output scores
	    String[] scores = new String[predictedData.numInstances()];
	    
	    for (Instance predInst : predictedData)
	    {
	    	int id = new Double(predInst.value(predInst.attribute(0))).intValue() - 1;
	    	
	    	int valueIdx = predictedData.numAttributes() - 2;
	    	
	    	String value = predInst.stringValue(predInst.attribute(valueIdx));
	    	
	    	scores[id] = value;
	    }
	    
	    // Output classifications
	    StringBuilder sb = new StringBuilder();
	    for (String score : scores)
	    	sb.append(score.toString() + LF);
	    
	    FileUtils.writeStringToFile(
	    	new File(OUTPUT_DIR + "/" + dataset.toString() + "/" + wekaClassifier.toString() + "/" + dataset.toString() + ".csv"),
	    	sb.toString());
	    
	    // Output prediction arff
	    DataSink.write(
	    	OUTPUT_DIR + "/" + dataset.toString() + "/" + wekaClassifier.toString() + "/" + dataset.toString() + ".predicted.arff",
	    	predictedData);
	    
	    // Output meta information
	    sb = new StringBuilder();
	    sb.append(baseClassifier.toString() + LF);
	    sb.append(eval.toSummaryString() + LF);
	    sb.append(eval.toMatrixString() + LF);
	    
	    FileUtils.writeStringToFile(
	    	new File(OUTPUT_DIR + "/" + dataset.toString() + "/" + wekaClassifier.toString() + "/" + dataset.toString() + ".meta.txt"),
	    	sb.toString());
	}

	@SuppressWarnings("unchecked")
	public static void runEvaluationMetric(EvaluationMetric metric, Dataset dataset)
		throws IOException
	{
		// Get all subdirectories (i.e. all classifiers)
		File outputDir = new File(OUTPUT_DIR + "/" + dataset.toString() + "/");
		File[] dirsArray = outputDir.listFiles((FileFilter) FileFilterUtils.directoryFileFilter());
		
		List<File> dirs = CollectionUtils.arrayToList(dirsArray);
		
		// Don't list hidden dirs (such as .svn)
		for (int i = dirs.size() - 1; i >= 0; i--)
			if (dirs.get(i).getName().startsWith("."))
					dirs.remove(i);
		
		// Iteratively evaluate all classifiers' results
		for (File dir : dirs)
			runEvaluationMetric(
					WekaClassifier.valueOf(dir.getName()),
					metric,
					dataset);
	}
	
	public static void runEvaluationMetric(WekaClassifier wekaClassifier, EvaluationMetric metric, Dataset dataset)
			throws IOException
	{
		StringBuilder sb = new StringBuilder();
			
		if (metric == Accuracy)
		{
			// Read gold scores
			List<String> goldScores = FileUtils.readLines(new File(GOLD_DIR + "/" + dataset.toString() + ".txt"));
						
			// Read the experimental scores
			List<String> expScores = FileUtils.readLines(new File(OUTPUT_DIR + "/" + dataset.toString() + "/" + wekaClassifier.toString() + "/" + dataset.toString() + ".csv"));
						
			// Compute the accuracy
			double acc = 0.0;
			for (int i = 0; i < goldScores.size(); i++)
			{
				// The predictions have a max length of 8 characters...
				if (goldScores.get(i).substring(0, Math.min(goldScores.get(i).length(), 8)).equals(
						expScores.get(i).substring(0, Math.min(expScores.get(i).length(), 8))))
					acc++;
			}
			acc = acc / goldScores.size();
			
			sb.append(acc);
		}
		if (metric == CWS)
		{
			// Read gold scores
			List<String> goldScores = FileUtils.readLines(new File(GOLD_DIR + "/" + dataset.toString() + ".txt"));
						
			// Read the experimental scores
			List<String> expScores = FileUtils.readLines(new File(OUTPUT_DIR + "/" + dataset.toString() + "/" + wekaClassifier.toString() + "/" + dataset.toString() + ".csv"));
			
			// Read the confidence scores
			List<String> probabilities = FileUtils.readLines(new File(OUTPUT_DIR + "/" + dataset.toString() + "/" + wekaClassifier.toString() + "/" + dataset.toString() + ".probabilities.csv"));
			
			// Combine the data
			List<CwsData> data = new ArrayList<CwsData>();
			
			for (int i = 0; i < goldScores.size(); i++)
			{
				CwsData cws = (new Evaluator()).new CwsData(
						Double.parseDouble(probabilities.get(i)),
						goldScores.get(i),
						expScores.get(i));
				data.add(cws);
			}
			
			// Sort in descending order
			Collections.sort(data, Collections.reverseOrder());
			
			// Compute the CWS score
			double cwsScore = 0.0;
			for (int i = 0; i < data.size(); i++)
			{
				double cws_sub = 0.0;
				for (int j = 0; j <= i; j++)
				{
					if (data.get(j).isCorrect())
						cws_sub++;
				}
				cws_sub /= (i+1);
				
				cwsScore += cws_sub;
			}
			cwsScore /= data.size();
						
			sb.append(cwsScore);
		}
		if (metric == AveragePrecision)
		{
			// Read gold scores
			List<String> goldScores = FileUtils.readLines(new File(GOLD_DIR + "/" + dataset.toString() + ".txt"));

			// Trim to 8 characters
			for (int i = 0; i < goldScores.size(); i++)
				if (goldScores.get(i).length() > 8)
					goldScores.set(i, goldScores.get(i).substring(0, 8));
			
			// Read the experimental scores
			List<String> expScores = FileUtils.readLines(new File(OUTPUT_DIR + "/" + dataset.toString() + "/" + wekaClassifier.toString() + "/" + dataset.toString() + ".csv"));
			
			// Trim to 8 characters
			for (int i = 0; i < expScores.size(); i++)
				if (expScores.get(i).length() > 8)
					expScores.set(i, expScores.get(i).substring(0, 8));
			
			// Read the confidence scores
			List<String> probabilities = FileUtils.readLines(new File(OUTPUT_DIR + "/" + dataset.toString() + "/" + wekaClassifier.toString() + "/" + dataset.toString() + ".probabilities.csv"));
			
			// Conflate UNKONWN + CONTRADICTION classes for 3-way classifications
			if (RteUtil.hasThreeWayClassification(dataset))
			{
				// Gold
				for (int i = 0; i < goldScores.size(); i++)
					if (goldScores.get(i).equals("CONTRADI") || goldScores.get(i).equals("NO") || goldScores.get(i).equals("FALSE"))
						goldScores.set(i, "FALSE");
				
				// Experimental
				for (int i = 0; i < expScores.size(); i++)
					if (expScores.get(i).equals("CONTRADI") || expScores.get(i).equals("NO") || expScores.get(i).equals("FALSE"))
						expScores.set(i, "FALSE");
			}
			
			// Combine the data
			List<CwsData> data = new ArrayList<CwsData>();
			
			for (int i = 0; i < goldScores.size(); i++)
			{
				CwsData cws = (new Evaluator()).new CwsData(
						Double.parseDouble(probabilities.get(i)),
						goldScores.get(i),
						expScores.get(i));
				data.add(cws);
			}
			
			// Sort in descending order
			Collections.sort(data, Collections.reverseOrder());
			
			// Compute the average precision
			double avgPrec = 0.0;
			int numPositive = 0;
			for (int i = 0; i < data.size(); i++)
			{
				double ap_sub = 0.0;
				if (data.get(i).isPositivePair())
				{
					numPositive++;
					
					for (int j = 0; j <= i; j++)
					{
						if (data.get(j).isCorrect())
							ap_sub++;
					}
					ap_sub /= (i+1);
				}
				
				avgPrec += ap_sub;					
			}
			avgPrec /= numPositive;
						
			sb.append(avgPrec);
		}

		FileUtils.writeStringToFile(new File(OUTPUT_DIR + "/" + dataset.toString() + "/" + wekaClassifier.toString() + "/" + dataset.toString() + "_" + metric.toString() + ".txt"), sb.toString());
		
		System.out.println("[" + wekaClassifier.toString() + "] " + metric.toString() + ": " + sb.toString());
	}
	
	private class CwsData
		implements Comparable
	{
		private double confidence;
		private String goldScore;
		private String expScore;
		
		public CwsData(double confidence, String goldScore, String expScore)
		{
			this.confidence = confidence;
			this.goldScore = goldScore;
			this.expScore = expScore;
		}
		
		public boolean isCorrect()
		{
			return goldScore.equals(expScore);
		}

		public int compareTo(Object other)
		{
			CwsData otherObj = (CwsData)other;
			
			if (this.getConfidence() == otherObj.getConfidence()) {
				return 0;
			} else if (this.getConfidence() > otherObj.getConfidence()) {
				return 1;
			} else {
				return -1;
			}
		}
		
		public boolean isPositivePair()
		{
			return this.goldScore.equals("TRUE") || this.goldScore.equals("YES") || this.goldScore.equals("ENTAILMENT") || this.goldScore.equals("ENTAILME");
		}

		public double getConfidence()
		{
			return confidence;
		}

		public String getGoldScore()
		{
			return goldScore;
		}

		public String getExpScore()
		{
			return expScore;
		}
	}
	
//	
//	@SuppressWarnings("unchecked")
//	private static void computePearsonCorrelation(Mode mode, Dataset dataset)
//		throws IOException
//	{
//		File expScoresFile = new File(OUTPUT_DIR + "/" + mode.toString().toLowerCase() + "/" + dataset.toString() + ".csv");
//		
//		String gsScoresFilePath = GOLDSTANDARD_DIR + "/" + mode.toString().toLowerCase() + "/" + 
//				"STS.gs." + dataset.toString() + ".txt";
//		
//		PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
//        Resource res = r.getResource(gsScoresFilePath);				
//		File gsScoresFile = res.getFile();
//		
//		List<Double> expScores = new ArrayList<Double>();
//		List<Double> gsScores = new ArrayList<Double>();
//		
//		List<String> expLines = FileUtils.readLines(expScoresFile);
//		List<String> gsLines = FileUtils.readLines(gsScoresFile);
//		
//		for (int i = 0; i < expLines.size(); i++)
//		{
//			expScores.add(Double.parseDouble(expLines.get(i)));
//			gsScores.add(Double.parseDouble(gsLines.get(i)));
//		}
//		
//		double[] expArray = ArrayUtils.toPrimitive(expScores.toArray(new Double[expScores.size()])); 
//		double[] gsArray = ArrayUtils.toPrimitive(gsScores.toArray(new Double[gsScores.size()]));
//
//		PearsonsCorrelation pearson = new PearsonsCorrelation();
//		Double correl = pearson.correlation(expArray, gsArray);
//		
//		FileUtils.writeStringToFile(
//				new File(OUTPUT_DIR + "/" + mode.toString().toLowerCase() + "/" + dataset.toString() + ".txt"),
//				correl.toString());
//	}
}
