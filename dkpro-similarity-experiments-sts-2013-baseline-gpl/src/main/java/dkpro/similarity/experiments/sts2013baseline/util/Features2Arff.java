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
package dkpro.similarity.experiments.sts2013baseline.util;

import static dkpro.similarity.experiments.sts2013baseline.Pipeline.FEATURES_DIR;
import static dkpro.similarity.experiments.sts2013baseline.Pipeline.GOLDSTANDARD_DIR;
import static dkpro.similarity.experiments.sts2013baseline.Pipeline.MODELS_DIR;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import dkpro.similarity.experiments.sts2013baseline.Pipeline.Dataset;
import dkpro.similarity.experiments.sts2013baseline.Pipeline.Mode;


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
			
			toArffFile(mode, dataset, res.getInputStream());
		}
	}
		
	private static void toArffFile(Mode mode, Dataset dataset, InputStream goldStandardInputStream)
		throws IOException
	{
		System.out.println("Generating ARFF file");
        File outFile = new File(MODELS_DIR + "/" + mode.toString().toLowerCase() + "/" + dataset.toString() + ".arff");
//        System.out.println(outFile.getAbsolutePath());
		
		Collection<File> files = FileUtils.listFiles(
				new File(FEATURES_DIR + "/" + mode.toString().toLowerCase() + "/" + dataset.toString()),
				new String[] { "txt" },
				true); 
		
		String arffString = toArffString(files, goldStandardInputStream);
		
		FileUtils.writeStringToFile(outFile, arffString);
		
		System.out.println(" - done");
	}
	
	private static String toArffString(Collection<File> csvFiles, InputStream goldStandardInputStream)
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
			
			if (lines.size() == 0) {
			    System.err.println("Empty feature file for " + feature + ". Experiment will probably fail.");
			}
			
			for (int doc = 1; doc <= lines.size(); doc++)
			{
				String line = lines.get(doc - 1);
				
				if (line.length() > 0)	// Ignore empty lines
				{
					double value = Double.parseDouble(line);	// There's just the score on the line, nothing else.
					
					// Limit to [0;5] interval
					if (value > 5.0) {
                        value = 5.0;
                    }
					if (value < 0.0) {
                        value = 0.0;
                    }
					
					// Get doc object in data list
					List<Double> docObj;
					if (data.containsKey(doc)) {
                        docObj = data.get(doc);
                    }
                    else {
                        docObj = new ArrayList<Double>();
                    }
					
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
		List<String> lines = new ArrayList<String>();
		if (goldStandardInputStream != null)
		{
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(goldStandardInputStream, "UTF-8"));
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
		}
		else
		{
			for (int i = 0; i < FileUtils.readLines(csvFiles.iterator().next()).size(); i++) {
                lines.add("0.0");
            }
		}
			
		for (int doc = 1; doc <= lines.size(); doc++)
		{	
//			System.out.println(lines.get(doc - 1).length());
//			System.out.println(lines.get(doc - 1));
			if (lines.get(doc - 1).length() == 0)
			{
				System.out.println("here2");
				break;
			}
			
			double value = Double.parseDouble(lines.get(doc - 1));				
			
//			System.out.println(doc);
			
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
