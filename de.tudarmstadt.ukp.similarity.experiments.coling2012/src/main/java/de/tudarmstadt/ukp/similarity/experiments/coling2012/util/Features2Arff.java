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
package de.tudarmstadt.ukp.similarity.experiments.coling2012.util;

import static de.tudarmstadt.ukp.similarity.experiments.coling2012.Pipeline.FEATURES_DIR;
import static de.tudarmstadt.ukp.similarity.experiments.coling2012.Pipeline.MODELS_DIR;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import de.tudarmstadt.ukp.similarity.experiments.coling2012.Pipeline.Dataset;


public class Features2Arff
{
	public static final String LF = System.getProperty("line.separator");
	
	public static void toArffFile(Dataset... datasets)
		throws IOException
	{
		for (Dataset dataset : datasets)
		{			
			toArffFile(dataset);
		}
	}
		
	@SuppressWarnings("unchecked")
	private static void toArffFile(Dataset dataset)
		throws IOException
	{
		System.out.println("Generating ARFF file");
		
		Collection<File> files = FileUtils.listFiles(
				new File(FEATURES_DIR + "/" + dataset.toString()),
				new String[] { "txt" },
				true); 
		
		String arffString = toArffString(dataset, files);
		
		FileUtils.writeStringToFile(
				new File(MODELS_DIR + "/" + dataset.toString() + ".arff"),
				arffString);
		
		System.out.println(" - done");
	}
	
	@SuppressWarnings("unchecked")
	private static String toArffString(Dataset dataset, Collection<File> csvFiles)
		throws IOException
	{
		// Create the Arff header
		StringBuilder arff = new StringBuilder();
		arff.append("@relation temp-relation" + LF);
		arff.append(LF);
		
		// Init data object
		Map<Integer,List<String>> data = new HashMap<Integer,List<String>>();
		
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
					Double value = Double.parseDouble(line);	// There's just the score on the line, nothing else.
					
					// Get doc object in data list
					List<String> docObj;
					if (data.containsKey(doc))
						docObj = data.get(doc);
					else
						docObj = new ArrayList<String>();
					
					// Put data
					docObj.add(value.toString());
					data.put(doc, docObj);
				}
			}
		}
		
		// Read gold standard
		List<String> lines = ColingUtils.readGoldstandard(dataset);
		
		// Get a list of all classes (as they differ from dataset to dataset
		Set<String> allClasses = new HashSet<String>();
		for (String line: lines)
		{
			allClasses.add(line);
		}
		
		// Add gold attribute to attribute list in header
		arff.append("@attribute gold { " + StringUtils.join(allClasses, ", ") + " }" + LF);
		
		// Add gold similarity score 
		for (int doc = 1; doc <= lines.size(); doc++)
		{	
			String value = lines.get(doc - 1);				
			
			List<String> docObj = data.get(doc);
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
