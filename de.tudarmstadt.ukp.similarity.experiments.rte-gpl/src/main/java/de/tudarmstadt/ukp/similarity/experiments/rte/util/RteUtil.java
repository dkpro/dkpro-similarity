package de.tudarmstadt.ukp.similarity.experiments.rte.util;

import java.io.IOException;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DKProContext;
import de.tudarmstadt.ukp.similarity.experiments.rte.Pipeline.Dataset;

import static de.tudarmstadt.ukp.similarity.experiments.rte.Pipeline.Dataset.*;


public class RteUtil
{
	public static String getInputFilePathForDataset(String datasetDir, Dataset dataset)
		throws IllegalArgumentException, IOException
	{				
		switch (dataset)
		{
		case RTE1_dev:
			return datasetDir + "/RTE1/Dev/dev.xml";
		case RTE1_dev2:
			return datasetDir + "/RTE1/Dev/dev2.xml";
		case RTE1_test:
			return datasetDir + "/RTE1/Test/annotated_test.xml";
		case RTE2_dev:
			return datasetDir + "/RTE2/Dev/RTE2_dev.xml";
		case RTE2_test:
			return datasetDir + "/RTE2/Test/RTE2_test.annotated.xml";
		case RTE3_dev:
			return datasetDir + "/RTE3/Dev/RTE3_pairs_dev-set-final.xml";
		case RTE3_test:
			return datasetDir + "/RTE3/Test/RTE3-TEST-GOLD.xml";
		case RTE4_test:
			return datasetDir + "/RTE4/Test/RTE4_TEST-SET_GOLD.xml";
		case RTE5_dev:
			return datasetDir + "/RTE5/Main/Dev/RTE5_MainTask_DevSet.xml";
		case RTE5_test:
			return datasetDir + "/RTE5/Main/Test/RTE5_MainTask_TestSet_Gold.xml";
		default:
			throw new IllegalArgumentException("Dataset " + dataset.toString() + " not found.");
		}
	}
}
