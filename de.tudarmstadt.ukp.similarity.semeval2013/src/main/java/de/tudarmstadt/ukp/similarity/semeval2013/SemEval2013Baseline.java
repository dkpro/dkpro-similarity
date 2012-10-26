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
		PearsonAllnrm,
		PearsonMean
	}
	
	public static final String FEATURES_DIR = "src/main/resources/features";
	public static final String MODELS_DIR = "src/main/resources/models";
	
	public static final String OUTPUT_DIR = "target/output";
	public static final String REPORT_FILE = "report.txt";
	
	public static void main(String[] args)
		throws Exception
	{
		FeatureGeneration.generateFeatures(MSRpar, TRAIN);
		FeatureGeneration.generateFeatures(MSRvid, TRAIN);
		FeatureGeneration.generateFeatures(SMTeuroparl, TRAIN);
		
		Features2Arff.toArffFile(TRAIN, MSRpar, MSRvid, SMTeuroparl);

		Evaluator.runLinearRegressionCV(TRAIN, MSRpar, MSRvid, SMTeuroparl);
		Evaluator.runEvaluationMetrics(TRAIN, PearsonAll, PearsonAllnrm, PearsonMean);
	}

	public static void mainTest(String[] args)
		throws Exception
	{
		FeatureGeneration.generateFeatures(MSRpar, TRAIN);
		FeatureGeneration.generateFeatures(MSRvid, TRAIN);
		FeatureGeneration.generateFeatures(SMTeuroparl, TRAIN);
		
		FeatureGeneration.generateFeatures(MSRpar, TEST);
		FeatureGeneration.generateFeatures(MSRvid, TEST);
		
		FeatureGeneration.combineFeatureSets(TRAIN, ALL, MSRpar, MSRvid, SMTeuroparl);
		
		Features2Arff.toArffFile(TRAIN, ALL);
		Features2Arff.toArffFile(TEST, MSRpar, MSRvid);

		Evaluator.runLinearRegression(ALL, MSRpar, MSRvid);
		Evaluator.runEvaluationMetrics(TEST, PearsonAll, PearsonAllnrm, PearsonMean);
	}

}
