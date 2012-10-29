package de.tudarmstadt.ukp.similarity.semeval2013;

import static de.tudarmstadt.ukp.similarity.semeval2013.SemEval2013Baseline.Dataset.*;
import static de.tudarmstadt.ukp.similarity.semeval2013.SemEval2013Baseline.Mode.*;
import static de.tudarmstadt.ukp.similarity.semeval2013.SemEval2013Baseline.EvaluationMetric.*;


public class SemEval2013Baseline
{
	public enum Mode
	{
		TRAIN,
		TEST
	}
	
	public enum Dataset
	{
		ALL,
		MSRpar,
		MSRvid,
		OnWN,
		SMTeuroparl,
		SMTnews
	}
	
	public enum EvaluationMetric
	{
		PearsonAll,
		PearsonMean
	}
	
	public static final String DATASET_DIR = "classpath:/datasets/semeval-2012";
	
	public static final String FEATURES_DIR = "target/features";
	public static final String MODELS_DIR = "target/models";
	public static final String UTILS_DIR = "target/utils";
	public static final String OUTPUT_DIR = "target/output";
	
	public static void mainTrain(String[] args)
		throws Exception
	{
		// Generate the features for training data
		FeatureGeneration.generateFeatures(MSRpar, TRAIN);
		FeatureGeneration.generateFeatures(MSRvid, TRAIN);
		FeatureGeneration.generateFeatures(SMTeuroparl, TRAIN);
		
		// Packages features in arff files
		Features2Arff.toArffFile(TRAIN, MSRpar, MSRvid, SMTeuroparl);

		// Run the classifier
		Evaluator.runLinearRegressionCV(TRAIN, MSRpar, MSRvid, SMTeuroparl);
		
		// Evaluate
		Evaluator.runEvaluationMetric(TRAIN, PearsonAll, MSRpar, MSRvid, SMTeuroparl);
		Evaluator.runEvaluationMetric(TRAIN, PearsonMean, MSRpar, MSRvid, SMTeuroparl);
	}

	public static void mainTest(String[] args)
		throws Exception
	{
		// Generate the features for training data
		FeatureGeneration.generateFeatures(MSRpar, TRAIN);
		FeatureGeneration.generateFeatures(MSRvid, TRAIN);
		FeatureGeneration.generateFeatures(SMTeuroparl, TRAIN);
		
		// Generate the features for test data
		FeatureGeneration.generateFeatures(MSRpar, TEST);
		FeatureGeneration.generateFeatures(MSRvid, TEST);
		FeatureGeneration.generateFeatures(SMTeuroparl, TEST);
		FeatureGeneration.generateFeatures(OnWN, TEST);
		FeatureGeneration.generateFeatures(SMTnews, TEST);
		
		// Concatenate all training data
		FeatureGeneration.combineFeatureSets(TRAIN, ALL, MSRpar, MSRvid, SMTeuroparl);
		
		// Package features in arff files
		Features2Arff.toArffFile(TRAIN, ALL);
		Features2Arff.toArffFile(TEST, MSRpar, MSRvid, SMTeuroparl, OnWN, SMTnews);

		// Run the classifer
		Evaluator.runLinearRegression(ALL, MSRpar, MSRvid, SMTeuroparl, OnWN, SMTnews);
		
		// Evaluate
		// Note: For submission scenario, comment the lines below, as 
		// there is no gold standard present then to compare with.
		Evaluator.runEvaluationMetric(TEST, PearsonAll, MSRpar, MSRvid, SMTeuroparl, OnWN, SMTnews);
		Evaluator.runEvaluationMetric(TEST, PearsonMean, MSRpar, MSRvid, SMTeuroparl, OnWN, SMTnews);
	}
}
