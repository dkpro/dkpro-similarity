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
package de.tudarmstadt.ukp.similarity.experiments.coling2012.util;

import static de.tudarmstadt.ukp.similarity.experiments.coling2012.Pipeline.MODELS_DIR;
import static de.tudarmstadt.ukp.similarity.experiments.coling2012.Pipeline.OUTPUT_DIR;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AddClassification;
import weka.filters.unsupervised.attribute.AddID;
import weka.filters.unsupervised.attribute.Remove;
import de.tudarmstadt.ukp.similarity.experiments.coling2012.Pipeline.Dataset;
import de.tudarmstadt.ukp.similarity.experiments.coling2012.Pipeline.EvaluationMetric;


public class Evaluator
{
	public static final String LF = System.getProperty("line.separator");
	
	public enum WekaClassifier
	{
		NAIVE_BAYES,
		J48
	}
	
	
	public static void runClassifierCV(WekaClassifier wekaClassifier, Dataset dataset)
		throws Exception
	{
		// Set parameters
		int folds = 10;
		Classifier baseClassifier = getClassifier(wekaClassifier);
		
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
	        filter.setClassifier(filteredClassifier);
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
	    
	    // Prepare output classification
	    String[] scores = new String[predictedData.numInstances()];
	    
	    for (Instance predInst : predictedData)
	    {
	    	int id = new Double(predInst.value(predInst.attribute(0))).intValue() - 1;
	    	
	    	int valueIdx = predictedData.numAttributes() - 2;
	    	
	    	String value = predInst.stringValue(predInst.attribute(valueIdx));
	    	
	    	scores[id] = value;
	    }
	    
	    // Output
	    StringBuilder sb = new StringBuilder();
	    for (String score : scores)
	    	sb.append(score.toString() + LF);
	    
	    FileUtils.writeStringToFile(
	    	new File(OUTPUT_DIR + "/" + dataset.toString() + "/" + wekaClassifier.toString() + "/output.csv"),
	    	sb.toString());
	}
	
	@SuppressWarnings("unchecked")
	public static void runEvaluationMetric(WekaClassifier wekaClassifier, EvaluationMetric metric, Dataset dataset)
		throws IOException
	{
		StringBuilder sb = new StringBuilder();
		
		List<String> gold = ColingUtils.readGoldstandard(dataset);
		List<String> exp = FileUtils.readLines(new File(OUTPUT_DIR + "/" + dataset.toString() + "/" + wekaClassifier.toString() + "/output.csv"));
		
		if (metric.equals(EvaluationMetric.Accuracy))
		{			
			double acc = 0.0;
			
			for (int i = 0; i < gold.size(); i++)
			{
				if (gold.get(i).equals(exp.get(i)))
					acc++;
			}
			
			acc /= gold.size();
			
			sb.append(acc);
		}
		else if (metric.equals(EvaluationMetric.AverageF1))
		{
			// Get all classes
			Set<String> classesSet = new HashSet<String>();
			for (String cl : gold)
				classesSet.add(cl);
			
			// Order the classes
			List<String> classes = new ArrayList<String>(classesSet);
			
			// Initialize confusion matrix
			// exp\class	A	B
			// A			x1	x2
			// B			x3	x4			
			int[][] matrix = new int[classes.size()][classes.size()];
			
			// Initialize matrix
			for (int i = 0; i < classes.size(); i++)
				for (int j = 0; j < classes.size(); j++)
					matrix[i][j] = 0;
			
			// Construct confusion matrix
			for (int i = 0; i < gold.size(); i++)
			{
				int goldIndex = classes.indexOf(gold.get(i));
				int expIndex = classes.indexOf(exp.get(i));
				
				matrix[goldIndex][expIndex] += 1;
			}
			
			// Compute precision and recall per class
			double[] prec = new double[classes.size()];
			double[] rec = new double[classes.size()];
			
			for (int i = 0; i < classes.size(); i++)
			{
				double tp = matrix[i][i];
				double fp = 0.0;
				double fn = 0.0;
				
				// FP
				for (int j = 0; j < classes.size(); j++)
				{
					if (i == j)
						continue;
					
					fp += matrix[j][i];
				}
				
				// FN
				for (int j = 0; j < classes.size(); j++)
				{
					if (i == j)
						continue;
					
					fn += matrix[i][j];
				}
				
				// Save
				prec[i] = tp / (tp + fp);
				rec[i] = tp / (tp + fn);				
			}
			
			// Compute average F1 score across all classes
			double f1 = 0.0;
			
			for (int i = 0; i < classes.size(); i++)
			{
				double f1PerClass = (2 * prec[i] * rec[i]) / (prec[i] + rec[i]);
				f1 += f1PerClass;
			}
			
			f1 = f1 / classes.size();
			
			// Output
			sb.append(f1);
		}
		
		FileUtils.writeStringToFile(new File(OUTPUT_DIR + "/" + dataset.toString() + "/" + wekaClassifier.toString() + "/" + metric.toString() + ".txt"), sb.toString());
	}
	
	
	
	public static Classifier getClassifier(WekaClassifier classifier)
		throws IllegalArgumentException
	{
		try {
			switch (classifier)
			{
				case NAIVE_BAYES:
					return new NaiveBayes();
				case J48:
					J48 j48 = new J48();			
					j48.setOptions(new String[] { "-C", "0.25", "-M", "2" });
					return j48;
//				case SMO:
//					SMO smo = new SMO();
//					smo.setOptions(Utils.splitOptions("-C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""));
//					return smo;
//				case LOGISTIC:
//					Logistic logistic = new Logistic();
//					logistic.setOptions(Utils.splitOptions("-R 1.0E-8 -M -1"));
//					return logistic;
				default:
					throw new IllegalArgumentException("Classifier " + classifier + " not found!");
			}
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

	}
}
