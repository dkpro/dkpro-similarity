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

import java.io.IOException;

import dkpro.similarity.experiments.rte.Pipeline.Dataset;


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
		case RTE3_dev_2way:
			return datasetDir + "/RTE3/Dev/RTE3_pairs_dev-set-final.xml";
		case RTE3_dev_3way:
			return datasetDir + "/RTE3/Dev/RTE3_dev_3class.xml";
		case RTE3_test_2way:
			return datasetDir + "/RTE3/Test/RTE3-TEST-GOLD.xml";
		case RTE3_test_3way:
			return datasetDir + "/RTE3/Test/RTE3_test_3class_gold.xml";
		case RTE4_test_2way:
			return datasetDir + "/RTE4/Test/RTE4_TEST-SET-GOLD-TWO-WAY.xml";
		case RTE4_test_3way:
			return datasetDir + "/RTE4/Test/RTE4_TEST-SET_GOLD.xml";
		case RTE5_dev_2way:
			return datasetDir + "/RTE5/Main/Dev/RTE5_MainTask_DevSet-2Way.xml";
		case RTE5_test_2way:
			return datasetDir + "/RTE5/Main/Test/RTE5_MainTask_TestSet_Gold_2way.ascii.xml";
		case RTE5_dev_3way:
			return datasetDir + "/RTE5/Main/Dev/RTE5_MainTask_DevSet.xml";
		case RTE5_test_3way:
			return datasetDir + "/RTE5/Main/Test/RTE5_MainTask_TestSet_Gold.xml";
		default:
			throw new IllegalArgumentException("Dataset " + dataset.toString() + " not found.");
		}
	}
	
	public static boolean hasTwoSetsOfClassifications(Dataset dataset)
	{
		switch (dataset)
		{
			case RTE1_dev:
			case RTE1_dev2:
			case RTE1_test:
			case RTE2_dev:
			case RTE2_test:
				return false;
			default:
				return true;
		}
	}
	
	public static boolean hasThreeWayClassification(Dataset dataset)
	{
		return dataset.toString().endsWith("_3way");
	}
	
	public static String getCommonDatasetName(Dataset dataset)
	{
		if (RteUtil.hasTwoSetsOfClassifications(dataset))
		{
			int endIndex = Math.max(dataset.toString().indexOf("_2way"), dataset.toString().indexOf("_3way"));
			
			return dataset.toString().substring(0, endIndex);
		}
		else
		{
			return dataset.toString();
		}
	}
}
