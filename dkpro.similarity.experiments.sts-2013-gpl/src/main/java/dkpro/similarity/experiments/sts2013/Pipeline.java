package dkpro.similarity.experiments.sts2013;

import static dkpro.similarity.experiments.sts2013.Pipeline.Dataset.*;
import static dkpro.similarity.experiments.sts2013.Pipeline.EvaluationMetric.*;
import static dkpro.similarity.experiments.sts2013.Pipeline.Mode.*;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import dkpro.similarity.experiments.sts2013.util.Evaluator;
import dkpro.similarity.experiments.sts2013.util.Features2Arff;


/**
 * Full-featured experimental setup.
 */
public class Pipeline
{
	public enum Mode
	{
		TRAIN,
		TEST
	}
	
	public enum Dataset
	{
		FNWN,			// Test data STS-2013
		headlines,
		OnWN,
		SMT,
		ALL,			// All data (test + train) SemEval-2012
		MSRparTrain,	
		MSRvidTrain,
		SMTeuroparlTrain,
		MSRparTest,
		MSRvidTest,
		OnWNTest,
		SMTeuroparlTest,
		SMTnewsTest
	}
	
	public enum EvaluationMetric
	{
		PearsonAll,
		PearsonMean,
		PearsonWeightedMean
	}
	
	public static final String DATASET_DIR = "classpath:/datasets/sts-2013";
	public static final String GOLDSTANDARD_DIR = "classpath:/goldstandards/sts-2013";
	
	public static final String FEATURES_DIR = "target/features";
	public static final String MODELS_DIR = "target/models";
	public static final String UTILS_DIR = "target/utils";
	public static final String OUTPUT_DIR = "target/output";
	
	public static void main(String[] args)
		throws Exception
	{
		Options options = new Options();
		options.addOption("D", "train", false, "run train mode");
		options.addOption("T", "test", false, "run test mode incl. evaluation (post-submission scenario)");
		options.addOption("S", "submission", false, "run test mode without evaluation (submission scenario)");
		
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine cmd = parser.parse( options, args);
			
			if (cmd.hasOption("D"))
			{
				System.out.println("*** TRAIN MODE ***");
				runTrain();
			}
			if (cmd.hasOption("T"))
			{
				System.out.println("*** TEST MODE (incl. evaluation) ***");
				runTest(true);
			}
			if (cmd.hasOption("S"))
			{
				System.out.println("*** TEST MODE (without evaluation) ***");
				runTest(false);
			}
			
			if (cmd.getOptions().length == 0)
				new HelpFormatter().printHelp(Pipeline.class.getSimpleName(), options, true);
		}
		catch (ParseException e) {
			new HelpFormatter().printHelp(Pipeline.class.getSimpleName(), options);
		}
	}
	
	public static void runTrain()
		throws Exception
	{
		// Generate the features for training data
		FeatureGeneration.generateFeatures(MSRparTrain, TRAIN);
		FeatureGeneration.generateFeatures(MSRvidTrain, TRAIN);
		FeatureGeneration.generateFeatures(SMTeuroparlTrain, TRAIN);
		FeatureGeneration.generateFeatures(MSRparTest, TRAIN);
		FeatureGeneration.generateFeatures(MSRvidTest, TRAIN);
		FeatureGeneration.generateFeatures(SMTeuroparlTest, TRAIN);
		FeatureGeneration.generateFeatures(OnWNTest, TRAIN);
		FeatureGeneration.generateFeatures(SMTnewsTest, TRAIN);
		
		// Packages features in arff files
		Features2Arff.toArffFile(TRAIN, MSRparTrain, MSRvidTrain, SMTeuroparlTrain, MSRparTest,
				MSRvidTest, SMTeuroparlTest, OnWNTest, SMTnewsTest);

		// Run the classifier
		Evaluator.runLinearRegressionCV(TRAIN, MSRparTrain, MSRvidTrain, SMTeuroparlTrain, MSRparTest,
				MSRvidTest, SMTeuroparlTest, OnWNTest, SMTnewsTest);
		
		// Evaluate
		Evaluator.runEvaluationMetric(TRAIN, PearsonWeightedMean, MSRparTrain, MSRvidTrain, SMTeuroparlTrain, MSRparTest,
				MSRvidTest, SMTeuroparlTest, OnWNTest, SMTnewsTest);
	}

	public static void runTest(boolean runEvaluation)
		throws Exception
	{
		// Generate the features for training data
		FeatureGeneration.generateFeatures(MSRparTrain, TRAIN);
		FeatureGeneration.generateFeatures(MSRvidTrain, TRAIN);
		FeatureGeneration.generateFeatures(SMTeuroparlTrain, TRAIN);
		FeatureGeneration.generateFeatures(MSRparTest, TRAIN);
		FeatureGeneration.generateFeatures(MSRvidTest, TRAIN);
		FeatureGeneration.generateFeatures(SMTeuroparlTest, TRAIN);
		FeatureGeneration.generateFeatures(OnWNTest, TRAIN);
		FeatureGeneration.generateFeatures(SMTnewsTest, TRAIN);
		
		// Generate the features for test data
		FeatureGeneration.generateFeatures(FNWN, TEST);
		FeatureGeneration.generateFeatures(headlines, TEST);
		FeatureGeneration.generateFeatures(OnWN, TEST);
		FeatureGeneration.generateFeatures(SMT, TEST);
		
		// Concatenate all training data
		FeatureGeneration.combineFeatureSets(TRAIN, ALL, MSRparTrain, MSRvidTrain, SMTeuroparlTrain, MSRparTest,
				MSRvidTest, SMTeuroparlTest, OnWNTest, SMTnewsTest);
		
		// Package features in arff files
		Features2Arff.toArffFile(TRAIN, ALL);
		Features2Arff.toArffFile(TEST, FNWN, headlines, OnWN, SMT);

		// Run the classifer
		Evaluator.runLinearRegression(ALL, FNWN, headlines, OnWN, SMT);
		
		// Evaluate
		// Note: For submission scenario, comment the lines below, as 
		// there is no gold standard present then to compare with.
		if (runEvaluation)
		{
			Evaluator.runEvaluationMetric(TEST, PearsonWeightedMean, FNWN, headlines, OnWN, SMT);
		}
	}
}
