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
package de.tudarmstadt.ukp.similarity.experiments.coling2012;

import static de.tudarmstadt.ukp.similarity.experiments.coling2012.Pipeline.EvaluationMetric.Accuracy;
import static de.tudarmstadt.ukp.similarity.experiments.coling2012.Pipeline.EvaluationMetric.AverageF1;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DKProContext;
import de.tudarmstadt.ukp.similarity.experiments.coling2012.util.ColingUtils;
import de.tudarmstadt.ukp.similarity.experiments.coling2012.util.Evaluator;
import de.tudarmstadt.ukp.similarity.experiments.coling2012.util.Evaluator.WekaClassifier;
import de.tudarmstadt.ukp.similarity.experiments.coling2012.util.Features2Arff;
import edu.stanford.nlp.util.StringUtils;


/**
 * Full-featured experimental setup
 */
public class Pipeline
{	
	public enum Dataset
	{
		WikipediaRewriteCorpus,
		MeterCorpus,
		WebisCrowdParaphraseCorpus,
	}
	
	public enum EvaluationMetric
	{
		Accuracy,
		AverageF1
	}
	
	public static String DATASET_DIR;
	public static String GOLDSTANDARD_DIR;
	
	public static final String FEATURES_DIR = "target/features";
	public static final String MODELS_DIR = "target/models";
	public static final String UTILS_DIR = "target/utils";
	public static final String OUTPUT_DIR = "target/output";
	
	public static void main(String[] args)
		throws Exception
	{
		DATASET_DIR = DKProContext.getContext().getWorkspace().getAbsolutePath() + "/Datasets/###/ds";
		GOLDSTANDARD_DIR = DKProContext.getContext().getWorkspace().getAbsolutePath() + "/Datasets/###/gs";
		
		Options options = new Options();
		options.addOption("d", "dataset", true, "dataset to evaluate: " + StringUtils.join(Dataset.values(), ", "));
		options.addOption("c", "classifier", true, "classifier to use: " + StringUtils.join(WekaClassifier.values(), ", "));
		
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			
			if (cmd.hasOption("d") && cmd.hasOption("c"))
			{
				Dataset dataset = Dataset.valueOf(cmd.getOptionValue("d"));
				WekaClassifier wekaClassifier = WekaClassifier.valueOf(cmd.getOptionValue("c"));
				
				runCV(dataset, wekaClassifier);
			}
			else
			{
				new HelpFormatter().printHelp(Pipeline.class.getSimpleName(), options, true);
			}
		}
		catch (ParseException e) {
			new HelpFormatter().printHelp(Pipeline.class.getSimpleName(), options);
		}
	}
	
	public static void runCV(Dataset dataset, WekaClassifier wekaClassifier)
		throws Exception
	{
		// Generate the features
		FeatureGeneration.generateFeatures(dataset);
		
		// Output the ordered document IDs
		ColingUtils.generateDocumentOrder(dataset);
		
		// Packages features in arff files
		Features2Arff.toArffFile(dataset);

		// Run the classifier
		Evaluator.runClassifierCV(wekaClassifier, dataset);
		
		// Evaluate
		Evaluator.runEvaluationMetric(wekaClassifier, Accuracy, dataset);
		Evaluator.runEvaluationMetric(wekaClassifier, AverageF1, dataset);
	}
}
