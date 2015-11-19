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
package dkpro.similarity.experiments.sts2013.util;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import dkpro.similarity.experiments.sts2013.Pipeline.Dataset;
import dkpro.similarity.experiments.sts2013.Pipeline.Mode;

import static dkpro.similarity.experiments.sts2013.Pipeline.FEATURES_DIR;
import static dkpro.similarity.experiments.sts2013.Pipeline.GOLDSTANDARD_DIR;
import static dkpro.similarity.experiments.sts2013.Pipeline.MODELS_DIR;


public class Features2Arff
{
	public static final String LF = System.getProperty("line.separator");
	
	public static void toArffFile(Mode mode, Dataset... datasets)
		throws IOException
	{
		for (Dataset dataset : datasets)
		{			
			String path = GOLDSTANDARD_DIR + "/" + mode.toString().toLowerCase() + "/STS.gs." + dataset.toString() + ".txt";
			
			PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
	        Resource res = r.getResource(path);
			
			toArffFile(mode, dataset, res.getFile());
		}
	}
		
	@SuppressWarnings("unchecked")
	private static void toArffFile(Mode mode, Dataset dataset, File goldStandard)
		throws IOException
	{
		System.out.println("Generating ARFF file");
		
		Collection<File> files = FileUtils.listFiles(
				new File(FEATURES_DIR + "/" + mode.toString().toLowerCase() + "/" + dataset.toString()),
				new String[] { "txt" },
				true); 
		
		String arffString = toArffString(files, goldStandard);
		
		FileUtils.writeStringToFile(
				new File(MODELS_DIR + "/" + mode.toString().toLowerCase() + "/" + dataset.toString() + ".arff"),
				arffString);
		
		System.out.println(" - done");
	}
	
	@SuppressWarnings("unchecked")
	private static String toArffString(Collection<File> csvFiles, File goldFile)
		throws IOException
	{
		// Create the Arff header
		StringBuilder arff = new StringBuilder();
		arff.append("@relation temp-relation" + LF);
		arff.append(LF);
		
		// Init data object
		Map<Integer,List<Double>> data = new HashMap<Integer,List<Double>>();
		
		for (File file : csvFiles)
		{			
			String feature = file.getParentFile().getName() + "/" + file.getName().substring(0, file.getName().length() - 4);
			// feature = feature.replaceAll(",", "");
			
			// Add the attribute to the Arff header
			arff.append("@attribute " + feature + " numeric" + LF);
			
			// Read data
			List<String> lines = FileUtils.readLines(file);
			for (int doc = 1; doc <= lines.size(); doc++)
			{
				String line = lines.get(doc - 1);
				
				if (line.length() > 0)	// Ignore empty lines
				{
					double value = Double.parseDouble(line);	// There's just the score on the line, nothing else.
					
					// Limit to [0;5] interval
					if (value > 5.0) value = 5.0;
					if (value < 0.0) value = 0.0;
					
					// Get doc object in data list
					List<Double> docObj;
					if (data.containsKey(doc))
						docObj = data.get(doc);
					else
						docObj = new ArrayList<Double>();
					
					// Put data
					docObj.add(value);
					data.put(doc, docObj);
				}
			}
		}
		
		// Add gold attribute to attribute list in header
		// We also need to do this for unlabeled data
		arff.append("@attribute gold real" + LF);
		
		// Add gold similarity score 
		List<String> lines;
		if (goldFile != null)
		{
			lines = FileUtils.readLines(goldFile);
		}
		else
		{
			lines = new ArrayList<String>();
			for (int i = 0; i < FileUtils.readLines(csvFiles.iterator().next()).size(); i++)
				lines.add("0.0");
		}
			
		for (int doc = 1; doc <= lines.size(); doc++)
		{	
			System.out.println(lines.get(doc - 1).length());
			System.out.println(lines.get(doc - 1));
			if (lines.get(doc - 1).length() == 0)
			{
				System.out.println("here2");
				break;
			}
			
			double value = Double.parseDouble(lines.get(doc - 1));				
			
			System.out.println(doc);
			
			List<Double> docObj = data.get(doc);
			docObj.add(value);
			data.put(doc, docObj);									
		}
		
		// Finalize header
		arff.append(LF);
		arff.append("@data" + LF);
		
		// Write data
		for (int i = 1; i <= data.keySet().size(); i++)
		{			
			String dataItem = StringUtils.join(data.get(i), ",");
			
			arff.append(dataItem + LF);
		}
		
		return arff.toString();
	}
}
