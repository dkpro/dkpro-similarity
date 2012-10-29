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
	
	public static final String FEATURES_DIR = "src/main/resources/features";
	public static final String MODELS_DIR = "src/main/resources/models";
	
	public static final String OUTPUT_DIR = "target/output";
	
	public static void main(String[] args)
		throws Exception
	{
		FeatureGeneration.generateFeatures(MSRpar, TRAIN);
		FeatureGeneration.generateFeatures(MSRvid, TRAIN);
		FeatureGeneration.generateFeatures(SMTeuroparl, TRAIN);
		
		Features2Arff.toArffFile(TRAIN, MSRpar, MSRvid, SMTeuroparl);

		Evaluator.runLinearRegressionCV(TRAIN, MSRpar, MSRvid, SMTeuroparl);
		Evaluator.runEvaluationMetric(TRAIN, PearsonAll, MSRpar, MSRvid, SMTeuroparl);
		Evaluator.runEvaluationMetric(TRAIN, PearsonMean, MSRpar, MSRvid, SMTeuroparl);
	}

	public static void mainTest(String[] args)
		throws Exception
	{
		FeatureGeneration.generateFeatures(MSRpar, TRAIN);
		FeatureGeneration.generateFeatures(MSRvid, TRAIN);
		FeatureGeneration.generateFeatures(SMTeuroparl, TRAIN);
		
		FeatureGeneration.generateFeatures(MSRpar, TEST);
		FeatureGeneration.generateFeatures(MSRvid, TEST);
		FeatureGeneration.generateFeatures(SMTeuroparl, TEST);
		
		FeatureGeneration.combineFeatureSets(TRAIN, ALL, MSRpar, MSRvid, SMTeuroparl);
		
		Features2Arff.toArffFile(TRAIN, ALL);
		Features2Arff.toArffFile(TEST, MSRpar, MSRvid, SMTeuroparl);

		Evaluator.runLinearRegression(ALL, MSRpar, MSRvid, SMTeuroparl);
		
		// For submission scenario, comment the lines below
		// (there is no gold standard present then to compare with)
		Evaluator.runEvaluationMetric(TEST, PearsonAll, MSRpar, MSRvid, SMTeuroparl);
		Evaluator.runEvaluationMetric(TEST, PearsonMean, MSRpar, MSRvid, SMTeuroparl);
	}
}
