package de.tudarmstadt.ukp.similarity.experiments.rte;

import static de.tudarmstadt.ukp.similarity.experiments.rte.Pipeline.Dataset.*;
import static de.tudarmstadt.ukp.similarity.experiments.rte.Pipeline.EvaluationMetric.*;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.bouncycastle.asn1.tsp.Accuracy;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DKProContext;
import de.tudarmstadt.ukp.similarity.experiments.rte.util.Evaluator;
import de.tudarmstadt.ukp.similarity.experiments.rte.util.Features2Arff;
import de.tudarmstadt.ukp.similarity.experiments.rte.util.GoldstandardCreator;


public class Pipeline
{	
	public enum Dataset
	{
		RTE1_dev,
		RTE1_dev2,
		RTE1_test,
		RTE2_dev,
		RTE2_test,
		RTE3_dev,
		RTE3_test,
		RTE4_test,
		RTE5_dev,
		RTE5_test
	}

	public enum EvaluationMetric
	{
		Accuracy
	}
	
	public static String DATASET_DIR;
	
	public static final String FEATURES_DIR = "target/features";
	public static final String GOLD_DIR = "target/gold";
	public static final String MODELS_DIR = "target/models";
	public static final String UTILS_DIR = "target/utils";
	public static final String OUTPUT_DIR = "target/output";
	
	public static void main(String[] args)
		throws Exception
	{
		DATASET_DIR = DKProContext.getContext().getWorkspace("RTE").getAbsolutePath();
		
		Options options = new Options();
		options.addOption("d", "devset", true, "run the given dev-set");
		options.addOption("t", "testset", true, "run the given test-set (also needs a dev-set");
		
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine cmd = parser.parse( options, args);
			
//			if (cmd.hasOption("d") && !cmd.hasOption("t"))
//			{
//				Dataset devset = Dataset.valueOf(cmd.getOptionValue("d"));
//				
//				System.out.println("*** " + devset.toString() + " ***");
//				
//				runDev(devset);
//			}
			if (cmd.hasOption("t") && cmd.hasOption("d"))
			{
				Dataset devset = Dataset.valueOf(cmd.getOptionValue("d"));
				Dataset testset = Dataset.valueOf(cmd.getOptionValue("t"));
				
				System.out.println("*** DEV " + devset.toString() + ", TEST " + testset.toString() + " ***");
				
				runTest(devset, testset);
			}
			
			if (cmd.getOptions().length == 0)
				new HelpFormatter().printHelp(Pipeline.class.getSimpleName(), options, true);
		}
		catch (ParseException e) {
			new HelpFormatter().printHelp(Pipeline.class.getSimpleName(), options);
		}
	}
	
//	public static void runDev(Dataset devset)
//		throws Exception
//	{
//		// Generate the features
//		FeatureGeneration.generateFeatures(devset);
//
//		// Output the gold standard
//		GoldstandardCreator.outputGoldstandard(devset);
//		
//		// Packages features in arff files
//		Features2Arff.toArffFile(devset);
//
//		// Run the classifier
//		Evaluator.runLinearRegressionCV(TRAIN, MSRpar, MSRvid, SMTeuroparl);
//		
//		// Evaluate
//		Evaluator.runEvaluationMetric(TRAIN, PearsonAll, MSRpar, MSRvid, SMTeuroparl);
//		Evaluator.runEvaluationMetric(TRAIN, PearsonMean, MSRpar, MSRvid, SMTeuroparl);
//	}

	public static void runTest(Dataset devset, Dataset testset)
		throws Exception
	{
		// Generate the features for training data
		FeatureGeneration.generateFeatures(devset);
		FeatureGeneration.generateFeatures(testset);

		// Output the gold standard
		GoldstandardCreator.outputGoldstandard(devset);
		GoldstandardCreator.outputGoldstandard(testset);

		// Packages features in arff files
		Features2Arff.toArffFile(devset);
		Features2Arff.toArffFile(testset);

		// Run the classifer
		Evaluator.runClassifier(devset, testset);

		// Evaluate
		Evaluator.runEvaluationMetric(Accuracy, testset);
	}
}
