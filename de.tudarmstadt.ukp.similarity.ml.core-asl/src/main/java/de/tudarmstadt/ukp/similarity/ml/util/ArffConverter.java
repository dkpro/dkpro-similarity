package de.tudarmstadt.ukp.similarity.ml.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;


public class ArffConverter
{	
	public static final String LF = System.getProperty("line.separator");
	
	public enum GoldScoreTransformation
	{
		CONTINUOUSLY_0_TO_5,
		BINARY_0_5,
		STEPWISE_0_1_2_3_4_5,
	}
	
	public static String toArffString(Collection<File> csvFiles, File goldFile, GoldScoreTransformation evalMode)
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
			String feature = file.getParentFile().getName() + "_" + file.getName().substring(0, file.getName().length() - 4);
			feature = feature.replaceAll(",", "");
			
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
			double value = Double.parseDouble(lines.get(doc - 1));				
			
			List<Double> docObj = data.get(doc);
			docObj.add(value);
			data.put(doc, docObj);									
		}
					
		// Add a modified gold score for non-standard evaluation modes
		if (evalMode != GoldScoreTransformation.CONTINUOUSLY_0_TO_5)
		{
			lines = FileUtils.readLines(goldFile);
			for (int doc = 1; doc <= lines.size(); doc++)
			{					
				double value = Double.parseDouble(lines.get(doc - 1));
				
				switch (evalMode)
				{
					case BINARY_0_5:
						value = (value < 2.5) ? 0 : 5;
						break;
					case STEPWISE_0_1_2_3_4_5:
						if (value >= 4.5) 		value = 5;
						else if (value >= 3.5) 	value = 4;
						else if (value >= 2.5) 	value = 3;
						else if (value >= 1.5) 	value = 2;
						else if (value >= 0.5) 	value = 1;
						else 				 	value = 0;
						break;
				}
				
				List<Double> docObj = data.get(doc);
				docObj.add(value);
				data.put(doc, docObj);
			}
			
			// Add modified gold attribute to attribute list in header
			switch (evalMode)
			{
				case BINARY_0_5:
					arff.append("@attribute mgold { 0, 5 }" + LF);
					break;
				case STEPWISE_0_1_2_3_4_5:
					arff.append("@attribute mgold { 0, 1, 2, 3, 4, 5 }" + LF);
					break;
			}			
		}
		
		// Finalize header
		arff.append(LF);
		arff.append("@data" + LF);
		
		// Write data
		for (int i = 1; i <= data.keySet().size(); i++)
		{			
			String dataItem = StringUtils.join(data.get(i), ",");
			
			// Remove the double representation ".0" from nominal classes
			if (evalMode != GoldScoreTransformation.CONTINUOUSLY_0_TO_5)
				dataItem = dataItem.substring(0, dataItem.length() - 2);
			
			arff.append(dataItem + LF);
		}
		
		return arff.toString();
	}
	
//	public void lala()
//	{
//		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//		Resource[] ress = resolver.getResources("classpath*:/feature/**/*.txt");
//		ress[0].getURL();
//	}
}
