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
package dkpro.similarity.experiments.rte;

import static dkpro.similarity.experiments.rte.Pipeline.EvaluationMetric.Accuracy;
import static dkpro.similarity.experiments.rte.Pipeline.EvaluationMetric.AveragePrecision;
import static dkpro.similarity.experiments.rte.Pipeline.EvaluationMetric.CWS;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import dkpro.similarity.algorithms.ml.ClassifierSimilarityMeasure.WekaClassifier;
import dkpro.similarity.experiments.rte.util.Evaluator;
import dkpro.similarity.experiments.rte.util.Features2Arff;
import dkpro.similarity.experiments.rte.util.GoldstandardCreator;


/**
 * Full-featured experimental setup.
 */
public class Pipeline
{	
	public enum Dataset
	{
		RTE1_dev,
		RTE1_dev2,
		RTE1_test,
		RTE2_dev,
		RTE2_test,
		RTE3_dev_2way,
		RTE3_dev_3way,
		RTE3_test_2way,
		RTE3_test_3way,
		RTE4_test_2way,
		RTE4_test_3way,
		RTE5_dev_2way,
		RTE5_dev_3way,
		RTE5_test_2way,
		RTE5_test_3way,
	}

	public enum EvaluationMetric
	{
		Accuracy,
		CWS,
		AveragePrecision
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
		DATASET_DIR = DkproContext.getContext().getWorkspace("RTE").getAbsolutePath();
		
		Options options = new Options();
		options.addOption("d", "devset", true, "run the given dev-set");
		options.addOption("t", "testset", true, "run the given test-set (also needs a dev-set");
		
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine cmd = parser.parse( options, args);
			
			if (cmd.hasOption("d") && !cmd.hasOption("t"))
			{
				Dataset devset = Dataset.valueOf(cmd.getOptionValue("d"));
				
				System.out.println("*** " + devset.toString() + " ***");
				
				runDev(devset);
			}
			if (cmd.hasOption("t") && cmd.hasOption("d"))
			{
				Dataset devset = Dataset.valueOf(cmd.getOptionValue("d"));
				Dataset testset = Dataset.valueOf(cmd.getOptionValue("t"));
				
				System.out.println("*** DEV " + devset.toString() + ", TEST " + testset.toString() + " ***");
				
				runTest(devset, testset);
			}
			
			if (cmd.getOptions().length == 0) {
                new HelpFormatter().printHelp(Pipeline.class.getSimpleName(), options, true);
            }
		}
		catch (ParseException e) {
			new HelpFormatter().printHelp(Pipeline.class.getSimpleName(), options);
		}
	}
	
	public static void runDev(Dataset devset)
		throws Exception
	{
		// Generate the features
		FeatureGeneration.generateFeatures(devset);

		// Output the gold standard
		GoldstandardCreator.outputGoldstandard(devset);
		
		// Packages features in arff files
		Features2Arff.toArffFile(devset, true);

		// Run the classifier
		Evaluator.runClassifierCV(WekaClassifier.SMO, devset);
		Evaluator.runClassifierCV(WekaClassifier.LOGISTIC, devset);
		
		// Evaluate
		Evaluator.runEvaluationMetric(Accuracy, devset);
		Evaluator.runEvaluationMetric(CWS, devset);
		Evaluator.runEvaluationMetric(AveragePrecision, devset);
	}

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
		Features2Arff.toArffFile(devset, true);
		Features2Arff.toArffFile(testset, true);

		// Run the classifer
		Evaluator.runClassifier(WekaClassifier.SMO, devset, testset);
		Evaluator.runClassifier(WekaClassifier.LOGISTIC, devset, testset);

		// Evaluate
		Evaluator.runEvaluationMetric(Accuracy, testset);
		Evaluator.runEvaluationMetric(CWS, testset);
		Evaluator.runEvaluationMetric(AveragePrecision, testset);
	}
}
